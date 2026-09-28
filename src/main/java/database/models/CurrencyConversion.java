package database.models;

import java.math.BigDecimal;

public class CurrencyConversion {

    private Integer id;
    private String currencyCode;
    private String currencyName;
    private BigDecimal amount;
    private boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String amountAsInput() {
        return amount.stripTrailingZeros().toPlainString();
    }

    @Override
    public String toString() {
        return "#" + id + " " + currencyCode + " " + amountAsInput();
    }
}
