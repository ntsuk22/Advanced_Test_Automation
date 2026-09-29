package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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

    public Locator result(String title, String targetPath) {
        return results()
                .filter(new Locator.FilterOptions().setHas(page.locator("a[href='" + targetPath + "']")))
                .filter(new Locator.FilterOptions().setHasText(title));
    }

    public Locator resultTitle(String title, String targetPath) {
        return result(title, targetPath).locator(".search-result-item__title");
    }

    public void search(String keyword) {
        input().fill(keyword);
    }

    public void openResult(String title, String targetPath) {
        result(title, targetPath).locator("a[href='" + targetPath + "']").click();
    }
}
