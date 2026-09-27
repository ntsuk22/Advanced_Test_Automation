package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import components.TabMenuComponent;
import constants.AppLocale;
import constants.PagePaths;
import utils.RegexUtils;

import java.util.regex.Pattern;

/**
 * ATMs &amp; Branches page: Google map, location search, type tabs and the location list.
 */
public class LocationsPage extends BasePage {

    private static final String LIST_ITEM = ".tbcx-pw-atm-branches-section__list-item";
    private static final String LOCATIONS_API = "/atmsAndBranches/list";

    private final TabMenuComponent typeTabs;

    public LocationsPage(Page page, AppLocale locale) {
        super(page, locale);
        this.typeTabs = new TabMenuComponent(page, page.locator("tbcx-pw-tab-menu").first());
    }

    @Override
    protected String path() {
        return PagePaths.LOCATIONS;
    }

    public TabMenuComponent typeTabs() {
        return typeTabs;
    }

    public Locator map() {
        return page.locator(".gm-style").first();
    }

    public Locator searchInput() {
        return page.locator("input.search-input");
    }

    public Locator items() {
        return page.locator(LIST_ITEM);
    }

    public Locator itemsWithIcon(String icon) {
        return items().filter(new Locator.FilterOptions().setHas(iconLocator(icon)));
    }

    public Locator itemsWithoutIcon(String icon) {
        return items().filter(new Locator.FilterOptions().setHasNot(iconLocator(icon)));
    }

    /** Locations whose address (title) does not contain {@code text}, case-insensitive. */
    public Locator itemsWithTitleNotContaining(String text) {
        Locator matchingTitle = page.locator(".tbcx-pw-atm-branches-section__list-item-title")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(RegexUtils.escape(text), Pattern.CASE_INSENSITIVE)));
        return items().filter(new Locator.FilterOptions().setHasNot(matchingTitle));
    }

    private Locator iconLocator(String icon) {
        return page.locator(".tbcx-pw-atm-branches-section__list-item-icon")
                .filter(new Locator.FilterOptions().setHasText(icon));
    }

    public void typeInSearch(String text) {
        searchInput().fill(text);
    }

    /** True for the POST request that (re)loads the location list. */
    public static boolean isLocationListResponse(Response response) {
        return response.url().contains(LOCATIONS_API) && "POST".equals(response.request().method());
    }

    /**
     * Presses Enter in the search field and returns once the list request carrying the keyword has completed.
     */
    public Response submitSearch(String keyword) {
        return page.waitForResponse(
                r -> isLocationListResponse(r) && r.request().postData() != null
                        && r.request().postData().contains("\"keyword\":\"" + keyword + "\""),
                () -> searchInput().press("Enter"));
    }

    /**
     * Clicks a type tab and returns once the list request for that tab has completed.
     */
    public Response selectType(String tabLabel) {
        return page.waitForResponse(LocationsPage::isLocationListResponse, () -> typeTabs.select(tabLabel));
    }
}
