package steps;

import com.microsoft.playwright.Page;
import components.SearchOverlayComponent;
import constants.AppLocale;
import pages.HomePage;
import pages.ProductPage;
import utils.LocalizedText;
import utils.RegexUtils;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SearchSteps {

    private final Page page;
    private final AppLocale locale;
    private SearchOverlayComponent searchOverlay;

    public SearchSteps(Page page, AppLocale locale) {
        this.page = page;
        this.locale = locale;
    }

    public SearchSteps openSearch(HomePage from) {
        searchOverlay = from.header().openSearch();
        assertThat(searchOverlay.root()).isVisible();
        assertThat(searchOverlay.root()).containsText(LocalizedText.get(locale, "search.title"));
        assertThat(searchOverlay.input()).isVisible();
        assertThat(searchOverlay.input()).isEditable();
        return this;
    }

    public SearchSteps searchFor(String keyword) {
        searchOverlay.search(keyword);
        assertThat(searchOverlay.input()).hasValue(keyword);
        assertThat(searchOverlay.results().first()).isVisible();
        return this;
    }

    public SearchSteps verifyResultShown(String title, String targetPath) {
        assertThat(searchOverlay.result(title, targetPath)).hasCount(1);
        assertThat(searchOverlay.result(title, targetPath).locator(".search-result-item__title")).hasText(title);
        return this;
    }

    public SearchSteps openResultAndVerifyProductPage(String title, String targetPath) {
        searchOverlay.openResult(title, targetPath);
        assertThat(searchOverlay.root()).isHidden();

        ProductPage productPage = new ProductPage(page, locale, targetPath);
        assertThat(page).hasURL(Pattern.compile(".*" + RegexUtils.escape(targetPath) + "/?$"));
        assertThat(productPage.heading()).hasText(title);
        assertThat(productPage.breadcrumbs().current()).hasText(title);
        assertThat(page).hasTitle(Pattern.compile(RegexUtils.escape(title)));
        return this;
    }
}
