import io
import math
import wave
from fastapi import FastAPI
from fastapi.responses import Response
from pydantic import BaseModel

app = FastAPI(title="SDDE NeuroDub TTS")

class TtsRequest(BaseModel):
    text: str
    voice: str = "neutral_female"

@app.post("/tts/preview")
def tts_preview(request: TtsRequest):
    sample_rate = 22050
    duration_seconds = min(3.0, max(1.0, len(request.text) / 40.0))
    freq = 440.0 if "female" in request.voice else 330.0
    frames = int(sample_rate * duration_seconds)
    buffer = io.BytesIO()
    with wave.open(buffer, "wb") as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(sample_rate)
        for i in range(frames):
            value = int(16000 * math.sin(2 * math.pi * freq * i / sample_rate))
            wf.writeframesraw(value.to_bytes(2, byteorder="little", signed=True))
    return Response(content=buffer.getvalue(), media_type="audio/wav")
