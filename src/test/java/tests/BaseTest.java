package tests;

import driver.Driver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import util.TestProperties;

import java.lang.reflect.Method;
import java.net.MalformedURLException;


public class BaseTest {

    @BeforeSuite
    public  void beforeSuiteStartWD() {
        try {
            String env = System.getProperty("runEnv");
            TestProperties.loadProperties((env!=null?env:"next"));
            Driver.startOrBackToFirstDriver(this.getClass().getSimpleName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod
    public void handleTestMethodName(Method method)
    {
        Driver.getReport().initTest(method.getName());
    }

    @AfterMethod
    public void afterMethodEndTest(ITestResult result)
    {
        Driver.getReport().endTest(result.isSuccess(), String.valueOf(result.getThrowable()));
    }

    @AfterSuite
    public void closeDriver() {
        Driver.close();
        Driver.getReport().publish();
    }


}
