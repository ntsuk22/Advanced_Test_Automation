package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import components.CurrencyConverterComponent;
import components.RateHistoryDialogComponent;
import components.TabMenuComponent;
import constants.AppLocale;
import constants.PagePaths;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Exchange-rates page: rate-type tabs, commercial rates table, currency converter and rate-history dialog.
 */
public class ExchangeRatesPage extends BasePage {

    private final TabMenuComponent rateTypeTabs;
    private final CurrencyConverterComponent converter;
    private final RateHistoryDialogComponent historyDialog;

    public ExchangeRatesPage(Page page, AppLocale locale) {
        super(page, locale);
        // The first tab menu on the page switches the rates table (commercial / wholesale / ...)
        this.rateTypeTabs = new TabMenuComponent(page, page.locator("tbcx-pw-tab-menu").first());
        this.converter = new CurrencyConverterComponent(page);
        this.historyDialog = new RateHistoryDialogComponent(page);
    }

    @Override
    protected String path() {
        return PagePaths.EXCHANGE_RATES;
    }

    public TabMenuComponent rateTypeTabs() {
        return rateTypeTabs;
    }

    public CurrencyConverterComponent converter() {
        return converter;
    }

    public RateHistoryDialogComponent historyDialog() {
        return historyDialog;
    }

    public Locator heading() {
        return page.locator("h1").first();
    }

    public Locator ratesTable() {
        return page.locator(".tbcx-pw-exchange-rates__table");
    }

    public Locator rateRow(String currencyCode) {
        Locator currencyCell = page.locator("[col-id='currencyName']")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + currencyCode + "\\b")));
        return ratesTable().locator(".ag-row").filter(new Locator.FilterOptions().setHas(currencyCell));
    }

    public Locator buyRate(String currencyCode) {
        return rateRow(currencyCode).locator("[col-id='buyRate']");
    }

    public Locator sellRate(String currencyCode) {
        return rateRow(currencyCode).locator("[col-id='sellRate']");
    }

    public Locator currencyName(String currencyCode) {
        return rateRow(currencyCode).locator("[col-id='currencyName']");
    }

    public Locator officialRate(String currencyCode) {
        return rateRow(currencyCode).locator("[col-id='officialRate']");
    }

    public void openHistory(String currencyCode) {
        rateRow(currencyCode).click();
    }

    public Locator rateRows() {
        return ratesTable().locator(".ag-row");
    }

    /** The table initially lists the most popular currencies; this button reveals the rest. */
    public Locator showAllCurrenciesButton(String label) {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(label));
    }

    /**
     * Reads every rendered row as {currencyName, buyRate, sellRate, officialRate} with whitespace normalised,
     * e.g. currencyName "CNY 10 ჩინური იუანი", officialRate "chevron-up-outlined 3.8848" (trend icon + value).
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> readRateRows() {
        return (List<Map<String, String>>) rateRows().evaluateAll(
                "rows => rows.map(r => Object.fromEntries([...r.querySelectorAll('[col-id]')]"
                        + ".map(c => [c.getAttribute('col-id'), c.innerText.replace(/\\s+/g, ' ').trim()])))");
    }
}
