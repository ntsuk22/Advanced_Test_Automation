package steps;

import com.microsoft.playwright.Page;
import constants.AppLocale;
import pages.HomePage;
import utils.RegexUtils;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomeSteps {

    private final AppLocale locale;
    private final HomePage homePage;

    public HomeSteps(Page page, AppLocale locale) {
        this.locale = locale;
        this.homePage = new HomePage(page, locale);
    }

    public HomeSteps openHomePage() {
        homePage.open();
        assertThat(homePage.page()).hasURL(Pattern.compile(RegexUtils.escape(homePage.url()) + "/?$"));
        assertThat(homePage.header().root()).isVisible();
        return this;
    }

    public HomeSteps acceptCookies() {
        homePage.cookieBanner().acceptAll();
        assertThat(homePage.cookieBanner().root()).isHidden();
        return this;
    }

    public HomeSteps verifyHeaderLocalized() {
        new HeaderSteps(homePage.header(), locale).verifyLocalized();
        return this;
    }

    public HomePage homePage() {
        return homePage;
    }
}
