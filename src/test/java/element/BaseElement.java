package element;

import driver.Driver;
import util.TestProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


class BaseElement {
    private String name;
    private String notValuedName;
    private By locator;
    private By notValuedLocator;
    private BaseElement container;

    private static LocalDateTime startT;
    //private static Element fermerPopupTraitementPeutPasAboutir = new Element("fermer popup peut pas aboutir", By.xpath("//div[@class=\"modal ng-scope\"][contains(., \"" + ("Votre_traitement_ne_peut_aboutir") + "\")]/div/button[contains(., \"" + ("Fermer") + "\")]"));
    //private static Element fermerPopupTutoriel = new Element("fermer popup tutoriel", By.xpath("//div[@class=\"modal ng-scope\"][contains(., '" + ("Bienvenue") + "')]/div[@class=\"modal-footer\"]/div[@class=\" pull-right\"]/button[@ng-click=\"close()\"]"));


    /**
     * @param elementName    : nom / description de l'élément
     * @param elementLocator : information d'identification de l'élément. Exemple : By.id("idelement"), By.name("elementName"), By.xpath("//a[@class='toto']")
     */
    public BaseElement(String elementName, By elementLocator) {
        name = elementName;
        notValuedName = elementName;
        locator = elementLocator;
        notValuedLocator = elementLocator;
        container = null;
    }

    public void setName(String elementName) {
        name = elementName;
        notValuedName = elementName;
    }

    public void setLocator(By elementLocator) {
        locator = elementLocator;
        notValuedLocator = elementLocator;
    }

    public String getName() {
        return name;
    }

    public By getLocator() {
        return locator;
    }

    /**
     * Définit dans quel élement on doit chercher l'élément, si non setté on le recherchera dans tout le body
     * par exemple element.click() cliquera sur le premier élément correspondant dans toute la page quand element.setContainer(parent).click() cliquera sur le premier élément correspondant dans l'élément parent
     *
     * @param containerElement : élément contenant l'élément recherché
     * @return l'élément recherché dans le container
     */
    public BaseElement setBaseContainer(BaseElement containerElement) {
        container = containerElement;
        String locatorPath = locator.toString();
        if (container != null && locatorPath.contains("By.xpath: ")) {
            //locatorPath = locatorPath.replaceAll("By.xpath: ", "");
            locatorPath = locatorPath.substring(locatorPath.indexOf(": ")+2);
            if (locatorPath.startsWith("//")) {
                locator = By.xpath("." + locatorPath);
            }
            if (locatorPath.startsWith("(//")) {
                locator = By.xpath("(." + locatorPath.substring(1));
            }
        }
        return this;
    }

    /**
     * Remet à null le container de l'élément pour le chercher das tout le body dans tout le body
     *
     * @return l'élément recherché dans le container
     */
    public BaseElement resetBaseContainer() {
        container = null;
        String locatorPath = locator.toString();
        if (locatorPath.contains("By.xpath: ")) {
            //locatorPath = locatorPath.replaceAll("By.xpath: ", "");
            locatorPath = locatorPath.substring(locatorPath.indexOf(": ")+2);
            if (locatorPath.startsWith("//")) {
                locator = By.xpath(locatorPath.substring(1));
            }
            if (locatorPath.startsWith("(.//")) {
                locator = By.xpath("(" + locatorPath.substring(2));
            }
        }
        return this;
    }

    /**
     * Renvoi le container dans lequel on doit rechercher l'élément
     *
     * @return le container dans lequel on doit rechercher l'élément
     */
    public BaseElement getBaseContainer() {
        return container;
    }

    /**
     * Si le elementLocator contient plusieurs variables, par exemple By.xpath("//button[@class='MA_CLASSE'][contains(.,'MA_VARIABLE')]"),
     * utiliser cette méthode pour les valoriser avant de faire une action sur l'élément :
     * mon_element.setParamater(new String[] {"MA_CLASS","success_button", "MA_VARIABLE", "Enregister"});
     * mon_element.click();
     *
     * @param params : tableau des variables et de leur valorisation {var1, value1, var2, value2, var3, value3, ...}
     * @return l'element paramétré
     */
    public BaseElement setBaseParameter(String[] params) {
        String path = notValuedLocator.toString();
        name = notValuedName;
        for (int i = 0; i < params.length / 2; i++) {
            path = path.replace(params[2 * i], params[2 * i + 1]);
            name = name.replace(params[2 * i], params[2 * i + 1]);
        }
        System.out.println(path);
        setLocatorFromString(path);
        return this;
    }

