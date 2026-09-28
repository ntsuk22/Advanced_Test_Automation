package pages;

import com.microsoft.playwright.Page;
import constants.AppLocale;

public class ProductPage extends BasePage {

    private final String path;

    public ProductPage(Page page, AppLocale locale, String localizedPath) {
        super(page, locale);
        String prefix = "/" + locale.path();
        this.path = localizedPath.startsWith(prefix) ? localizedPath.substring(prefix.length()) : localizedPath;
    }

    @Override
    protected String path() {
        return path;
    }
}
