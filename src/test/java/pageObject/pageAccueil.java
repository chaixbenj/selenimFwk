package pageObject;

import driver.Driver;
import element.Element;
import element.Loader;
import org.openqa.selenium.By;
import util.TestProperties;

public class pageAccueil {
    private static String url = TestProperties.baseURL + "/projects/welcome";
    private static Element welcomeMessage = new Element("welcomeMessage", By.xpath("//div[@class\"welcomeMessage\"]"));
    private static Element lienMenu = new Element("menu {MENU}", By.xpath("//li[@class=\"welcomeLinks\"]//a[contains(., \"{MENU}\")]"));

    public static boolean loaded() {
        return Loader.waitNotVisibleAndAllElementsLoaded(new Element[] {welcomeMessage});
    }

    public static void selectMenu(String menu) {
        lienMenu.setParameter("{MENU}", menu).click();
    }
}
