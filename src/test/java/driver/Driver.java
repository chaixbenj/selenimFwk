package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.WebDriver;
import util.TestProperties;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Driver {
    private static HashMap<String,WebDriver> allDrivers = new HashMap<String,WebDriver>();
    private static HashMap<WebDriver,String> connectedUser = new HashMap<WebDriver,String>();
    private static WebDriver mainDriver;
    private static WebDriver currentDriver;
    private static Reporter report;

    public static JavascriptExecutor JSExecutor() {
        return (JavascriptExecutor) currentDriver;
    }

    public static Reporter getReport() {
        return report;
    }

    public static void saveCurrentDriverAs(String webDriverName) {
        allDrivers.put(webDriverName, currentDriver);
    }

    public static void setConnectedUser(String userName) {
        if (!connectedUser.containsKey(currentDriver)) {
            connectedUser.put(currentDriver, userName);
        } else {
            connectedUser.replace(currentDriver, userName);
        }
    }

    public static String getConnectedUser() {
        try {
            return connectedUser.get(currentDriver);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isConnectedUser(String user) {
        return getConnectedUser()!=null && getConnectedUser().equals(user);
    }

    public static WebDriver getCurrentDriver() {
        return currentDriver;
    }

    public static void renameCurrentDriver(String newName) {
        for (Map.Entry<String, WebDriver> nameDriver : allDrivers.entrySet()) {
            if (nameDriver.getValue().equals(currentDriver)) {
                allDrivers.remove(nameDriver.getKey());
                allDrivers.put(newName, currentDriver);
                break;
            }
        }
    }

    public static void startNewDriver(String webDriverName) {
        switch (TestProperties.browser) {
            case "chrome":
                HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", Paths.get("").toAbsolutePath().toString() + File.separator + TestProperties.download_rep.replace("/", File.separator));
                chromePrefs.put("download.prompt_for_download", false);
                chromePrefs.put("download.directory_upgrade", true);
                chromePrefs.put("safebrowsing.enabled", true);
                chromePrefs.put("safebrowsing.disable_download_protection", true);
                ChromeOptions choptions = new ChromeOptions();
                choptions.setExperimentalOption("prefs", chromePrefs);
                //choptions.addArguments("--headless");
                choptions.addArguments("--no-sandbox" , "--disable-dev-shm-usage", "--disable-gpu", "--disable-extensions");
                WebDriverManager.chromedriver().setup();
                currentDriver = new ChromeDriver(choptions);
                break;
            case "firefox":
                FirefoxOptions ffoptions = new FirefoxOptions();
                ffoptions.addPreference("browser.download.folderList", 2);
                ffoptions.addPreference("browser.download.dir", Paths.get("").toAbsolutePath().toString() + File.separator + TestProperties.download_rep.replace("/", File.separator));
                ffoptions.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
                ffoptions.addPreference("browser.download.manager.showWhenStarting", false);
                ffoptions.addPreference("pdfjs.disabled", true);
                WebDriverManager.firefoxdriver().setup();
                currentDriver = new FirefoxDriver(ffoptions);
                break;
        }
        currentDriver.manage().timeouts().implicitlyWait(0, TimeUnit.MICROSECONDS);
        currentDriver.manage().window().setSize(new Dimension(TestProperties.browser_width, TestProperties.browser_heigth));
        currentDriver.manage().deleteAllCookies();
        saveCurrentDriverAs(webDriverName);
        Window.saveAllOpenedWindows();
    }

    public static void startOrBackToFirstDriver(String suiteName) throws MalformedURLException {
        if (mainDriver ==null) {
            startNewDriver("main");
            mainDriver = currentDriver;
            Window.saveAllOpenedWindows();
            report = new Reporter(suiteName);
        } else {
            currentDriver = mainDriver;
            Window.backToFirstWindow();
        }
    }

    public static void switchDriver(String webDriverName) {
        if (allDrivers.containsKey(webDriverName)) {
            currentDriver = allDrivers.get(webDriverName);
        } else {
            startNewDriver(webDriverName);
        }
    }

    public static void close() {
        currentDriver.quit();
        currentDriver = null;
        report.publish();
    }

    public static void goToUrl(String url) {
        currentDriver.get(url);
    }

    public static void refresh() {
        currentDriver.navigate().refresh();
    }
}