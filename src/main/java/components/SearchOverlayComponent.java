package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Global search overlay opened from the header search button.
 */
public class SearchOverlayComponent extends BaseComponent {

    public SearchOverlayComponent(Page page) {
        super(page, page.locator(".global-search"));
    }

    public Locator input() {
        return root.locator("input");
    }

    public Locator results() {
        return root.locator(".search-result-item");
    }

    public Locator resultTitles() {
        return root.locator(".search-result-item__title");
    }

    /**
     * A result is identified by its title and target path, because the same title can
     * exist for different segments (e.g. personal and business "ვადიანი ანაბარი").
     */
    public Locator result(String title, String targetPath) {
        return results()
                .filter(new Locator.FilterOptions().setHas(page.locator("a[href='" + targetPath + "']")))
                .filter(new Locator.FilterOptions().setHasText(title));
    }

    public void search(String keyword) {
        input().fill(keyword);
    }

    public void openResult(String title, String targetPath) {
        result(title, targetPath).locator("a[href='" + targetPath + "']").click();
    }
}
