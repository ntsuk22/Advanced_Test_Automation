package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.regex.Pattern;

/**
 * Currency converter on the exchange-rates page: amount + currency on each side, a swap button
 * and the "1 USD = 2.555 GEL" description.
 */
public class CurrencyConverterComponent extends BaseComponent {

    private static final String AMOUNT_INPUT = "input.amount-field__input";

    public CurrencyConverterComponent(Page page) {
        super(page, page.locator(".exchange-rates-calculator"));
    }

    public Locator fromAmount() {
        return root.locator(AMOUNT_INPUT).nth(0);
    }

    public Locator toAmount() {
        return root.locator(AMOUNT_INPUT).nth(1);
    }

    public Locator fromCurrency() {
        return root.locator("app-currency-dropdown .currency-dropdown__selected").nth(0);
    }

    public Locator toCurrency() {
        return root.locator("app-currency-dropdown .currency-dropdown__selected").nth(1);
    }

    public Locator rateDescription() {
        return root.locator(".exchange-rates-calculator__description");
    }

    /** Opens the source-currency dropdown and picks {@code currencyCode}; the option list renders in an overlay. */
    public void selectFromCurrency(String currencyCode) {
        root.locator(".currency-dropdown__trigger").nth(0).click();
        page.locator(".currency-dropdown__panel .currency-dropdown__item")
                .filter(new Locator.FilterOptions().setHas(page.locator(".currency-dropdown__item-label")
                        .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + currencyCode + "\\s*$")))))
                .click();
    }

    public void enterAmount(String amount) {
        fromAmount().fill(amount);
    }

    public void swap() {
        root.locator("button.currency-calculator__swap__button").click();
    }

    /**
     * Converted amount is calculated client-side; wait until the target field holds a value
     * within {@code tolerance} of {@code expected} instead of sleeping.
     */
    public void waitForConvertedAmount(double expected, double tolerance) {
        page.waitForFunction(
                "([el, exp, tol]) => { const v = parseFloat((el.value || '').replace(/,/g, '')); return !isNaN(v) && Math.abs(v - exp) <= tol; }",
                new Object[]{toAmount().elementHandle(), expected, tolerance});
    }
}
