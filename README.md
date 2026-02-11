# SDDE NeuroDub Studio

SDDE NeuroDub Studio — локальная студия дубляжа для **Sleeping Dogs: Definitive Edition (Steam)**. Проект строит безопасный пайплайн: извлекает субтитры, помогает сопоставить реплики и аудио, генерирует русскую озвучку и применяет патч **без перепаковки .big** через FileRedirector.

> ⚖️ **Этика/право**
> - Проект **не содержит ассетов игры**.
> - По умолчанию используются **нейтральные TTS‑голоса** (без копирования актёров). Пользователь может подключить свой голос **только при наличии прав**.
> - Перед изменениями создаётся бэкап; доступен откат.

## Быстрый старт (одной командой)

```bash
./scripts/run-local.sh
```

Для Windows:

```powershell
./scripts/run-local.ps1
```

После старта:
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

> ⚠️ Важно для Docker: backend видит только примонтированные пути. Если игра установлена на хосте, добавьте volume в `docker-compose.yml` или запускайте backend без контейнера, иначе scan вернёт предупреждение о недоступном пути.

### Пример монтирования папки игры (Windows)

1. Откройте `docker-compose.yml` и добавьте volume для backend:

```yaml
services:
  backend:
    volumes:
      - ./data:/data
      - "D:/STEAM/steamapps/common/SleepingDogsDefinitiveEdition:/games/SDDE:ro"
```

2. В UI укажите путь **внутри контейнера**: `/games/SDDE`.

### Как подключить внешние инструменты в Docker

1. Установите инструменты на хост‑машине (Windows).
2. Смонтируйте их папку в контейнер (пример):

```yaml
services:
  backend:
    volumes:
      - ./data:/data
      - "D:/SDDE-Tools:/tools"
```

3. В UI → **Tools Setup** укажите пути внутри контейнера, например:
   - `C:\\` → `/tools/...` (в Docker)  
   - `ffmpeg`: `/usr/bin/ffmpeg` (уже установлен в контейнере)

### Автоустановка в Docker (что возможно)

- **ffmpeg** устанавливается автоматически в контейнере backend.
- **wwiseutil** можно собрать автоматически в `./tools/bin`:

```bash
./scripts/bootstrap-tools.sh
```

Для Windows PowerShell:

```powershell
./scripts/bootstrap-tools.ps1
```

> ⚠️ SDDEUnpacker, SDDE Text Tool, FileRedirector и WwiseConsole — Windows‑утилиты и/или требуют лицензионной установки. Их нельзя легально/корректно автоустанавливать в Linux‑контейнер; используйте монтирование и настройку путей в UI.

## Запуск через IntelliJ IDEA (Windows-first)

> Цель: комфортный дебаг из IDE, без обязательного запуска backend/frontend в Docker.

### 1) Что запускать локально из IDE
- **Backend (Spring Boot)**: запускайте `NeuroDubApplication` из IntelliJ.
- **Frontend (Vite/React)**: запускайте `npm run dev` в папке `frontend` (терминал IntelliJ).

### 2) Что оставить в Docker
- **AI сервисы** (tts/translate/asr) можно оставить в Docker:
```bash
docker compose up -d ai-tts ai-translate ai-asr
```
- Отдельную серверную СУБД ставить не нужно: backend использует **SQLite файл** (`./data/sdde-neurodub.db`).

### 3) Конфиг для backend в IDE
В `application.yml` уже выставлены IDE-friendly дефолты:
- `TTS_SERVICE_URL` -> `http://localhost:8001`
- `TRANSLATE_SERVICE_URL` -> `http://localhost:8000`
- `WHISPER_SERVICE_URL` -> `http://localhost:8002`

То есть при запуске backend из IntelliJ сервисы на localhost подхватятся автоматически.

### 4) Рекомендуемый порядок старта (Windows)
1. `docker compose up -d ai-tts ai-translate ai-asr`
2. IntelliJ: Run `NeuroDubApplication`
3. IntelliJ Terminal: `cd frontend && npm install && npm run dev`
4. Открыть UI: `http://localhost:5173`

## Полная установка тулов на Windows (скачать + установить)

Ниже минимальный набор для реального SDDE-пайплайна. Бинарники в git не кладём — только локальная установка и пути в Tools Setup.

1. **FFmpeg** (обязателен)  
   - Сайт: https://ffmpeg.org/download.html  
   - Windows builds: https://www.gyan.dev/ffmpeg/builds/  
   - Установка: распаковать, добавить `...\ffmpeg\bin` в PATH.

2. **Wwise Authoring + WwiseConsole.exe** (обязателен для WAV->WEM)  
   - Audiokinetic Launcher: https://www.audiokinetic.com/download/  
   - Документация CLI: https://www.audiokinetic.com/en/library/edge/?id=bankscommandline.html&source=SDK  
   - После установки путь обычно: `%WWISEROOT%\Authoring\x64\Release\bin\WwiseConsole.exe`.

3. **wwiseutil** (обязателен для replace/unpack `.pck/.wem`)  
   - Репозиторий: https://github.com/hpxro7/wwiseutil  
   - Для Docker-варианта можно собрать `./scripts/bootstrap-tools.ps1`, либо скачать/собрать вручную и указать путь в UI.

4. **SDDEUnpacker** (для распаковки `.big/.bix`)  
   - См. гайд SDDE: https://steamcommunity.com/sharedfiles/filedetails/?id=3438844153  
   - Установить локально, путь к exe указать в Tools Setup.

