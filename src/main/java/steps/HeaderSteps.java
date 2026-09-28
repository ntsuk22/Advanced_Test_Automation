package steps;

import components.HeaderComponent;
import constants.AppLocale;
import utils.LocalizedText;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HeaderSteps {

    private final HeaderComponent header;
    private final AppLocale locale;

    public HeaderSteps(HeaderComponent header, AppLocale locale) {
        this.header = header;
        this.locale = locale;
    }

    public HeaderSteps verifyLocalized() {
        assertThat(header.navigationItems().first()).hasText(LocalizedText.get(locale, "header.nav.first"));
        assertThat(header.languageSwitcher().label()).hasText(LocalizedText.get(locale, "header.language.switcher"));
        return this;
    }
}
