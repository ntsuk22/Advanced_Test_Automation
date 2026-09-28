CREATE TABLE IF NOT EXISTS currency_conversion
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(3)     NOT NULL,
    currency_name VARCHAR(100)   NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    active        BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_currency_amount UNIQUE (currency_code, amount)
);
