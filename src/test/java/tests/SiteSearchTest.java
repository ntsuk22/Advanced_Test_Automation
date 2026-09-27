package tests;

import constants.AppLocale;
import org.testng.annotations.Test;
import steps.HomeSteps;
import steps.SearchSteps;

public class SiteSearchTest extends BaseTest {

    private static final AppLocale LOCALE = AppLocale.KA;

    @Test(description = "SCRUM-T30 | Site search - find a product and open its page")
    public void searchProductAndOpenIt() {
        String keyword = "ანაბარი";
        String resultTitle = "ვადიანი ანაბარი";
        String resultPath = "/ka/deposits/term-deposit";

        HomeSteps home = new HomeSteps(page(), LOCALE).openHomePage().acceptCookies();

        new SearchSteps(page(), LOCALE)
                .openSearch(home.homePage())
                .searchFor(keyword)
                .verifyResultShown(resultTitle, resultPath)
                .openResultAndVerifyProductPage(resultTitle, resultPath);
    }
}
