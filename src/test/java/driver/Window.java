package driver;

import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Set;

public class Window {
    private static HashMap<WebDriver,Set<String>> driverAllWindows = new HashMap<WebDriver,Set<String>>();
    private static HashMap<String,String> windowsName = new HashMap<String,String>();

    public static void saveAllOpenedWindows() {
        WebDriver driver = Driver.getCurrentDriver();
        Set<String> windows = driver.getWindowHandles();
        if (driverAllWindows.containsKey(driver)) {
            driverAllWindows.replace(driver, windows);
        } else {
            driverAllWindows.put(driver, windows);
        }
    }

    public static void saveCurrentWindowAs(String windowSaveName) {
        WebDriver driver = Driver.getCurrentDriver();
        windowSaveName = driver.toString() + windowSaveName;
        if (windowsName.containsKey(windowSaveName)) {
            windowsName.replace(windowSaveName, driver.getWindowHandle());
        } else {
            windowsName.put(windowSaveName, driver.getWindowHandle());
        }
    }

    public static void backToWindow(String windowSaveName) {
        WebDriver driver = Driver.getCurrentDriver();
        windowSaveName = driver.toString() + windowSaveName;
        driver.switchTo().window(windowsName.get(windowSaveName));
    }

    public static void backToFirstWindow() {
        WebDriver driver = Driver.getCurrentDriver();
        driver.switchTo().window(String.valueOf(driverAllWindows.get(driver).toArray()[0]));
    }


    public static void switchToNewOpenedWindow() {
        WebDriver driver = Driver.getCurrentDriver();
        Set<String> allWindows = driverAllWindows.get(driver);
        int i = 0;
        while (allWindows.size()==driver.getWindowHandles().size() && i < 30) {
            i++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (allWindows.size()<driver.getWindowHandles().size()) {
            for(String item : driver.getWindowHandles()) {
                if (!allWindows.contains(item)) driver.switchTo().window(item);
            }
        }
        saveAllOpenedWindows();
    }

    public void switchDefault() {
        Driver.getCurrentDriver().switchTo().defaultContent();
    }

    public void switchTo(String window) {
        String windowSaveName = Driver.getCurrentDriver().toString() + window;
        if (windowsName.containsKey(windowSaveName)) {
            Driver.getCurrentDriver().switchTo().window(windowsName.get(windowSaveName));
        } else {
            Driver.getCurrentDriver().switchTo().window(window);
        }
    }

}
