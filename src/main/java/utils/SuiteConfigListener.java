package utils;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

public class SuiteConfigListener implements IAlterSuiteListener {

    @Override
    public void alter(List<XmlSuite> suites) {
        int threads = ConfigReader.getInt("threads");
        suites.forEach(suite -> suite.setThreadCount(threads));
    }
}