    /**
     * Si le elementLocator contient une variable et une seule, par exemple By.xpath("//button[contains(.,'MA_VARIABLE')]"),
     * utiliser cette méthode pour la valoriser avant de faire une action sur l'élément :
     * mon_element.setParamater("MA_VARIABLE", "Enregister");
     * mon_element.click();
     *
     * @param key   : nom de la variable dans le locator
     * @param value : valeur de remplacement de la key dans le locator (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @return l'element paramétré
     */
    public BaseElement setBaseParameter(String key, String value) {
        name = name.replace(key, (value));
        setLocatorFromString(notValuedLocator.toString().replace(key, (value)));
        return this;
    }

    private void setLocatorFromString(String path) {
        if (path.contains("By.xpath: ")) {
            locator = By.xpath(path.replaceAll("By.xpath: ", ""));
        }
        if (path.contains("By.id: ")) {
            locator = By.id(path.replaceAll("By.id: ", ""));
        }
        if (path.contains("By.name: ")) {
            locator = By.name(path.replaceAll("By.name: ", ""));
        }
        setBaseContainer(container);
    }

    /**
     * retourne le locator de l'élément format xPath;
     *
     * @return : element xpath
     */
    public String locatorXpath() {
        String locatorPath = locator.toString();
        if (locatorPath.contains("By.xpath: ")) {
            locatorPath = locatorPath.replaceAll("By.xpath: ", "");
        }
        if (locatorPath.contains("By.id: ")) {
            locatorPath = "//*[@id='" + locatorPath.replaceAll("By.id: ", "") + "']";
        }
        if (locatorPath.contains("By.name: ")) {
            locatorPath = "//*[@name='" + locatorPath.replaceAll("By.name: ", "") + "']";
        }
        return locatorPath;
    }


    public WebElement findElement() {
        return findElement(TestProperties.implicit_wait);
    }

    public WebElement findElement(int timeout) {
        //System.out.println(">>>>>>>>>>>>>findElement " + element.name);
        WebElement mon_element = findElementNoScrollBefore(timeout);
        if (mon_element != null) scrollElement(this.getClass().getSimpleName(), mon_element);
        return mon_element;
    }

    public WebElement findElementNoScrollBefore() {
        return findElementNoScrollBefore(TestProperties.implicit_wait);
    }

