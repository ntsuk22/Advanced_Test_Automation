package utils;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

/**
 * Applies the configurable thread count (config.properties / -Dthreads) to every suite before it starts,
 * so parallelism can be tuned without editing testng.xml.
 */
public class SuiteConfigListener implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        int threads = ConfigReader.getInt("threads");
        suites.forEach(suite -> suite.setThreadCount(threads));
    }
}
