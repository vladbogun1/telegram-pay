# Tooling Guide

## Обязательные утилиты

| Tool | Назначение | Ссылка |
| --- | --- | --- |
| SDDEUnpacker | Распаковка `.big/.bix` | https://steamcommunity.com/sharedfiles/filedetails/?id=3438844153 |
| SDDE Text Tool | Экспорт/импорт `.bin` | https://steamcommunity.com/sharedfiles/filedetails/?id=3438844153 |
| FileRedirector | Безопасная подмена без репака `.big` | https://github.com/SDmodding/FileRedirector |
| wwiseutil | Работа с `.pck` и `.wem` | https://github.com/hpxro7/wwiseutil |
| WwiseConsole.exe | Конвертация wav→wem | https://www.audiokinetic.com/en/library/edge/?id=bankscommandline.html&source=SDK |
| ffmpeg | Конвертация аудио | https://ffmpeg.org |

## Опциональные утилиты

| Tool | Назначение | Ссылка |
| --- | --- | --- |
| sound2wem | Обёртка над ffmpeg + WwiseConsole | https://github.com/EternalLeo/sound2wem |

## Настройка путей

1. Установите инструменты локально.
2. Откройте UI → **Tools Setup**.
3. Укажите пути (можно абсолютные, можно через переменные окружения).

## Примечания

- **FileRedirector**: после установки появится папка `RedirectorData` в корне игры. Файлы нужно класть с сохранением относительных путей.
- **WwiseConsole**: чаще всего путь `%WWISEROOT%\Authoring\x64\Release\bin\WwiseConsole.exe`.
- **SDDE Text Tool**: экспорт/импорт `.bin` в JSON нужен для пакетного редактирования.
