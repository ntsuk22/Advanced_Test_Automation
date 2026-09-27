package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Site header rendered on every page.
 */
public class HeaderComponent extends BaseComponent {

    private final LanguageSwitcherComponent languageSwitcher;

    public HeaderComponent(Page page) {
        super(page, page.locator("header"));
        this.languageSwitcher = new LanguageSwitcherComponent(page, root.locator("tbcx-lang-switcher").first());
    }

    public Locator searchButton() {
        return root.locator("button.tbcx-pw-search__button");
    }

    /** Top-level navigation entries ("ჩემთვის" / "Personal", ...). */
    public Locator navigationItems() {
        return root.locator(".tbcx-pw-navigation-item__link");
    }

    public LanguageSwitcherComponent languageSwitcher() {
        return languageSwitcher;
    }

    public SearchOverlayComponent openSearch() {
        searchButton().click();
        return new SearchOverlayComponent(page);
    }
}
