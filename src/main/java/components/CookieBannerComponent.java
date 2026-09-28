package components;

import com.microsoft.playwright.Page;

public class CookieBannerComponent extends BaseComponent {

    public CookieBannerComponent(Page page) {
        super(page, page.locator("tbcx-pw-cookie-consent"));
    }

    public void acceptAll() {
        root.locator(".tbcx-pw-cookie-consent__actions button").first().click();
    }
}
