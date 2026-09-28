package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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
