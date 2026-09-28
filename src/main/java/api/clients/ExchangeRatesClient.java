package api.clients;

import io.restassured.response.Response;

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