5. **SDDE Text Tool by Delutto** (экспорт/импорт `.bin`)  
   - См. гайд SDDE: https://steamcommunity.com/sharedfiles/filedetails/?id=3438844153  
   - Установить локально, путь к exe указать в Tools Setup.

6. **FileRedirector** (рекомендовано для safe patch без repack)  
   - GitHub: https://github.com/SDmodding/FileRedirector  
   - После установки появится `RedirectorData`, в него кладутся изменённые файлы по относительным путям.

7. **sound2wem** (опционально)  
   - GitHub: https://github.com/EternalLeo/sound2wem  
   - Можно использовать как оболочку для конвертации в WEM.

### Пути в Tools Setup (пример Windows)
- `ffmpegPath`: `C:\Tools\ffmpeg\bin\ffmpeg.exe`
- `wwiseConsolePath`: `C:\Program Files\Audiokinetic\Wwise ...\WwiseConsole.exe`
- `wwiseUtilPath`: `C:\Tools\wwiseutil\wwiseutil.exe`
- `sddeUnpackerPath`: `C:\Tools\SDDEUnpacker\...exe`
- `sddeTextToolPath`: `C:\Tools\SDDETextTool\...exe`
- `fileRedirectorPath`: путь до корня мода/Redirector

## Требования

- **Docker Desktop** (Windows 11, WSL2)
- Локальная установка SDDE (Steam)
- **Внешние инструменты (устанавливаются отдельно):**
  - SDDEUnpacker, SDDE Text Tool (экспорт/импорт `.bin`). [guide](https://steamcommunity.com/sharedfiles/filedetails/?id=3438844153)
  - FileRedirector. [github.com/SDmodding/FileRedirector](https://github.com/SDmodding/FileRedirector)
  - wwiseutil (работа с `.pck`/`.wem`). [github.com/hpxro7/wwiseutil](https://github.com/hpxro7/wwiseutil)
  - WwiseConsole.exe (официальный CLI). [Audiokinetic docs](https://www.audiokinetic.com/en/library/edge/?id=bankscommandline.html&source=SDK)
  - ffmpeg (для конвертации). https://ffmpeg.org/
  - sound2wem (опционально). [github.com/EternalLeo/sound2wem](https://github.com/EternalLeo/sound2wem)

> ⚠️ Ограничение Docker: внутри контейнера мы **автоматически ставим только ffmpeg** (open‑source). Остальные инструменты (SDDEUnpacker, SDDE Text Tool, FileRedirector, wwiseutil, WwiseConsole) требуют ручной установки и настройки путей из‑за лицензий/прав и особенностей Windows‑утилит.

## Что умеет MVP

- Сканировать папку игры и проверять ключевые файлы.
- Извлекать **примерный набор субтитров** (20 строк) для проверки end‑to‑end.
- Делать авто‑мэппинг (черновой) и manual mapping.
- Генерировать TTS‑preview (локальный FastAPI сервис).
- Формировать PatchManifest.json и применять безопасные redirector‑патчи.

> Полноценная интеграция внешних инструментов (SDDEUnpacker, SDDE Text Tool, wwiseutil, WwiseConsole, ffmpeg) подключается через Tools Setup (указание путей) и описана в `TOOLING.md`.

## Пайплайн (кратко)

1. **Select Game Folder + Scan** — проверка `Global.big`, `UI.big`, аудио `.pck`.
2. **Tools Setup** — укажите пути к утилитам.
3. **Subtitles** — экспорт из 6 файлов `.bin`:
   - EN_Front-End
   - EN_Global
   - EN_Gameplay
   - EN_GameplayAct1
   - EN_GameplayAct2
   - EN_GameplayAct3
4. **Mapping** — Auto (ASR+align) + Review или Manual.
5. **Voice Settings** — выбор голоса и параметров.
6. **Build Patch** — dry‑run, оценка бэкапа.
7. **Apply Patch** — FileRedirector‑патч и замена `.wem`.
8. **Rollback** — откат из бэкапа, удаление redirector файлов.

## Как найти папку игры Steam

- Steam → Библиотека → Sleeping Dogs: Definitive Edition → Управление → Обзор локальных файлов.
- Пример пути: `C:\Program Files (x86)\Steam\steamapps\common\SleepingDogsDefinitiveEdition`.

## Включение/отключение патча

- **Включить:** примените patch → файлы окажутся в `RedirectorData` (FileRedirector).
- **Отключить:** кнопка **Rollback** удалит redirector файлы и восстановит `.pck` из бэкапа.

## Troubleshooting

- **WwiseConsole.exe not found** — установите Wwise Authoring через Audiokinetic Launcher, укажите путь `%WWISEROOT%\Authoring\x64\Release\bin\WwiseConsole.exe`.
- **ffmpeg not found** — установите ffmpeg и добавьте в PATH или укажите в Tools Setup.
- **Access denied** — запускайте Docker Desktop с правами администратора или разместите игру вне `Program Files`.
- **Steam update detected** — если checksum не совпадает, откат выполнит только redirector‑часть и предупредит о рисках.

## Логи (для диагностики)

- В UI добавлены два блока:
  - **Frontend Activity Log** — показывает все действия мастера и ответы/ошибки запросов.
  - **Backend Log Tail** — кнопка **Load Logs** подтягивает хвост логов backend.
- Backend пишет лог в файл:
  - `./data/sdde-neurodub/logs/application.log` (при Docker запуске)
- Также доступен API для логов:
  - `GET /api/system/logs?lines=200`

## Документация

- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [TOOLING.md](./TOOLING.md)

## Лицензия

Проект предназначен для локальной модификации с соблюдением прав владельцев контента. Не распространяйте модифицированные ассеты.
