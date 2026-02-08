from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="SDDE NeuroDub Translate")

class TranslateRequest(BaseModel):
    text: str
    source: str = "en"
    target: str = "ru"

@app.post("/translate")
def translate(request: TranslateRequest):
    return {
        "source": request.source,
        "target": request.target,
        "original": request.text,
        "translation": f"[RU] {request.text}"
    }
