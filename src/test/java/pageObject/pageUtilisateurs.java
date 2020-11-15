package pageObject;

import element.Element;
import element.Form;
import org.openqa.selenium.By;
import util.TestProperties;

public class pageUtilisateurs {
    private static String url = TestProperties.baseURL + "/projects/users/index";
    private static Element inputNewUser = new Element("input nouvel utilisateur", By.id("unamec"));
    private static Element ajouter = new Element("bouton ajouter", By.name("add"));

    private static Element userName = new Element("userName", By.id("uname"));
    private static Element userLogin = new Element("userLogin", By.id("ulogin"));
    private static Element userEmail = new Element("email", By.id("email"));
    private static Element userLocked = new Element("locked", By.name("locked"));
    private static Element userPwd = new Element("pwd", By.id("upwd"));
    private static Element userPwdConfirmation = new Element("pwd confirmation", By.id("upwdc"));
    private static Element userProjetCheckBox = new Element("projet utilisateur {PROJET} checkbox", By.xpath("//tr[contains(., \"{PROJET}\")]//input[@type=\"checkbox\"]"));
    private static Element userProjetRole = new Element("projet utilisateur {PROJET} role", By.xpath("//tr[contains(., \"{PROJET}\")]//select"));
    private static Element valider = new Element("valider", By.id("btnval"));

    private static Element listItemUtilisateur = new Element("item list utilisateur {USER}", By.xpath("//A[@class=\"ligthline\"][contains(., \"{USER}\")]"));

    public static void setInputNewUser(String value){inputNewUser.setValue(value);ajouter.click();}
    public static void setUserName(String value){userName.setValue(value);}
    public static void setUserLogin(String value){userLogin.setValue(value);}
    public static void setUserEmail(String value){userEmail.setValue(value);}
    public static void setUserLocked(boolean value){userLocked.check(value);}
    public static void setUserPwd(String value){userPwd.setValue(value);}
    public static void setUserPwdConfirmation(String value){userPwdConfirmation.setValue(value);}
    public static void setUserProjetCheckBox(String value){userProjetCheckBox.setParameter("{PROJET}", value).check();}
    public static void setUserProjetRole(String value){userProjetRole.setParameter("{PROJET}", value.split(">")[0]).selectInList(value.split(">")[1]);}


    public static void ajouterNouvelUtilisateur(String dataSet, int jddNum) {
        Form.runDataSet(pageUtilisateurs.class, "set", "", dataSet, jddNum);
        valider.click();
    }

    public static  void verifierPresenceUtilisateur(String value) {
        listItemUtilisateur.setParameter("{USER}", value).assertExists(false);
    }
    public static  void verifierAbsenceUtilisateur(String value) {
        listItemUtilisateur.setParameter("{USER}", value).assertNotExists(2, false);
    }

}
