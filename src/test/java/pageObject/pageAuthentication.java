package pageObject;

import driver.Driver;
import element.Element;
import org.openqa.selenium.By;
import util.TestProperties;

public class pageAuthentication {
    private static String url = TestProperties.baseURL + "/users/login?locale=fr";
    private static Element login = new Element("identifiant", By.id("login"));
    private static Element pwd = new Element("pwd", By.id("pwd"));
    private static Element valider = new Element("valider", By.id("btnvalid"));

    public static void seConnecter(String identifiant, String password) {
        Driver.goToUrl(url);
        login.setValue(identifiant);
        pwd.setValue(password);
        valider.click(pageAccueil.class, "loaded");
    }
}
