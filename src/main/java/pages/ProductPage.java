package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import constants.AppLocale;

/**
 * Generic product page (deposit, loan, card, ...). The concrete product is defined by its path.
 */
public class ProductPage extends BasePage {

    private final String path;

    /**
     * @param localizedPath path as it appears in links, e.g. "/ka/deposits/term-deposit"
     */
    public ProductPage(Page page, AppLocale locale, String localizedPath) {
        super(page, locale);
        String prefix = "/" + locale.path();
        this.path = localizedPath.startsWith(prefix) ? localizedPath.substring(prefix.length()) : localizedPath;
    }

    @Override
    protected String path() {
        return path;
    }

    public Locator heading() {
        return page.locator("h1").first();
    }
}
