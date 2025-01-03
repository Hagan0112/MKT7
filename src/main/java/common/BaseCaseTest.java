package common;

import ObjectPage.LandingPage;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import driver.DriverManager;
import driver.DriverProperties;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import utils.ScreenShotUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.UnexpectedException;

public class BaseCaseTest {
    public static ExtentTest logger;
    public static DriverProperties driverProperties;
    public static BrowserMobProxy browserMobProxy;
    public static ExtentReports report;
    private static ApplicationContext context;
    public static String urlOriginal;
    public static LandingPage landingPage;

    @BeforeSuite(alwaysRun = true)
    public static void beforeSuite() {
        try {
            context = new ClassPathXmlApplicationContext("resources/settings/Setting.xml");
            report = new ExtentReports("", true);
        } catch (Exception e) {
            throw new NullPointerException(String.format("ERROR: Exception occurs beforeSuite by '%s'", e.getMessage()));
        }
    }

    @Parameters({"browser", "appname"})
    @BeforeClass(alwaysRun = true)
    public void beforeClass(String browser, String appname) {
        driverProperties = (DriverProperties) context.getBean(browser);
        urlOriginal = defineUrl(appname);
    }

    @Parameters({"appname", "isProxy"})
    @BeforeMethod(alwaysRun = true)
    public static void beforeMethod(String appname, boolean isProxy, Method method) {
        System.out.println("**************************** Beginning TC's " + method.getName() + " ****************************");
        logger = report.startTest(method.getName(), method.getClass().getName());
        driverProperties.setMethodName(method.getName());
        driverProperties.setIsProxy(isProxy);
        try {
            createDriver(urlOriginal);
            landingPage = new LandingPage(appname);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnexpectedException e) {
            throw new RuntimeException(e);
        }
        if (isProxy) {
            browserMobProxy = driverProperties.getBrowserMobProxy();
        }
    }

    @AfterMethod(alwaysRun = true)
    public static void afterMethod(ITestResult result, ITestContext ctx) throws IOException {
        String testResult = "PASSED";
        if (!result.isSuccess()) {
            String srcBase64 = ScreenShotUtils.captureScreenshotWithBase64(DriverManager.getDriver().getWebDriver());
            result.setAttribute(result.getMethod().getMethodName(), srcBase64);
            testResult = "FAILED";
//            System.err.println(result.getThrowable().getMessage());
        }
        if (driverProperties.isProxy()) {
            log("INFO: Quitting BrowserMobProxy's port is " + browserMobProxy.getPort());
            browserMobProxy.stop();
        }
        DriverManager.quitAll();
        System.out.println("**************************** Ending TC's name: " + result.getMethod().getMethodName() + " is " + testResult + " ****************************");
    }

    protected static void createDriver(String url) throws MalformedURLException, UnexpectedException {
        int count = 3;
        DriverManager.quitAll();
        while (count-- > 0) {
            System.out.println("driver Properties" + driverProperties.getBrowserName());
            DriverManager.createWebDriver(driverProperties);
            DriverManager.getDriver().setLoadingTimeOut(100);

            if (DriverManager.getDriver().getToAvoidTimeOut(url) || count == 0) {
                log(String.format("RUNNING under the link %s", url));
                log(String.format("DEBUG: CREATED DRIVER SUCCESSFULLY with COUNT %s and Map Size %s", count, DriverManager.driverMap.size()));
                System.out.print(String.format("Width x Height is %sx%s\n", DriverManager.getDriver().getWidth(), DriverManager.getDriver().getHeight()));
                break;
            } else {
                log("DEBUG: QUIT BROWSER DUE TO NOT CONNECTED");
                DriverManager.quitAll();
            }
        }
    }

    public static String defineUrl(String appname) {
        return Contants.URL_MAINSITE_FOLLOW_APP_NAME.get(appname);
    }

    public static String log(String message) {
        logger.log(LogStatus.INFO, message);//For extentTest HTML report
        System.out.println(message);
        Reporter.log(message);
        return message;
    }

    public static String getDownloadPath() {
        return DriverManager.getDriver().getDriverSetting().getDownloadPath();
    }

}
