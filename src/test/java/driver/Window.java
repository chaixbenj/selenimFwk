package driver;

import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Set;

/**
 * this class allow to manage all the windows opened in a driver in order to switch between them
 */
public class Window {
    private static HashMap<WebDriver,Set<String>> driverAllWindows = new HashMap<WebDriver,Set<String>>();
    private static HashMap<String,String> windowsName = new HashMap<String,String>();

    /**
     * save all the open windows.
     * it must be called before action that will open a new windows in order to be able to switch on the new window by  comparison with the previous collection of windows
     */
    public static void saveAllOpenedWindows() {
        WebDriver driver = Driver.getCurrentDriver();
        Set<String> windows = driver.getWindowHandles();
        if (driverAllWindows.containsKey(driver)) {
            driverAllWindows.replace(driver, windows);
        } else {
            driverAllWindows.put(driver, windows);
        }
    }

    /**
     * save the current window under a name in order to switch between windows easily
     * @param windowSaveName
     */
    public static void saveCurrentWindowAs(String windowSaveName) {
        WebDriver driver = Driver.getCurrentDriver();
        windowSaveName = driver.toString() + windowSaveName;
        if (windowsName.containsKey(windowSaveName)) {
            windowsName.replace(windowSaveName, driver.getWindowHandle());
        } else {
            windowsName.put(windowSaveName, driver.getWindowHandle());
        }
    }

    /**
     * switch to the first window opened in the browser
     */
     public static void backToFirstWindow() {
        WebDriver driver = Driver.getCurrentDriver();
        driver.switchTo().window(String.valueOf(driverAllWindows.get(driver).toArray()[0]));
    }

    /**
     * switch to the new opened window when an action has opened a new one
     */
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

    /**
     * switch to the default content, if you have switched in an iframe in the window
     */
    public void switchDefault() {
        Driver.getCurrentDriver().switchTo().defaultContent();
    }

    /**
     * swith to a window with its name (must have used saveCurrentWindowAs before)
     * @param window
     */
    public void switchTo(String window) {
        String windowSaveName = Driver.getCurrentDriver().toString() + window;
        if (windowsName.containsKey(windowSaveName)) {
            Driver.getCurrentDriver().switchTo().window(windowsName.get(windowSaveName));
        } else {
            Driver.getCurrentDriver().switchTo().window(window);
        }
    }

}
