import io
import logging
import math
import os
import subprocess
import tempfile
import time
import wave
from pathlib import Path

import edge_tts
from fastapi import FastAPI
from fastapi.responses import Response
from gtts import gTTS
from pydantic import BaseModel
from pydub import AudioSegment

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ai-tts")

app = FastAPI(title="SDDE NeuroDub TTS")


class TtsRequest(BaseModel):
    text: str
    voice: str = "neutral_female"


EDGE_VOICE_MAP = {
    "neutral_female": "ru-RU-SvetlanaNeural",
    "neutral_male": "ru-RU-DmitryNeural",
    "female_soft": "ru-RU-DariyaNeural",
    "male_deep": "ru-RU-DmitryNeural",
}

ESPEAK_VOICE_MAP = {
    "neutral_female": "ru+f3",
    "neutral_male": "ru+m3",
    "female_soft": "ru+f2",
    "male_deep": "ru+m4",
}

EDGE_DISABLED_UNTIL = 0.0
EDGE_FAIL_COOLDOWN_SEC = int(os.getenv("EDGE_TTS_COOLDOWN_SEC", "600"))


def _normalize_text(text: str) -> str:
    normalized = (text or "").strip()
    return normalized or "Пустой текст для предпросмотра"


def _post_process_to_wav(src_path: Path, src_format: str, out_path: Path) -> bytes:
    segment = AudioSegment.from_file(str(src_path), format=src_format)
    segment = segment.normalize(headroom=1.0)
    segment = segment.set_frame_rate(24000).set_channels(1)
    segment.export(str(out_path), format="wav")
    return out_path.read_bytes()


def _fallback_wave(text: str, voice: str) -> bytes:
    sample_rate = 22050
    duration_seconds = min(8.0, max(1.2, len(text.strip()) / 10.0))
    base_freq = 220.0 if "male" in voice else 320.0
    frames = int(sample_rate * duration_seconds)

    buffer = io.BytesIO()
    with wave.open(buffer, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        for i in range(frames):
            mod = 1 + 0.25 * math.sin(2 * math.pi * 3.0 * i / sample_rate)
            value = int(12000 * math.sin(2 * math.pi * base_freq * mod * i / sample_rate))
            wf.writeframesraw(value.to_bytes(2, byteorder="little", signed=True))
    return buffer.getvalue()


async def _generate_edge_wav(text: str, voice_profile: str) -> bytes:
    edge_voice = EDGE_VOICE_MAP.get(voice_profile, "ru-RU-SvetlanaNeural")
    rate = os.getenv("EDGE_TTS_RATE", "+0%")
    pitch = os.getenv("EDGE_TTS_PITCH", "+0Hz")

    with tempfile.TemporaryDirectory(prefix="tts-edge-") as tmp_dir:
        mp3_path = Path(tmp_dir) / "preview.mp3"
        wav_path = Path(tmp_dir) / "preview.wav"

        communicate = edge_tts.Communicate(text=text, voice=edge_voice, rate=rate, pitch=pitch)
        await communicate.save(str(mp3_path))
        audio = _post_process_to_wav(mp3_path, "mp3", wav_path)
        log.info("Generated TTS preview via edge-tts: voice=%s chars=%s bytes=%s", edge_voice, len(text), len(audio))
        return audio


def _generate_gtts_wav(text: str) -> bytes:
    with tempfile.TemporaryDirectory(prefix="tts-gtts-") as tmp_dir:
        mp3_path = Path(tmp_dir) / "preview.mp3"
        wav_path = Path(tmp_dir) / "preview.wav"
        gTTS(text=text, lang="ru", slow=False).save(str(mp3_path))
        audio = _post_process_to_wav(mp3_path, "mp3", wav_path)
        log.info("Generated TTS preview via gTTS: chars=%s bytes=%s", len(text), len(audio))
        return audio


def _generate_espeak_wav(text: str, voice_profile: str) -> bytes:
    voice = ESPEAK_VOICE_MAP.get(voice_profile, "ru+f3")
    speed = os.getenv("ESPEAK_SPEED", "155")

    with tempfile.TemporaryDirectory(prefix="tts-espeak-") as tmp_dir:
        out_path = Path(tmp_dir) / "preview.wav"
        normalized_path = Path(tmp_dir) / "preview-normalized.wav"
        cmd = ["espeak-ng", "-v", voice, "-s", speed, "-w", str(out_path), text]
        subprocess.run(cmd, check=True, capture_output=True, text=True)
        audio = _post_process_to_wav(out_path, "wav", normalized_path)
        log.info("Generated TTS preview via espeak-ng fallback: voice=%s chars=%s bytes=%s", voice, len(text), len(audio))
        return audio


@app.post("/tts/preview")
async def tts_preview(request: TtsRequest):
    global EDGE_DISABLED_UNTIL

    text = _normalize_text(request.text)
    voice_profile = request.voice or "neutral_female"

    now = time.time()
    if now >= EDGE_DISABLED_UNTIL:
        try:
            return Response(content=await _generate_edge_wav(text, voice_profile), media_type="audio/wav")
        except Exception as edge_exc:
            message = str(edge_exc)
            if "403" in message or "Invalid response status" in message:
                EDGE_DISABLED_UNTIL = now + EDGE_FAIL_COOLDOWN_SEC
                log.warning("edge-tts is returning 403; disabling for %ss and switching to next fallback", EDGE_FAIL_COOLDOWN_SEC)
            else:
                log.warning("edge-tts failed: %s", message)
    else:
        remaining = int(EDGE_DISABLED_UNTIL - now)
        log.info("edge-tts temporarily disabled due to previous failures (%ss left)", remaining)

    try:
        return Response(content=_generate_gtts_wav(text), media_type="audio/wav")
    except Exception as gtts_exc:
        log.warning("gTTS failed, switching to espeak-ng: %s", gtts_exc)

    try:
        return Response(content=_generate_espeak_wav(text, voice_profile), media_type="audio/wav")
    except Exception as espeak_exc:
        log.warning("espeak-ng failed, switching to synthetic fallback: %s", espeak_exc)

    return Response(content=_fallback_wave(text, voice_profile), media_type="audio/wav")
