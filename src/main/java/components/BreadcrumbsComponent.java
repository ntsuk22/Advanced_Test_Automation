package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class BreadcrumbsComponent extends BaseComponent {

    public BreadcrumbsComponent(Page page) {
        super(page, page.locator(".tbcx-pw-breadcrumbs"));
    }

    public Locator items() {
        return root.locator(".tbcx-pw-breadcrumbs__item");
    }

    public Locator current() {
        return items().last().locator("a");
    }
}
