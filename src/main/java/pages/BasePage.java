package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import components.BreadcrumbsComponent;
import components.CookieBannerComponent;
import components.HeaderComponent;
import components.QuickActionsComponent;
import constants.AppLocale;
import utils.ConfigReader;

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

    public void failIfBlockedBySite() {
        if (page.getByText(BLOCKED_PAGE_TEXT).count() > 0) {
            throw new IllegalStateException("tbcbank.ge blocked the request (WAF '" + BLOCKED_PAGE_TEXT
                    + "', error code 101). This is rate limiting, not a test failure - wait a few minutes and re-run.");
        }
    }

    public Locator heading() {
        return page.locator("h1").first();
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
