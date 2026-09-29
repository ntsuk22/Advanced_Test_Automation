package tests;

import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.PlaywrightFactory;

import java.nio.file.Paths;

public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        PlaywrightFactory.start();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && PlaywrightFactory.isStarted()) {
                page().screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("target", "screenshots", result.getMethod().getMethodName() + ".png"))
                        .setFullPage(true));
            }
        } finally {
            PlaywrightFactory.stop();
        }
    }

    protected Page page() {
        return PlaywrightFactory.getPage();
    }
}
