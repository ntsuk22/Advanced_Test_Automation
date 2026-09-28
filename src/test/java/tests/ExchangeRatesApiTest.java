package tests;

import constants.AppLocale;
import org.testng.annotations.Test;
import steps.ExchangeRatesApiSteps;

public class ExchangeRatesApiTest {

    @Test(description = "SCRUM-T34 | API - get commercial exchange rates (happy path)")
    public void getCommercialRates() {
        new ExchangeRatesApiSteps()
                .requestCommercialRates(AppLocale.KA)
                .verifyEveryRateIsValid()
                .verifyMajorCurrency("USD", AppLocale.KA)
                .verifyMajorCurrency("EUR", AppLocale.KA)
                .verifyUpdateTimeIsValid()
                .verifySameRatesTranslatedTo(AppLocale.EN, "USD", "EUR");
    }

    @Test(description = "SCRUM-T35 | API - request exchange rate with invalid parameters (negative)")
    public void getExchangeRateWithInvalidParameters() {
        new ExchangeRatesApiSteps()
                .requestExchangeRateExpectingValidationError("USD", "GE")
                .verifyValidationError("Iso2", "Iso2 must be 3 characters long.")
                .verifyNoValidationErrorFor("Iso1")
                .requestExchangeRateExpectingValidationError("USD", "USD")
                .verifyValidationError("", "Iso1 and Iso2 must be different.")
                .verifyNoRateDataReturned();
    }
}
