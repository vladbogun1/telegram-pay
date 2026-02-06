CREATE TABLE creators (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE bot_instances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    bot_token_encrypted VARCHAR(4096) NOT NULL,
    webhook_secret_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_bot_creator FOREIGN KEY (creator_id) REFERENCES creators(id)
);

CREATE TABLE chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    bot_instance_id BIGINT NOT NULL,
    telegram_chat_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(64) NOT NULL,
    CONSTRAINT fk_chat_creator FOREIGN KEY (creator_id) REFERENCES creators(id),
    CONSTRAINT fk_chat_bot FOREIGN KEY (bot_instance_id) REFERENCES bot_instances(id)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_stars INT NOT NULL,
    duration_days INT,
    recurring_monthly BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_product_creator FOREIGN KEY (creator_id) REFERENCES creators(id),
    CONSTRAINT fk_product_chat FOREIGN KEY (chat_id) REFERENCES chats(id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    provider VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    telegram_user_id VARCHAR(64) NOT NULL,
    amount_stars INT NOT NULL,
    external_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_orders_external_id (external_id),
    CONSTRAINT fk_order_creator FOREIGN KEY (creator_id) REFERENCES creators(id),
    CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    provider VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    telegram_payment_charge_id VARCHAR(255),
    provider_payment_charge_id VARCHAR(255),
    subscription_expiration_date TIMESTAMP,
    recurring BOOLEAN,
    first_recurring BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_payments_telegram_charge (telegram_payment_charge_id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE entitlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    telegram_user_id VARCHAR(64) NOT NULL,
    chat_id VARCHAR(255) NOT NULL,
    access_until TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    invite_link VARCHAR(1024),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_entitlement_creator FOREIGN KEY (creator_id) REFERENCES creators(id),
    CONSTRAINT fk_entitlement_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE wallet_pay_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT NOT NULL,
    store_api_key_encrypted VARCHAR(4096) NOT NULL,
    return_url VARCHAR(1024) NOT NULL,
    fail_return_url VARCHAR(1024) NOT NULL,
    UNIQUE KEY uk_wallet_creator (creator_id),
    CONSTRAINT fk_wallet_creator FOREIGN KEY (creator_id) REFERENCES creators(id)
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_id BIGINT,
    action VARCHAR(255) NOT NULL,
    details VARCHAR(4096),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_creator FOREIGN KEY (creator_id) REFERENCES creators(id)
);
