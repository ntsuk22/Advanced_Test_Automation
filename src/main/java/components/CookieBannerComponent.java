package components;

import com.microsoft.playwright.Page;

/**
 * Cookie consent banner shown on the first visit of every page.
 */
public class CookieBannerComponent extends BaseComponent {

    public CookieBannerComponent(Page page) {
        super(page, page.locator("tbcx-pw-cookie-consent"));
    }

    public void acceptAll() {
        // Actions are always ordered Accept / Settings / Reject ("თანხმობა" / "Accept all" first in every locale);
        // their styling classes vary between site releases, so the order is the stable signal
        root.locator(".tbcx-pw-cookie-consent__actions button").first().click();
    }
}
