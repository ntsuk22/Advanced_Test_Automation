package steps;

import com.microsoft.playwright.Page;
import components.CurrencyConverterComponent;
import components.RateHistoryDialogComponent;
import constants.AppLocale;
import pages.ExchangeRatesPage;
import pages.HomePage;
import utils.LocalizedText;
import utils.NumberUtils;
import utils.RateHistoryDateParser;
import utils.RegexUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ExchangeRatesSteps {

    private static final Pattern POSITIVE_NUMBER = Pattern.compile("^\\s*\\d+(\\.\\d+)?\\s*$");
    private static final int MIN_HISTORY_ENTRIES = 2;

    private final Page page;
    private final AppLocale locale;
    private final ExchangeRatesPage ratesPage;

    private String buyRateText;
    private double buyRate;
    private double sellRate;

    public ExchangeRatesSteps(Page page, AppLocale locale) {
        this.page = page;
        this.locale = locale;
        this.ratesPage = new ExchangeRatesPage(page, locale);
    }

    public ExchangeRatesSteps openFromQuickActions(HomePage from) {
        from.quickActions().openExchangeRates();
        return verifyPageOpened();
    }

    private ExchangeRatesSteps verifyPageOpened() {
        assertThat(page).hasURL(Pattern.compile(".*" + RegexUtils.escape(ratesPage.localizedPath()) + ".*"));
        assertThat(ratesPage.rateTypeTabs().activeTab()).hasText(LocalizedText.get(locale, "rates.tab.commercial"));
        assertThat(ratesPage.ratesTable()).isVisible();
        return this;
    }

    public ExchangeRatesSteps verifyContentLocalized() {
        new HeaderSteps(ratesPage.header(), locale).verifyLocalized();
        assertThat(ratesPage.heading()).hasText(LocalizedText.get(locale, "rates.heading"));
        assertThat(ratesPage.breadcrumbs().current()).hasText(LocalizedText.get(locale, "rates.breadcrumb"));
        assertThat(ratesPage.rateTypeTabs().activeTab()).hasText(LocalizedText.get(locale, "rates.tab.commercial"));
        assertThat(ratesPage.currencyName("USD")).containsText(LocalizedText.get(locale, "rates.currency.usd"));
        return this;
    }

    public ExchangeRatesSteps switchLanguageTo(AppLocale target) {
        ratesPage.header().languageSwitcher().toggle();
        return new ExchangeRatesSteps(page, target).verifyPageOpened();
    }

    public ExchangeRatesSteps verifyCurrencyListed(String currencyCode) {
        assertThat(ratesPage.rateRow(currencyCode)).isVisible();
        assertThat(ratesPage.buyRate(currencyCode)).hasText(POSITIVE_NUMBER);
        assertThat(ratesPage.sellRate(currencyCode)).hasText(POSITIVE_NUMBER);
        assertThat(ratesPage.officialRate(currencyCode)).containsText(Pattern.compile("\\d+\\.\\d+"));
        return this;
    }

    public ExchangeRatesSteps openHistory(String currencyCode) {
        ratesPage.openHistory(currencyCode);
        RateHistoryDialogComponent dialog = ratesPage.historyDialog();
        assertThat(dialog.root()).isVisible();
        assertThat(dialog.currencyBadge()).hasText(currencyCode);
        assertThat(dialog.title()).hasText(LocalizedText.get(locale, "rates.history.title"));
        assertThat(dialog.firstDateCell()).hasText(Pattern.compile("\\d"));
        assertThat(dialog.unfilledRows()).hasCount(0);
        return this;
    }

    public ExchangeRatesSteps verifyHistoryIsValid(String currencyCode) {
        List<Map<String, String>> entries = ratesPage.historyDialog().entries();
        assertTrue(entries.size() >= MIN_HISTORY_ENTRIES,
                currencyCode + " history should have at least " + MIN_HISTORY_ENTRIES + " entries, got " + entries.size());

        LocalDateTime previous = null;
        for (Map<String, String> entry : entries) {
            assertFalse(entry.get("date").isBlank(), "Empty date in " + currencyCode + " history: " + entry);
            double buy = NumberUtils.parse(entry.get("buyRate"));
            double sell = NumberUtils.parse(entry.get("sellRate"));
            assertTrue(buy > 0 && sell > 0, "Rates must be positive: " + entry);
            assertTrue(buy < sell, "Buy rate must be lower than sell rate: " + entry);

            LocalDateTime date = RateHistoryDateParser.parse(entry.get("date"), locale);
            if (previous != null) {
                assertFalse(date.isAfter(previous),
                        "History must be sorted newest first, but " + date + " comes after " + previous);
            }
            previous = date;
        }
        return this;
    }

    public ExchangeRatesSteps closeHistory() {
        ratesPage.historyDialog().close();
        assertThat(ratesPage.historyDialog().root()).isHidden();
        assertThat(ratesPage.ratesTable()).isVisible();
        return this;
    }

    public ExchangeRatesSteps verifyConverterDefaults(String from, String to) {
        CurrencyConverterComponent converter = ratesPage.converter();
        assertThat(converter.root()).isVisible();
        assertThat(converter.fromCurrency()).hasText(from);
        assertThat(converter.toCurrency()).hasText(to);
        assertThat(page).hasURL(Pattern.compile(".*/" + from + "-to-" + to + ".*"));
        return this;
    }

    public ExchangeRatesSteps rememberRatesOf(String currencyCode) {
        verifyCurrencyListed(currencyCode);
        buyRateText = ratesPage.buyRate(currencyCode).innerText().trim();
        buyRate = NumberUtils.parse(buyRateText);
        sellRate = NumberUtils.parse(ratesPage.sellRate(currencyCode).innerText());
        return this;
    }

    public ExchangeRatesSteps verifyConverterVisible() {
        assertThat(ratesPage.converter().root()).isVisible();
        assertThat(ratesPage.converter().fromAmount()).isEditable();
        return this;
    }

    public ExchangeRatesSteps verifyCurrencyName(String currencyCode, String expectedName) {
        assertThat(ratesPage.currencyName(currencyCode)).containsText(expectedName);
        return this;
    }

    public ExchangeRatesSteps selectConverterCurrency(String currencyCode) {
        CurrencyConverterComponent converter = ratesPage.converter();
        converter.selectFromCurrency(currencyCode);
        assertThat(converter.fromCurrency()).hasText(currencyCode);
        assertThat(converter.toCurrency()).hasText("GEL");
        assertThat(page).hasURL(Pattern.compile(".*/" + currencyCode + "-to-GEL.*"));
        assertThat(converter.rateDescription()).hasText("1 " + currencyCode + " = " + buyRateText + " GEL");
        return this;
    }

    public ExchangeRatesSteps convertAndVerify(String currencyCode, String amount) {
        CurrencyConverterComponent converter = ratesPage.converter();
        converter.enterAmount(amount);
        assertThat(page).hasURL(Pattern.compile(".*[?&]amount=" + RegexUtils.escape(amount) + "(&.*)?$"));

        double expected = NumberUtils.parse(amount) * buyRate;
        converter.waitForConvertedAmount(expected, tolerance(expected));
        assertThat(converter.rateDescription()).hasText("1 " + currencyCode + " = " + buyRateText + " GEL");
        return this;
    }

    public ExchangeRatesSteps swapAndVerify(String currencyCode, String amount) {
        CurrencyConverterComponent converter = ratesPage.converter();
        converter.swap();
        assertThat(converter.fromCurrency()).hasText("GEL");
        assertThat(converter.toCurrency()).hasText(currencyCode);
        assertThat(page).hasURL(Pattern.compile(".*/GEL-to-" + currencyCode + ".*"));
        assertThat(converter.fromAmount()).hasValue(amount);

        double expected = NumberUtils.parse(amount) / sellRate;
        converter.waitForConvertedAmount(expected, tolerance(expected));
        return this;
    }

    private static double tolerance(double expected) {
        return Math.max(0.01, expected * 0.0005);
    }
}
