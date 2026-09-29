package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.Geolocation;

import java.util.List;

public final class PlaywrightFactory {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightFactory() {
    }

    public static Page start() {
        Playwright playwright = Playwright.create();
        Browser browser = browserType(playwright).launch(new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBoolean("headless"))
                .setSlowMo(ConfigReader.getDouble("slow.mo")));

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(ConfigReader.getInt("viewport.width"), ConfigReader.getInt("viewport.height"))
                .setPermissions(List.of("geolocation"))
                .setGeolocation(new Geolocation(ConfigReader.getDouble("geo.latitude"), ConfigReader.getDouble("geo.longitude"))));
        context.setDefaultTimeout(ConfigReader.getInt("timeout.default"));
        context.setDefaultNavigationTimeout(ConfigReader.getInt("timeout.navigation"));
        PlaywrightAssertions.setDefaultAssertionTimeout(ConfigReader.getInt("timeout.assertion"));

        Page page = context.newPage();

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        CONTEXT.set(context);
        PAGE.set(page);
        return page;
    }

    private static BrowserType browserType(Playwright playwright) {
        return switch (ConfigReader.get("browser").toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            case "chromium" -> playwright.chromium();
            default -> throw new IllegalArgumentException("Unsupported browser: " + ConfigReader.get("browser"));
        };
    }

    public static boolean isStarted() {
        return PAGE.get() != null;
    }

    public static Page getPage() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Playwright was not started for this thread");
        }
        return page;
    }

    public static void stop() {
        if (CONTEXT.get() != null) {
            CONTEXT.get().close();
        }
        if (BROWSER.get() != null) {
            BROWSER.get().close();
        }
        if (PLAYWRIGHT.get() != null) {
            PLAYWRIGHT.get().close();
        }
        PAGE.remove();
        CONTEXT.remove();
        BROWSER.remove();
        PLAYWRIGHT.remove();
    }
}
