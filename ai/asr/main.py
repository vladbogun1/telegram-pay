from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="SDDE NeuroDub ASR")

class AsrRequest(BaseModel):
    audio_path: str | None = None

@app.post("/asr")
def asr(request: AsrRequest):
    return {
        "audio_path": request.audio_path,
        "language": "en",
        "transcript": "placeholder transcript for auto-mapping"
    }
