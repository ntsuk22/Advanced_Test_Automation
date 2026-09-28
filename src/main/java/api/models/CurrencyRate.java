package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * One element of {@code rates} in GET /exchangeRates/commercialList.
 * Rates are BigDecimal so values such as 2.5530 are compared exactly.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRate {

    private String iso;
    private String name;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private BigDecimal officialCourse;
    /** Units the rate is quoted for: 1 for USD, 10 for CNY, 100 for JPY. */
    private BigDecimal weight;
    private BigDecimal diff;

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(BigDecimal buyRate) {
        this.buyRate = buyRate;
    }

    public BigDecimal getSellRate() {
        return sellRate;
    }

    public void setSellRate(BigDecimal sellRate) {
        this.sellRate = sellRate;
    }

    public BigDecimal getOfficialCourse() {
        return officialCourse;
    }

    public void setOfficialCourse(BigDecimal officialCourse) {
        this.officialCourse = officialCourse;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getDiff() {
        return diff;
    }

    public void setDiff(BigDecimal diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        return iso + " (" + name + ") buy=" + buyRate + " sell=" + sellRate + " official=" + officialCourse + " weight=" + weight;
    }
}
