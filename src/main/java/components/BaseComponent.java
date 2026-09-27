package components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * A reusable UI fragment. Every selector of the fragment is resolved relative to its root,
 * so the same component works on any page that renders it.
 */
public abstract class BaseComponent {

    protected final Page page;
    protected final Locator root;

    protected BaseComponent(Page page, Locator root) {
        this.page = page;
        this.root = root;
    }

    public Locator root() {
        return root;
    }
}
