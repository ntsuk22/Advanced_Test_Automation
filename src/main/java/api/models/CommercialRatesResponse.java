package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

/**
 * Body of GET /exchangeRates/commercialList: {"rates": [ ... ], "updateDateTime": "2026-09-28T14:34:20.17+04:00"}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommercialRatesResponse {

    private List<CurrencyRate> rates;
    /** ISO-8601 with offset; kept as text and parsed by the steps so a format change fails with a clear message. */
    private String updateDateTime;

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(List<CurrencyRate> rates) {
        this.rates = rates;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public Optional<CurrencyRate> rate(String iso) {
        return rates.stream().filter(rate -> iso.equals(rate.getIso())).findFirst();
    }
}
