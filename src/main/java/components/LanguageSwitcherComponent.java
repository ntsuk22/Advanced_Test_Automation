package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Header language toggle. It shows the language it switches to ("EN" on Georgian pages, "ქარ" on English ones)
 * and a click re-renders the current page in that language.
 */
public class LanguageSwitcherComponent extends BaseComponent {

    public LanguageSwitcherComponent(Page page, Locator root) {
        super(page, root);
    }

    public Locator label() {
        return root.locator(".tbcx-language-select__selected");
    }

    public void toggle() {
        root.locator(".tbcx-language-select__field").click();
    }
}
