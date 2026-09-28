package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommercialRatesResponse {

    private List<CurrencyRate> rates;
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
