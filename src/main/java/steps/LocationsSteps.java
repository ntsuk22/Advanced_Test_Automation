package steps;

import com.google.gson.JsonParser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import constants.AppLocale;
import constants.LocationType;
import pages.HomePage;
import pages.LocationsPage;
import utils.RegexUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LocationsSteps {

    private final Page page;
    private final LocationsPage locationsPage;
    private final Map<LocationType, Integer> countByType = new EnumMap<>(LocationType.class);
    private int initialCount;

    public LocationsSteps(Page page, AppLocale locale) {
        this.page = page;
        this.locationsPage = new LocationsPage(page, locale);
    }

    public LocationsSteps openFromQuickActions(HomePage from) {
        Response response = page.waitForResponse(LocationsPage::isLocationListResponse,
                () -> from.quickActions().openLocations());
        assertThat(page).hasURL(Pattern.compile(".*" + RegexUtils.escape(locationsPage.localizedPath()) + ".*"));
        assertThat(locationsPage.map()).isVisible();
        initialCount = waitForRenderedList(response);
        return this;
    }

    public LocationsSteps typeCity(String city) {
        locationsPage.typeInSearch(city);
        assertThat(locationsPage.searchInput()).hasValue(city);
        return this;
    }

    public LocationsSteps submitSearchAndVerifyOnlyLocationsIn(String city) {
        int filteredCount = waitForRenderedList(locationsPage.submitSearch(city));

        assertThat(locationsPage.itemsWithTitleNotContaining(city)).hasCount(0);
        assertTrue(filteredCount < initialCount,
                "Search should narrow the list: before=" + initialCount + ", after=" + filteredCount);
        return this;
    }

    public LocationsSteps selectTypeAndVerifyOnlyThatType(LocationType type, String tabLabel) {
        int count = waitForRenderedList(locationsPage.selectType(tabLabel));

        assertThat(locationsPage.typeTabs().activeTab()).hasCount(1);
        assertThat(locationsPage.typeTabs().activeTab()).hasText(tabLabel);
        if (type == LocationType.ATM) {
            assertThat(locationsPage.itemsWithoutIcon(LocationType.ATM.icon())).hasCount(0);
        } else {
            assertThat(locationsPage.itemsWithIcon(LocationType.ATM.icon())).hasCount(0);
            assertThat(locationsPage.itemsWithoutIcon(type.icon())).hasCount(0);
        }
        countByType.put(type, count);
        return this;
    }

    public LocationsSteps verifyTypeCountsAddUpToAll() {
        assertEquals(countByType.size(), LocationType.values().length, "Every location type must be selected first");
        int sum = countByType.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(sum, initialCount, "ATMs + Branches + CDMs should equal all locations " + countByType);
        return this;
    }

    private int waitForRenderedList(Response response) {
        assertTrue(response.ok(), "Location list request failed with HTTP " + response.status());
        int expected = JsonParser.parseString(response.text()).getAsJsonArray().size();
        assertTrue(expected > 0, "Location list response is empty");
        assertThat(locationsPage.items()).hasCount(expected);
        return expected;
    }
}
