package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TabMenuComponent extends BaseComponent {

    public TabMenuComponent(Page page, Locator root) {
        super(page, root);
    }

    public static TabMenuComponent firstOn(Page page) {
        return new TabMenuComponent(page, page.locator("tbcx-pw-tab-menu").first());
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
