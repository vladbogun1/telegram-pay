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

## Документация

- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [TOOLING.md](./TOOLING.md)

## Лицензия

Проект предназначен для локальной модификации с соблюдением прав владельцев контента. Не распространяйте модифицированные ассеты.
