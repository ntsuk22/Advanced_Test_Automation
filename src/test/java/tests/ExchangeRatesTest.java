package tests;

import constants.AppLocale;
import org.testng.annotations.Test;
import steps.ExchangeRatesSteps;
import steps.HomeSteps;

public class ExchangeRatesTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.KA;

    @Test(description = "SCRUM-T29 | Exchange rates - view USD and EUR rate history")
    public void viewRateHistory() {
        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new ExchangeRatesSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .verifyCurrencyListed("USD")
                .verifyCurrencyListed("EUR")
                .openHistory("USD")
                .verifyHistoryIsValid("USD")
                .closeHistory()
                .openHistory("EUR")
                .verifyHistoryIsValid("EUR");
    }

    @Test(description = "SCRUM-T31 | Currency converter - convert amount and swap currencies")
    public void convertCurrency() {
        String currency = "USD";
        String amount = "250";

        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new ExchangeRatesSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .verifyConverterDefaults(currency, "GEL")
                .rememberRatesOf(currency)
                .convertAndVerify(currency, amount)
                .swapAndVerify(currency, amount);
    }
}
