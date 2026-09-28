package tests;

import constants.AppLocale;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import steps.ExchangeRatesSteps;
import steps.HomeSteps;

public class LocalizationTest extends BaseTest {

    @DataProvider(name = "localeSwitches")
    public Object[][] localeSwitches() {
        return new Object[][]{
                {AppLocale.KA, AppLocale.EN},
                {AppLocale.EN, AppLocale.KA},
        };
    }

    @Test(description = "SCRUM-T32 | Localization - exchange rates journey in Georgian and English",
            dataProvider = "localeSwitches")
    public void exchangeRatesJourneyIsLocalized(AppLocale startLocale, AppLocale switchedLocale) {
        String currency = "USD";

        HomeSteps home = new HomeSteps(page(), startLocale)
                .openHomePage()
                .acceptCookies()
                .verifyHeaderLocalized();

        new ExchangeRatesSteps(page(), startLocale)
                .openFromQuickActions(home.homePage())
                .verifyContentLocalized()
                .openHistory(currency)
                .verifyHistoryIsValid(currency)
                .closeHistory()
                .switchLanguageTo(switchedLocale)
                .verifyContentLocalized()
                .openHistory(currency)
                .verifyHistoryIsValid(currency);
    }
}
