package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import constants.PagePaths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Sticky quick-action menu (exchange rates, locations, chat) in the bottom-right corner of every page.
 * The buttons are stacked under the kebab toggle until the menu is expanded.
 */
public class QuickActionsComponent extends BaseComponent {

    public QuickActionsComponent(Page page) {
        super(page, page.locator("app-quick-actions"));
    }

    public Locator exchangeRatesLink() {
        return root.locator("a[href$='" + PagePaths.EXCHANGE_RATES + "']");
    }

    public Locator locationsLink() {
        return root.locator("a[href$='" + PagePaths.LOCATIONS + "']");
    }

    public void expand() {
        Locator menu = root.locator(".tbcx-pw-sticky-actions");
        if (!menu.getAttribute("class").contains("tbcx-pw-sticky-actions--open")) {
            root.locator("app-quick-action-button[tbcxpwtogglecontent] button").click();
        }
        assertThat(root.locator(".tbcx-pw-sticky-actions--open")).isVisible();
    }

    public void openExchangeRates() {
        expand();
        exchangeRatesLink().click();
    }

    public void openLocations() {
        expand();
        locationsLink().click();
    }
}