    public WebElement findElementNoScrollBefore(int timeout) {
        Loader.waitNotVisible();
        startT();
        fermerPopupParasite();
        //System.out.println(">>>>>>>>>>>>>findElementNoScrollBefore " + element.name);
        By locator = this.getLocator();
        BaseElement container = this.getBaseContainer();
        try {
            WebElement mon_element;
            (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );
            if (container == null) {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.presenceOfElementLocated(locator)
                );
            } else {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.presenceOfNestedElementLocatedBy(container.getLocator(), locator)
                );
            }
            //System.out.println("=============> " + searchTime() + " ms");
            return mon_element;
        } catch (Exception e) {
            //System.out.println("=============> " + searchTime() + " ms");
            return null;
        }
    }

    public List<WebElement> findAllElements() {
        return findAllElements(TestProperties.implicit_wait);
    }

    public List<WebElement> findAllElements(int timeout) {
        Loader.waitNotVisible();
        startT();
        fermerPopupParasite();
        //System.out.println(">>>>>>>>>>>>>findAllElements " + element.name);
        By locator = this.getLocator();
        List<WebElement> mon_element = null;
        BaseElement container = this.getBaseContainer();
        try {
            if (container == null) {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(locator)
                );
            } else {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.presenceOfNestedElementsLocatedBy(container.getLocator(), locator)
                );
            }
            //System.out.println("=============> " + searchTime() + " ms");
            return mon_element;
        } catch (Exception e) {
            //System.out.println("=============> " + searchTime() + " ms");
            return mon_element;
        }
    }

    public WebElement findElementEnabled() {
        Loader.waitNotVisible();
        startT();
        fermerPopupParasite();
        //System.out.println(">>>>>>>>>>>>>findElementEnabled " + element.name);
        By locator = this.getLocator();
        BaseElement container = this.getBaseContainer();
        WebElement mon_element = null;
        try {
            if (container == null) {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.implicit_wait)).until(
                        ExpectedConditions.elementToBeClickable(locator)
                );
            } else {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.implicit_wait)).until(
                        ExpectedConditions.elementToBeClickable(
                                (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.implicit_wait)).until(
                                        ExpectedConditions.presenceOfNestedElementLocatedBy(container.getLocator(), locator)
                                ))
                );
            }
            if (mon_element != null) scrollElement(this.getClass().getSimpleName(), mon_element);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("=============> " + searchTime() + " ms");
        return mon_element;
    }


    public WebElement findElementDisplayed() {
        return findElementDisplayedMaxWait(TestProperties.implicit_wait);
    }

    public WebElement findElementDisplayedMaxWait(int timeout) {
        //System.out.println(">>>>>>>>>>>>>findElementDisplayedMaxWait " + element.name);
        WebElement mon_element = findElementDisplayedNoScrollBeforeMaxWait(timeout);
        if (mon_element!=null) scrollElement(this.getClass().getSimpleName(), mon_element);
        return mon_element;
    }

    public WebElement findElementDisplayedNoScrollBeforeMaxWait(int timeout) {
        Loader.waitNotVisible();
        startT();
        fermerPopupParasite();
        //System.out.println(">>>>>>>>>>>>>findElementDisplayedNoScrollBeforeMaxWait " + element.name);
        By locator = this.getLocator();
        BaseElement container = this.getBaseContainer();
        try {
            WebElement mon_element;
            if (container == null) {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                );
            } else {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                        ExpectedConditions.visibilityOf(
                                (new WebDriverWait(Driver.getCurrentDriver(), timeout)).until(
                                        ExpectedConditions.presenceOfNestedElementLocatedBy(container.getLocator(), locator)
                                ))
                );
            }
            //System.out.println("=============> " + searchTime() + " ms");
            return mon_element;
        } catch (Exception e) {
            //System.out.println("=============> " + searchTime() + " ms");
            return null;
        }
    }

    WebElement findElementRightNow() {
        return findElementDisplayedMaxWait(0);
    }


    // PRIVATE -------------------------------------------------------------------------------------------------
    private void scrollElement(String elementClass, WebElement element) {
        /*try {
            if (element != null) {
                // si l'élément est hors cadre on scroll sur l'élément ou si c'est un dropdown
                int y = element.getLocation().getY();
                long scroll = (long) WebDriver.driver.executeScript("return window.pageYOffset;");
                int hauteurEcran = WebDriver.driver.manage().window().getSize().height;
                if (!elementClass.equals("Element") && !elementClass.equals("CalendarElement")) {
                    WebDriver.driver.executeScript("arguments[0].scrollIntoView(true);window.scrollBy(0 , -300);", element);
                } else {
                    if (Math.abs(y - scroll) < (300) || Math.abs(y - scroll) > (hauteurEcran - 400)) {
                        WebDriver.driver.executeScript("window.scrollBy(0 , " + (y - scroll - 300) + ");");
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }*/
    }

    private void fermerPopupParasite() {
        /*try {
            WebElement mon_element = (new WebDriverWait(WebDriver.driver, 0)).until(
                    ExpectedConditions.presenceOfElementLocated(fermerPopupTutoriel.getLocator())
            );
            if (mon_element!=null) mon_element.click();
        } catch (Exception e) {
            //do nothing
        }
        try {
            WebElement mon_element = (new WebDriverWait(WebDriver.driver, 0)).until(
                    ExpectedConditions.presenceOfElementLocated(fermerPopupTraitementPeutPasAboutir.getLocator())
            );
            if (mon_element!=null) mon_element.click();
        } catch (Exception e) {
            //do nothing
        }*/
    }

    private static void startT() {
        startT = LocalDateTime.now();
    }

    private static long searchTime() {
        return  ChronoUnit.MILLIS.between(startT, LocalDateTime.now());
    }

}
