package steps;

import api.models.CommercialRatesResponse;
import api.models.CurrencyRate;
import com.microsoft.playwright.Page;
import constants.AppLocale;
import pages.ExchangeRatesPage;
import utils.LocalizedText;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Compares the exchange-rates table with the commercialList API response.
 * The API is the source of truth: every expected value comes from {@link CommercialRatesResponse}.
 */
public class ExchangeRatesApiToUiSteps {

    private final AppLocale locale;
    private final ExchangeRatesPage ratesPage;
    private final CommercialRatesResponse api;

    public ExchangeRatesApiToUiSteps(Page page, AppLocale locale, CommercialRatesResponse api) {
        this.locale = locale;
        this.ratesPage = new ExchangeRatesPage(page, locale);
        this.api = api;
    }

    public ExchangeRatesApiToUiSteps verifyRowsMatchApi(String... isos) {
        List<Map<String, String>> uiRows = ratesPage.readRateRows();
        for (String iso : isos) {
            CurrencyRate expected = api.rate(iso)
                    .orElseThrow(() -> new AssertionError(iso + " is missing from the API response"));
            verifyRowMatches(expected, uiRows);
        }
        return this;
    }

    public ExchangeRatesApiToUiSteps showAllCurrencies() {
        ratesPage.showAllCurrenciesButton(LocalizedText.get(locale, "rates.show.all")).click();
        // Waits until the table renders exactly as many currencies as the API returned
        assertThat(ratesPage.rateRows()).hasCount(api.getRates().size());
        return this;
    }

    public ExchangeRatesApiToUiSteps verifyEveryApiCurrencyShown() {
        List<Map<String, String>> uiRows = ratesPage.readRateRows();
        assertEquals(uiRows.size(), api.getRates().size(), "Number of currencies in the table vs API");
        api.getRates().forEach(expected -> verifyRowMatches(expected, uiRows));
        return this;
    }

    /**
     * UI row label is "{iso} {weight} {name}" (e.g. "CNY 10 ჩინური იუანი"); rates are compared numerically
     * because the UI drops trailing zeros (API 2.5530 is shown as 2.553).
     */
    private void verifyRowMatches(CurrencyRate expected, List<Map<String, String>> uiRows) {
        String iso = expected.getIso();
        Map<String, String> row = findRow(uiRows, iso)
                .orElseThrow(() -> new AssertionError(iso + " from the API is not shown in the table"));

        String expectedLabel = iso + " " + expected.getWeight().stripTrailingZeros().toPlainString() + " " + expected.getName();
        assertEquals(row.get("currencyName"), expectedLabel, iso + " row label");
        assertSameNumber(row.get("buyRate"), expected.getBuyRate(), iso + " buy rate");
        assertSameNumber(row.get("sellRate"), expected.getSellRate(), iso + " sell rate");
        assertSameNumber(lastToken(row.get("officialRate")), expected.getOfficialCourse(), iso + " official rate");
    }

    private static Optional<Map<String, String>> findRow(List<Map<String, String>> uiRows, String iso) {
        return uiRows.stream().filter(row -> row.get("currencyName").startsWith(iso + " ")).findFirst();
    }

    /** The official-rate cell also contains the trend icon name, the value is the last token. */
    private static String lastToken(String text) {
        String[] parts = text.split(" ");
        return parts[parts.length - 1];
    }

    private static void assertSameNumber(String uiText, BigDecimal apiValue, String what) {
        BigDecimal uiValue;
        try {
            uiValue = new BigDecimal(uiText.replace(",", ""));
        } catch (NumberFormatException e) {
            throw new AssertionError(what + ": UI shows non-numeric '" + uiText + "'");
        }
        assertTrue(uiValue.compareTo(apiValue) == 0, what + ": UI " + uiText + " vs API " + apiValue);
    }
}
