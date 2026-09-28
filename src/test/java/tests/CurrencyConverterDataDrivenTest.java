package tests;

import constants.AppLocale;
import database.models.CurrencyConversion;
import org.testng.annotations.Test;
import steps.ExchangeRatesSteps;
import steps.HomeSteps;
import tests.dataproviders.CurrencyConversionDataProvider;

public class CurrencyConverterDataDrivenTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.KA;

    /**
     * Runs once per active row of the currency_conversion table (resources/testdata/sql/data.sql).
     * A new variation is a new row - this method does not change.
     */
    @Test(description = "SCRUM-T33 | Currency converter - convert currencies and amounts from the database",
            dataProvider = CurrencyConversionDataProvider.ACTIVE_CONVERSIONS,
            dataProviderClass = CurrencyConversionDataProvider.class)
    public void convertCurrencyFromDatabase(CurrencyConversion data) {
        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new ExchangeRatesSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .verifyConverterVisible()
                .verifyCurrencyName(data.getCurrencyCode(), data.getCurrencyName())
                .rememberRatesOf(data.getCurrencyCode())
                .selectConverterCurrency(data.getCurrencyCode())
                .convertAndVerify(data.getCurrencyCode(), data.amountAsInput());
    }
}
