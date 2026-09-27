package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Breadcrumb trail rendered under the header on every inner page.
 */
public class BreadcrumbsComponent extends BaseComponent {

    public BreadcrumbsComponent(Page page) {
        super(page, page.locator(".tbcx-pw-breadcrumbs"));
    }

    public Locator items() {
        return root.locator(".tbcx-pw-breadcrumbs__item");
    }

    /** Link text of the last breadcrumb, i.e. the current page (without the separator icon). */
    public Locator current() {
        return items().last().locator("a");
    }
}
