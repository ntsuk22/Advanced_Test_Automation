package tests;

import api.models.CommercialRatesResponse;
import constants.AppLocale;
import org.testng.annotations.Test;
import steps.ExchangeRatesApiSteps;
import steps.ExchangeRatesApiToUiSteps;
import steps.ExchangeRatesSteps;
import steps.HomeSteps;

public class ApiToUiConsistencyTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.KA;

    @Test(description = "SCRUM-T36 | API -> UI - exchange-rates table matches the commercial rates API")
    public void exchangeRatesTableMatchesApi() {
        // Source of truth: Rest Assured call deserialized into POJOs
        CommercialRatesResponse api = new ExchangeRatesApiSteps()
                .requestCommercialRates(LOCALE)
                .verifyEveryRateIsValid()
                .commercialRates();

        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();
        new ExchangeRatesSteps(page(), LOCALE).openFromQuickActions(home.homePage());

        new ExchangeRatesApiToUiSteps(page(), LOCALE, api)
                .verifyRowsMatchApi("USD", "EUR")
                .showAllCurrencies()
                .verifyEveryApiCurrencyShown();
    }
}
