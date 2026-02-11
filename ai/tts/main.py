import io
import logging
import math
import subprocess
import tempfile
import wave
from pathlib import Path

from fastapi import FastAPI
from fastapi.responses import Response
from pydantic import BaseModel

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("ai-tts")

app = FastAPI(title="SDDE NeuroDub TTS")


class TtsRequest(BaseModel):
    text: str
    voice: str = "neutral_female"


VOICE_MAP = {
    "neutral_female": "ru+f3",
    "neutral_male": "ru+m3",
    "female_soft": "ru+f2",
    "male_deep": "ru+m4",
}


def _fallback_wave(text: str, voice: str) -> bytes:
    """Last-resort audio to avoid hard failures when espeak is unavailable."""
    sample_rate = 22050
    duration_seconds = min(8.0, max(1.0, len(text.strip()) / 10.0))
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


@app.post("/tts/preview")
def tts_preview(request: TtsRequest):
    text = (request.text or "").strip()
    if not text:
        text = "Пустой текст для предпросмотра"

    voice = VOICE_MAP.get(request.voice, "ru+f3")
    speed = "165"

    try:
        with tempfile.TemporaryDirectory(prefix="tts-") as tmp_dir:
            out_path = Path(tmp_dir) / "preview.wav"
            cmd = [
                "espeak-ng",
                "-v",
                voice,
                "-s",
                speed,
                "-w",
                str(out_path),
                text,
            ]
            subprocess.run(cmd, check=True, capture_output=True, text=True)
            audio = out_path.read_bytes()
            log.info("Generated TTS preview via espeak-ng: voice=%s chars=%s bytes=%s", voice, len(text), len(audio))
            return Response(content=audio, media_type="audio/wav")
    except Exception as exc:
        log.exception("espeak-ng TTS failed, falling back to synthetic waveform: %s", exc)
        return Response(content=_fallback_wave(text, request.voice), media_type="audio/wav")
