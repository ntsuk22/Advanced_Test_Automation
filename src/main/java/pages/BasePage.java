package pages;

import com.microsoft.playwright.Page;
import components.BreadcrumbsComponent;
import components.CookieBannerComponent;
import components.HeaderComponent;
import components.QuickActionsComponent;
import constants.AppLocale;
import utils.ConfigReader;

/**
 * Common part of every tbcbank.ge page: the shared components and locale-aware URL building.
 */
public abstract class BasePage {

    private static final String BLOCKED_PAGE_TEXT = "Your request has been blocked";

    protected final Page page;
    protected final AppLocale locale;

    private final HeaderComponent header;
    private final CookieBannerComponent cookieBanner;
    private final QuickActionsComponent quickActions;
    private final BreadcrumbsComponent breadcrumbs;

    protected BasePage(Page page, AppLocale locale) {
        this.page = page;
        this.locale = locale;
        this.header = new HeaderComponent(page);
        this.cookieBanner = new CookieBannerComponent(page);
        this.quickActions = new QuickActionsComponent(page);
        this.breadcrumbs = new BreadcrumbsComponent(page);
    }

    /** Path of the page without the locale prefix, see {@link constants.PagePaths}. */
    protected abstract String path();

    public String localizedPath() {
        return "/" + locale.path() + path();
    }

    public String url() {
        return ConfigReader.get("base.url") + localizedPath();
    }

    public void open() {
        page.navigate(url());
    }

    /**
     * tbcbank.ge's firewall answers too many rapid requests with a plain "Your request has been blocked" page.
     * Fail with that reason instead of a misleading "element not found" further on.
     */
    public void failIfBlockedBySite() {
        if (page.getByText(BLOCKED_PAGE_TEXT).count() > 0) {
            throw new IllegalStateException("tbcbank.ge blocked the request (WAF '" + BLOCKED_PAGE_TEXT
                    + "', error code 101). This is rate limiting, not a test failure - wait a few minutes and re-run.");
        }
    }

    public HeaderComponent header() {
        return header;
    }

    public CookieBannerComponent cookieBanner() {
        return cookieBanner;
    }

    public QuickActionsComponent quickActions() {
        return quickActions;
    }

    public BreadcrumbsComponent breadcrumbs() {
        return breadcrumbs;
    }

    public Page page() {
        return page;
    }
}
