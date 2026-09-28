package api.clients;

import io.restassured.response.Response;

/**
 * Exchange-rate endpoints used by the exchange-rates page.
 * Methods return the raw Response so tests can check status and headers before deserializing.
 */
public class ExchangeRatesClient extends BaseApiClient {

    public Response getCommercialRates(String locale) {
        return request()
                .queryParam("locale", locale)
                .get("/exchangeRates/commercialList");
    }

    public Response getExchangeRate(String fromIso, String toIso) {
        return request()
                .queryParam("Iso1", fromIso)
                .queryParam("Iso2", toIso)
                .get("/exchangeRates/getExchangeRate");
    }
}
