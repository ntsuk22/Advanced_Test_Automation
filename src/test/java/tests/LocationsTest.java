package tests;

import constants.AppLocale;
import constants.LocationType;
import org.testng.annotations.Test;
import steps.HomeSteps;
import steps.LocationsSteps;
import utils.LocalizedText;

public class LocationsTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.EN;

    @Test(description = "SCRUM-T27 | Locations - search branches and ATMs by city")
    public void searchLocationsByCity() {
        String city = "Batumi";

        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new LocationsSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .typeCity(city)
                .submitSearchAndVerifyOnlyLocationsIn(city);
    }

    @Test(description = "SCRUM-T28 | Locations - filter by ATMs / Branches / CDMs tabs")
    public void filterLocationsByType() {
        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new LocationsSteps(page(), LOCALE)
                .openFromQuickActions(home.homePage())
                .selectTypeAndVerifyOnlyThatType(LocationType.ATM, LocalizedText.get(LOCALE, "locations.tab.atm"))
                .selectTypeAndVerifyOnlyThatType(LocationType.BRANCH, LocalizedText.get(LOCALE, "locations.tab.branch"))
                .selectTypeAndVerifyOnlyThatType(LocationType.CDM, LocalizedText.get(LOCALE, "locations.tab.cdm"))
                .verifyTypeCountsAddUpToAll();
    }
}
