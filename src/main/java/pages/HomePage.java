package pages;

import com.microsoft.playwright.Page;
import constants.AppLocale;
import constants.PagePaths;

public class HomePage extends BasePage {

    public HomePage(Page page, AppLocale locale) {
        super(page, locale);
    }

    @Override
    protected String path() {
        return PagePaths.HOME;
    }
}
