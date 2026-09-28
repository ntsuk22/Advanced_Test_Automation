package tests;

import constants.AppLocale;
import org.testng.annotations.Test;
import steps.ExchangeRatesSteps;
import steps.HomeSteps;
import steps.RateHistoryNetworkSteps;

public class NetworkValidationTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.KA;

    @Test(description = "SCRUM-T37 | Network - rate-history request sent when a currency row is clicked")
    public void rateHistoryRequestAndResponse() {
        String currency = "EUR";

        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();
        new ExchangeRatesSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .verifyCurrencyListed(currency);

        new RateHistoryNetworkSteps(page(), LOCALE)
                .openHistoryAndCaptureTraffic(currency)
                .verifyRequest(currency, "GEL")
                .verifyResponse(currency, "GEL")
                .verifyDialogMatchesResponse(currency);
    }
}
