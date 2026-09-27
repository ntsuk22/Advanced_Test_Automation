package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * "კურსის ისტორია" dialog opened by clicking a currency row on the exchange-rates page.
 */
public class RateHistoryDialogComponent extends BaseComponent {

    public RateHistoryDialogComponent(Page page) {
        super(page, page.locator(".cdk-overlay-pane")
                .filter(new Locator.FilterOptions().setHas(page.locator(".tbcx-pw-exchange-rates-history"))));
    }

    public Locator currencyBadge() {
        return root.locator(".tbcx-pw-currency-badge").first();
    }

    public Locator title() {
        return root.locator(".tbcx-pw-exchange-rates-history__title");
    }

    public Locator rows() {
        return root.locator(".tbcx-pw-exchange-rates-history .ag-row");
    }

    public Locator firstDateCell() {
        return rows().first().locator("[col-id='date']");
    }

    /** Rows whose cells are not filled yet (the grid renders placeholders before the data arrives). */
    public Locator unfilledRows() {
        Locator filledCell = page.locator("[col-id='sellRate']")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("\\d")));
        return rows().filter(new Locator.FilterOptions().setHasNot(filledCell));
    }

    public void close() {
        root.locator(".tbcx-pw-dialog__header-close button").click();
    }

    /**
     * Reads every history row as {date, buyRate, sellRate}. Call only after the rows are loaded.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> entries() {
        return (List<Map<String, String>>) rows().evaluateAll(
                "rows => rows.map(r => ({"
                        + " date: r.querySelector(\"[col-id='date']\").innerText.trim(),"
                        + " buyRate: r.querySelector(\"[col-id='buyRate']\").innerText.trim(),"
                        + " sellRate: r.querySelector(\"[col-id='sellRate']\").innerText.trim() }))");
    }
}
