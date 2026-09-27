package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Site header rendered on every page.
 */
public class HeaderComponent extends BaseComponent {

    public HeaderComponent(Page page) {
        super(page, page.locator("header"));
    }

    public Locator searchButton() {
        return root.locator("button.tbcx-pw-search__button");
    }

    public SearchOverlayComponent openSearch() {
        searchButton().click();
        return new SearchOverlayComponent(page);
    }
}
