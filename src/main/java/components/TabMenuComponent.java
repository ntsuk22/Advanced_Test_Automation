package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Horizontal tab menu (tbcx-pw-tab-menu) used by several pages,
 * e.g. ATMs / Branches / CDMs on the locations page and rate types on the exchange-rates page.
 */
public class TabMenuComponent extends BaseComponent {

    public TabMenuComponent(Page page, Locator root) {
        super(page, root);
    }

    public Locator tab(String label) {
        return root.locator(".tbcx-pw-tab-menu__item").filter(new Locator.FilterOptions().setHasText(label));
    }

    public Locator activeTab() {
        return root.locator(".tbcx-pw-tab-menu__item.active");
    }

    public void select(String label) {
        tab(label).click();
    }
}
