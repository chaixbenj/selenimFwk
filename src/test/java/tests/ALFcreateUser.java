package tests;

import org.testng.annotations.Test;
import pageObject.*;



public class ALFcreateUser extends BaseTest {
@Test(priority = 1)
    public static void ALFcreateUser()  {
        pageAuthentication.seConnecter("demo", "demoPwd!!01");
        pageAccueil.selectMenu("Utilisateurs");
        pageUtilisateurs.ajouterNouvelUtilisateur("creationUsers", 1);
        pageUtilisateurs.verifierPresenceUtilisateur("test auto");
        pageUtilisateurs.verifierAbsenceUtilisateur("test auto");
    }
}