# Architecture

## Overview

SDDE NeuroDub Studio состоит из четырёх слоёв:

1. **Frontend** (React + Vite) — мастер по шагам, состояние пайплайна, предпросмотр TTS.
2. **Backend** (Spring Boot 3, Java 21) — REST API, очередь задач, SQLite‑хранилище, подготовка патчей.
3. **AI Services** (FastAPI) — локальные микросервисы для translate/TTS/ASR.
4. **External Tools** — SDDEUnpacker, SDDE Text Tool, wwiseutil, WwiseConsole, ffmpeg, FileRedirector.

## Backend modules

- **API**: endpoints для сканирования установки, извлечения субтитров, мэппинга и патча.
- **Tooling**: проверка наличия утилит и их путей.
- **Patch**: формирование PatchManifest.json, редирект и откат.
- **Jobs**: SSE прогресс и статус для долгих операций.

## Data model (SQLite)

- `projects` — информация о проектах и путях к игре.
- `subtitles` — извлечённые строки.
- `mappings` — соответствие `wemId -> subtitle`.
- `tool_config` — пути к внешним инструментам.
- `jobs` — история задач.

## Patch manifest format

```json
{
  "patchId": "uuid",
  "gamePathHash": "hash",
  "toolVersions": {
    "wwiseutil": "external"
  },
  "touchedFiles": [
    {
      "path": "Audio/PCK/WEM_1.wem",
      "sha256_before": "...",
      "sha256_after": "...",
      "backupPath": "..."
    }
  ],
  "redirectorFiles": [
    {
      "relativePath": "Text/EN_Gameplay.bin",
      "sha256": "..."
    }
  ],
  "createdAt": "2024-01-01T00:00:00Z"
}
```

## Smart revert

Rollback сначала удаляет `RedirectorData`, затем проверяет контрольные суммы аудио‑файлов. Если игра обновилась в Steam и checksum не совпадает, применяется безопасный режим: откат только redirector‑части и предупреждение в UI.
