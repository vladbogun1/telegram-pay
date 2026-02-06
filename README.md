# telegram-pay

Production-ready multi-tenant Telegram monetization platform for paid subscriptions, donations, and access management.

## Features
- Multi-tenant creators with API keys.
- Multi-bot hosting with secure webhook secret validation.
- Telegram Stars (XTR) payment flow with pre-checkout and successful payment handling.
- Optional Wallet Pay (@Wallet) integration.
- Entitlements with invite links, expirations, and renewal reminders.
- Creator dashboard endpoints with revenue summaries.
- AES-GCM encryption for bot tokens and Wallet Pay API keys.
- Audit logging for critical events.

## Tech Stack
- Java 21, Spring Boot 4.x
- MySQL 8 + Flyway
- Spring Web + Validation + Data JPA
- JUnit 5, Testcontainers, Mockito

## Local Development
### Prerequisites
- Docker + Docker Compose
- Java 21

### Generate a master key
```bash
python - <<'PY'
import os, base64
print(base64.b64encode(os.urandom(32)).decode())
PY
```

### Run with Docker Compose
```bash
export TELEGRAM_PAY_MASTER_KEY=REPLACE_WITH_BASE64_KEY
export PUBLIC_BASE_URL=https://your-public-host
mvn -q -DskipTests package
docker compose up --build
```

## API Overview
All creator endpoints require `X-Api-Key` header, except onboarding.

### Create a creator
```bash
curl -X POST http://localhost:8080/api/creators \
  -H 'Content-Type: application/json' \
  -d '{"name":"Creator One"}'
```

### Register a bot
```bash
curl -X POST http://localhost:8080/api/bots \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"name":"Main Bot","botToken":"123:ABC"}'
```

### Register a chat
```bash
curl -X POST http://localhost:8080/api/chats \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"botInstanceId":1,"telegramChatId":"-100123456","title":"Premium Chat","type":"supergroup"}'
```

### Create a product
```bash
curl -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"chatId":1,"name":"Monthly Access","priceStars":100,"durationDays":30,"recurringMonthly":true}'
```

### Create a Stars invoice
```bash
curl -X POST http://localhost:8080/api/orders/stars \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"productId":1,"botInstanceId":1,"telegramUserId":"123456"}'
```

### Wallet Pay configuration
```bash
curl -X POST http://localhost:8080/api/walletpay/config \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"storeApiKey":"<wallet-pay-key>","returnUrl":"https://your.site/return","failReturnUrl":"https://your.site/fail"}'
```

### Create Wallet Pay order
```bash
curl -X POST http://localhost:8080/api/orders/walletpay \
  -H 'Content-Type: application/json' \
  -H 'X-Api-Key: <creator-api-key>' \
  -d '{"productId":1,"telegramUserId":"123456"}'
```

## Telegram Webhooks
Set webhook per bot instance:
```
POST https://api.telegram.org/bot<token>/setWebhook
{
  "url": "https://your-domain/webhooks/telegram/{botInstanceId}",
  "secret_token": "<webhookSecretToken>"
}
```

Updates are sent to:
```
POST /webhooks/telegram/{botInstanceId}
Header: X-Telegram-Bot-Api-Secret-Token: <webhookSecretToken>
```

## Wallet Pay Webhooks
```
POST /webhooks/walletpay/{creatorId}
```
Wallet Pay may deliver duplicate webhooks; the handler is idempotent.

## Notes
- Stars invoices must use `provider_token=""`, `currency="XTR"`, and exactly one price item.
- Access is granted only after `successful_payment`.
- In production, webhooks must be HTTPS and Telegram must reach your `PUBLIC_BASE_URL`.

## Tests
```bash
mvn test
```
