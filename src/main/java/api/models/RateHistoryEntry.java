package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * One element of the POST /commercialRates/list response the rate-history dialog is built from, e.g.
 * {"startDate": "2026-09-28T22:56:00.09", "ccyFrom": "EUR", "ccyTo": "GEL", "multiplier": 1, "buy": 2.889, "sell": 3.034}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RateHistoryEntry {

    /** Local date-time (no offset) from which this rate was valid. */
    private String startDate;
    private String ccyFrom;
    private String ccyTo;
    private BigDecimal multiplier;
    private BigDecimal buy;
    private BigDecimal sell;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCcyFrom() {
        return ccyFrom;
    }

    public void setCcyFrom(String ccyFrom) {
        this.ccyFrom = ccyFrom;
    }

    public String getCcyTo() {
        return ccyTo;
    }

    public void setCcyTo(String ccyTo) {
        this.ccyTo = ccyTo;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    @Override
    public String toString() {
        return startDate + " " + ccyFrom + "/" + ccyTo + " buy=" + buy + " sell=" + sell;
    }
}
