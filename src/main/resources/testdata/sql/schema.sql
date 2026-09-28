-- Test data for SCRUM-T33 (currency converter). Executed on every connection start, see mybatis-config.xml.
CREATE TABLE IF NOT EXISTS currency_conversion
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(3)     NOT NULL,
    -- Localized row label on the Georgian exchange-rates page; only currencies quoted per 1 unit are valid here
    currency_name VARCHAR(100)   NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    active        BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_currency_amount UNIQUE (currency_code, amount)
);
