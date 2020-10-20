package element;

import driver.Driver;
import util.TestProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Element extends BaseElement {
    private static HashMap<String, LocalDateTime> dateStartSearch = new HashMap<String, LocalDateTime>();


    /**
     * @param elementName    : nom / description de l'élément
     * @param elementLocator : information d'identification de l'élément. Exemple : By.id("idelement"), By.name("elementName"), By.xpath("//a[@class='toto']")
     */
    public Element(String elementName, By elementLocator) {
        super(elementName, elementLocator);
    }

    public Element setContainer(Element containerElement) {
        return (Element) setBaseContainer((BaseElement) containerElement);
    }
    public Element resetContainer() {
        return (Element) resetBaseContainer();
    }
    public Element getContainer() {
        return (Element) getBaseContainer();
    }
    public Element setParameter(String[] params) {
        return (Element) setBaseParameter(params);
    }
    public Element setParameter(String key, String value) {
        return (Element) setBaseParameter(key, value);
    }

    static void startSearch(String from) {
        if (dateStartSearch.containsKey(from)) {
            dateStartSearch.remove(from);
        }
        dateStartSearch.put(from, LocalDateTime.now());
    }

    static boolean stopSearch(int timeout, String from) {
        if (dateStartSearch.get(from).plusSeconds(timeout).isAfter(LocalDateTime.now())) {
            return false;
        } else {
            return true;
        }
    }

    boolean pageloaded(Class pageObjectToBeLoaded, String assertLoadedMethod) {
        boolean loaded = false;
        boolean hasLoadedMethod = false;
        for (final Method method : pageObjectToBeLoaded.getMethods()) {
            if (method.getName().equals(assertLoadedMethod)) {
                if (method.getParameterCount() == 0) {
                    hasLoadedMethod = true;
                    try {
                        loaded = (boolean) method.invoke(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return (hasLoadedMethod ? loaded : true);
    }



    /**
     * retourne ton père Luc;
     *
     * @return le selenium WebElement père de l'élément
     */
    public WebElement getFather() {
        return findElement().findElement(By.xpath(".."));
    }

    /**
     * retourne ton père Luc, non ton grand-père, non ton arrière grand-père. Attend tu m'as dit remonter jusqu'à quand ? 3, alors ton arrière grand-perè;
     *
     * @param nbFather : Nieme de degré de parentalité
     * @return le selenium WebElement parent au Nieme degré de l'élément
     */
    public WebElement getFather(int nbFather) {
        WebElement element = findElement();
        for (int i = 0; i < nbFather; i++) {
            element = element.findElement(By.xpath(".."));
        }
        return element;
    }

    /**
     * retourne "noir" pour nbFather=1 et attr="couleur-casque", Luc;
     *
     * @param nbFather : Nieme de degré de parentalité
     * @param attr     : nom de l'attribut
     * @return le selenium WebElement parent au Nieme degré de l'élément
     */
    public String getFatherAttribute(int nbFather, String attr) {
        return getFather(nbFather).getAttribute(attr);
    }


    /**
     * retourne le nombre de WebElement selenium correspondant à la description de l'élément dans un délai de : timeout secondes;
     *
     * @param timeout : timeout en seconde
     * @return le nombre de selenium WebElements correspondant au locator de l'élément
     */
    public int getElementsNumber(int timeout) {
        try {
            return findAllElements(timeout).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * indique si un élément est affiché dans un délai de : timeout secondes;
     *
     * @param timeout : timeout en seconde
     * @return true si existe, false sinon
     */
    public boolean exists(int timeout) {
        if (findElementNoScrollBefore((int) timeout) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * indique si l'élément est enabled a l'écran dans un délai de : timeout secondes;
     *
     * @param timeout : timeout en seconde
     * @return true si displayed, false sinon
     */
    public boolean isEnabled(int timeout) {
        try {
            return findElementDisplayedNoScrollBeforeMaxWait(timeout).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * indique si l'élément est enabled a l'écran dans un délai de : 1 seconde;
     *
     * @return true si displayed, false sinon
     */
    public boolean isEnabled() {
        try {
            return findElementDisplayedNoScrollBeforeMaxWait(1).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * indique si l'élément est visible a l'écran dans un délai de : timeout secondes;
     *
     * @param timeout : timeout en seconde
     * @return true si displayed, false sinon
     */
    public boolean isDisplayed(int timeout) {
        try {
            return findElementDisplayedNoScrollBeforeMaxWait(timeout).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * indique si l'élément est visible a l'écran dans un délai de : 1 seconde;
     *
     * @return true si displayed, false sinon
     */
    public boolean isDisplayed() {
        try {
            return findElementDisplayedNoScrollBeforeMaxWait(1).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * indique si un élément est coché dans un délai de : test_env.properties#implicit_wait secondes;
     * @return true si coché, false sinon
     */
    public boolean isChecked() {
        System.out.println("isChecked " + this.getName());
        boolean checked;
        Element input = new Element("input", By.xpath("//input")).setContainer(this);
        if ((getAttribute("checked") != null && getAttribute("checked").equals("true"))
                ||
                (input.exists(0) && input.getAttribute("checked") != null && input.getAttribute("checked").equals("true"))) {
            checked = true;
        } else {
            checked = false;
        }
        return checked;
    }

    /**
     * indique si l'élément est chargé dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @return true si chargé, false sinon
     */
    public boolean loaded() {
        return loaded(TestProperties.implicit_wait);
    }

    /**
     * indique si l'élément n'est pas chargé dans un délai de : 60 secondes;
     *
     * @return true si non chargé, false sinon
     */
    public boolean unloaded() {
        startSearch("unloaded");
        while (findElementDisplayedNoScrollBeforeMaxWait(0) != null && !stopSearch(60, "unloaded")) {
        }
        ;
        return findElementDisplayedNoScrollBeforeMaxWait(0) == null;
    }

    /**
     * indique si l'élément est chargé dans un délai de : timeout secondes;
     *
     * @param timeout : timout en seconde
     * @return true si chargé, false sinon
     */
    public boolean loaded(int timeout) {
        boolean elementLoaded = true;
        WebElement el = findElement(timeout);
        if (el != null) {
            try {
                startSearch("loaded");
                while (!(Boolean) Driver.JSExecutor().executeScript("return arguments[0].complete", el) && !stopSearch(timeout, "loader")) {
                    elementLoaded = false;
                }
                if ((Boolean) Driver.JSExecutor().executeScript("return arguments[0].complete", el))
                    elementLoaded = true;
            } catch (Exception e) {
                elementLoaded = true;
            }
        } else {
            elementLoaded = false;
        }
        return elementLoaded;
    }



    /**
     * fait un mouseover sur l'élément qu'il est displayed dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void mouseOver() {
        System.out.println("mouseOver " + this.getName());
        String result = null;
        String errorMessage = null;
        try {
            WebElement element = findElementDisplayed();
            Actions action = new Actions(Driver.getCurrentDriver());
            action.moveToElement(element, 0, 0).build().perform();
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
            result = "error";
        }
        Driver.getReport().log(result, "mouseOver", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * renvoi la valeur d'élément dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @return la valeur si trouvée, "not found" si pas trouvé. Le résultat est tracé dans le rapport.
     */
    public String getValue() {
        return getValue(TestProperties.implicit_wait);
    }

    /**
     * renvoi la valeur d'élément dans un délai de : timeout secondes;
     *
     * @param timeout : timeout en seconde
     * @return la valeur si trouvée, "not found" si pas trouvé. Le résultat est tracé dans le rapport.
     */
    public String getValue(int timeout) {
        System.out.println("getValue " + this.getName());
        String elementValue;
        try {
            WebElement element = findElement(timeout);
            if (!element.isDisplayed()) {
                return ((String) Driver.JSExecutor().executeScript("return arguments[0].innerText", element)).trim();
            }else{
                if (element.getTagName().toUpperCase().equals("INPUT") || element.getTagName().toUpperCase().equals("TEXTAREA")) {
                    elementValue = ((String)Driver.JSExecutor().executeScript("return arguments[0].value", element)).trim();
                    System.out.println("value " + elementValue);
                } else {
                    if (element.getTagName().toUpperCase().equals("SELECT")) {
                        Select select = new Select(Driver.getCurrentDriver().findElement(this.getLocator()));
                        WebElement option = select.getFirstSelectedOption();
                        if (option == null) {
                            elementValue = "";
                        } else {
                            elementValue = option.getText().trim();
                        }
                        System.out.println("value " + elementValue);
                    } else {
                        elementValue = element.getText().trim();
                        System.out.println("texte " + elementValue);
                    }
                }
            }
        } catch (Exception e) {
            elementValue = "not found";
        }
        return String.valueOf(elementValue);
    }

    /**
     * renvoi la valeur d'un attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param attribute : l'attribut de l'élément
     * @return la valeur si trouvée, "not found" si pas trouvé. Le résultat est tracé dans le rapport.
     */
    public String getAttribute(String attribute) {
        String attrVal;
        try {
            attrVal = String.valueOf(findElement().getAttribute(attribute));
            System.out.println("getAttribute " + this.getName() + " >> " + attribute + " => " + attrVal);
        } catch (Exception e) {
            attrVal = "not found";
        }
        return String.valueOf(attrVal);
    }

    /**
     * renvoi la position d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @return [X, Y] position de l'élément
     */
    public int[] getXY() {
        int x = 0;
        int y = 0;
        int[] xy = new int[2];
        WebElement element = findElement();
        if (element != null) {
            x = element.getLocation().getX();
            y = element.getLocation().getY();
        }
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    /**
     * renvoi la dimension d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @return [X, Y] dimension de l'élément
     */
    public int[] getDimension() {
        int x = 0;
        int y = 0;
        int[] xy = new int[2];
        try {
            WebElement element = findElement();
            if (element != null) {
                Dimension dim = element.getSize();
                x = dim.width;
                y = dim.height;
            }
        } catch (Exception e) {
            System.out.println("!!! ERROR --> " + e.getMessage());
        }
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    /////////////// ACTION
    /**
     * saisi une valeur dans l'élément des qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     * @param value : valeur a saisir (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void setValue(String value) {
        if (value != null) {
            //
            String action = "setValue >> " + value + " ";
            String result = "error";
            WebElement element = findElementEnabled();
            System.out.println(this);
            System.out.println(this.getName());
            System.out.println(this.getLocator());
            String errorMessage = null;
            int i = 0;
            while (i<10 && !result.equals("pass")) {
                try {
                    element.clear();
                    Loader.waitUntilJSReady();
                    element.sendKeys(value);
                    Loader.waitUntilJSReady();
                    element.sendKeys(Keys.chord(Keys.TAB));
                    result = "pass";
                } catch (Exception e2) {
                    element = findElement(0);
                    errorMessage = e2.getMessage();
                }
                i++;
            }
            Driver.getReport().log(result, action, this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
        }
    }

    /**
     * click sur l'élément des qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     * reclick jusqu'à ce que la page pageObjectToBeLoadedAfterClick soit chargée (la méthode loaded() renvoyant true quand page chargé doit être codée dans la classe pageObjects de la page)
     * Le résultat est tracé dans le rapport.
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void click(Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        startSearch("clickpageObjectToBeLoadedAfterClick");
        click();
        while(exists(1) && !pageloaded(pageObjectToBeLoadedAfterClick, assertLoadedMethod) && !stopSearch(300,"clickpageObjectToBeLoadedAfterClick")) {click();};
    }

    /**
     * click sur l'élément des qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     * reclick jusqu'à ce que la page pageObjectToBeLoadedAfterClick soit chargée (la méthode loaded() renvoyant true quand page chargé doit être codée dans la classe pageObjects de la page)  tout en recliquant sur elementToReclickBeforeIfFail si l'élément à cliquer n'est pas visible
     * Le résultat est tracé dans le rapport.
     * @param elementToReclickBeforeIfFail : element sur lequel cliquer avant de retenter un click en cas d'echec
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void click(Element elementToReclickBeforeIfFail, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        startSearch("clickpageObjectToBeLoadedAfterClickAndClickBefore");
        click(elementToReclickBeforeIfFail);
        while(exists(1) && !pageloaded(pageObjectToBeLoadedAfterClick, assertLoadedMethod) && !stopSearch(300,"clickpageObjectToBeLoadedAfterClickAndClickBefore")) {
            click(elementToReclickBeforeIfFail);
        }
    }


    /**
     * click sur l'élément des qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void click() {
        //WebDriver.getCurrentBrowserTabs().beforeSwitch();
        System.out.println("click " + this.getName());
        String result = "error";
        String errorMessage = null;
        startSearch("click");
        int i = 0;
        findElementEnabled();
        while (i<10 && !result.equals("pass")) {
            errorMessage = null;
            try {
                findElement(0).click();
                result = "pass";
            }
            catch (Exception e) {
                errorMessage += e.getMessage() + e.toString();
            }
            i++;
        }
        Driver.getReport().log(result, "click", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * click sur l'élément dès qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     * Si le click fail, click sur l'élément indique en parametre avant de retenter un click sur l'élément.
     * Le résultat est tracé dans le rapport.
     * @param elementToReclickBeforeIfFail : element sur lequel cliquer avant de retenter un click en cas d'echec
     */
    public void click(Element elementToReclickBeforeIfFail) {
        System.out.println("click " + this.getName() + " avec click before " + elementToReclickBeforeIfFail.getName());
        startSearch("clickelementToReclickBeforeIfFail");
        while (!isEnabled(2) && !stopSearch(60,"clickelementToReclickBeforeIfFail") && elementToReclickBeforeIfFail.isEnabled(0)) {
            elementToReclickBeforeIfFail.clickIfPossible();
        }
        click();
    }

    /**
     * selectionne une option dans une liste de type select ou UL/LI
     * @param option : option à choisir
     */
    public void selectInList(String option) {
        if (option!=null) {
            String result = "error";
            String errorMessage = "";
            try {
                if (option != null) {
                    this.findElementNoScrollBefore();
                    int i = 0;
                    while (!this.getValue().contains(option) && i < 10) {
                        this.clickIfPossible();
                        Element optionElement = new Element("Option '" + option + "'", By.xpath("li[contains(.,\"" + option + "\")]|option[contains(.,\"" + option + "\")]")).setContainer(this);
                        optionElement.clickIfPossible();
                        i++;
                    }
                    result = (this.getValue().contains(option)?"pass":"fail");
                    System.out.println("select " + result + this.getName() + "=>" + option);
                }
            } catch (Exception e) {
                System.out.println("select error " + this.getName() + "=>" + option);
                System.out.println(e.getMessage());
                errorMessage = e.getMessage();
            }
            Driver.getReport().log(result, "selectInList >> " + option + "(" + option + ")", this.getName(), null , "", errorMessage);
        }
    }


    /**
     * click sur le fils numero numeroSousElement de l'élément
     * @param numeroSousElement numero du sous élément
     */
    public void clickSubElementNo(String numeroSousElement) {
        Element sousElement = new Element("sous element {N} de " + this.getName(), By.xpath(this.locatorXpath() + "/*[{N}]")).setParameter("{N}", numeroSousElement);
        sousElement.click();
    }

    /**
     * click sur l'élément dès qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes; jusqu'à l'apparition d'un des éléments passés en paramètres
     * Le résultat est tracé dans le rapport.
     * @param elementToBeDisplayed : liste des éléments dont on attend qu'aux moins 1 apparaissent.
     */
    public Element clickUntilOneOfThoseElementsDisplayed(Element[] elementToBeDisplayed) {
        Element displayedElement = null;
        System.out.println("clickUntilOneOfThoseElementsDisplayed " + this.getName());
        String result = "fail";
        startSearch("clickWhileOneOfThoseElementsDisplayedelementToBeDisplayed");
        while (displayedElement==null && !stopSearch(30, "clickWhileOneOfThoseElementsDisplayedelementToBeDisplayed")) {
            click();
            for (Element element: elementToBeDisplayed
            ) {
                if (element.isDisplayed(0)) {
                    result = "pass";
                    displayedElement = element;
                    break;
                }
            }
        }
        Driver.getReport().log(result, "clickUntilOneOfThoseElementsDisplayed", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, null);
        return displayedElement;
    }

    /**
     * click sur l'élément dès qu'il est enabled dans un délai de : test_env.properties#implicit_wait secondes; jusqu'à l'apparition d'un des éléments passés en paramètres tout en recliquant sur elementToReclickBeforeIfFail si l'élément à cliquer n'est pas visible
     * Le résultat est tracé dans le rapport.
     * @param elementToReclickBeforeIfFail : element sur lequel cliquer avant de retenter un click en cas d'echec
     * @param elementToBeDisplayed : liste des éléments dont on attend qu'aux moins 1 apparaissent.
     */
    public void clickUntilOneOfThoseElementsDisplayed(Element elementToReclickBeforeIfFail, Element[] elementToBeDisplayed) {
        System.out.println("clickUntilOneOfThoseElementsDisplayed " + this.getName());
        String result = "fail";
        startSearch("clickWhileOneOfThoseElementsDisplayedelementToBeDisplayedAndClickBefore");
        boolean isOneDisplayed = false;
        while (!isOneDisplayed && !stopSearch(30, "clickWhileOneOfThoseElementsDisplayedelementToBeDisplayedAndClickBefore")) {
            click(elementToReclickBeforeIfFail);
            for (Element element: elementToBeDisplayed
            ) {
                if (element.isDisplayed(1)) {
                    result = "pass";
                    isOneDisplayed = true;
                    break;
                }
            }
        }
        Driver.getReport().log(result, "clickUntilOneOfThoseElementsDisplayed", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, null);
    }

    /**
     * le click() de base scroll dans la page sur l'élément avant de cliquer. Cette méthode non : donc ça peut failer si l'élément est hors cadre mais ça évite de refermer les listes déroulantes quand on clique un de leurs éléments.
     * Le résultat est tracé dans le rapport.
     * Si le click fail, click sur l'élément indique en parametre avant de retenter un click sur l'élément.
     * Le résultat est tracé dans le rapport.
     * @param elementToReclickBeforeIfFail : element sur lequel cliquer avant de retenter un click en cas d'echec
     */
    public void clickNoScrollBefore(Element elementToReclickBeforeIfFail) {
        System.out.println("click " + this.getName() + " avec click before " + elementToReclickBeforeIfFail.getName());
        startSearch("clickNoScrollBeforeelementToReclickBeforeIfFail");
        while (!isEnabled(2) && !stopSearch(60,"clickNoScrollBeforeelementToReclickBeforeIfFail") && elementToReclickBeforeIfFail.isEnabled(0)) {
            elementToReclickBeforeIfFail.clickIfPossible();
        }
        clickNoScrollBefore();
    }

    /**
     * le click() de base scroll dans la page sur l'élément avant de cliquer. Cette méthode non : donc ça peut failer si l'élément est hors cadre mais ça évite de refermer les listes déroulantes quand on clique un de leurs éléments.
     * Le résultat est tracé dans le rapport.
     */
    public void clickNoScrollBefore() {
        clickNoScrollBefore(TestProperties.implicit_wait);
    }

    /**
     * le click(int timeout) de base scroll dans la page sur l'élément avant de cliquer. Cette méthode non : donc ça peut failer si l'élément est hors cadre mais ça évite de refermer les listes déroulantes quand on clique un de leurs éléments.
     * Le résultat est tracé dans le rapport.
     * @param timeout : timeout en seconde
     */
    public void clickNoScrollBefore(int timeout) {
        //WebDriver.getCurrentBrowserTabs().beforeSwitch();
        System.out.println("clickNoScrollBefore " + this.getName());
        String result = "error";
        String errorMessage = null;
        try {
            findElementNoScrollBefore(timeout).click();
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage += e.getMessage() + e.toString();
        }
        Driver.getReport().log(result, "clickNoScrollBefore", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * click si c'est possible, sinon passe son chemin sans erreur.
     * Le résultat est tracé dans le rapport.
     */
    public void clickIfPossible() {
        //WebDriver.getCurrentBrowserTabs().beforeSwitch();
        System.out.println("clickIfPossible " + this.getName());
        try {
            findElementDisplayedMaxWait(5).click();
        } catch (Exception e) {
            // on fait rien
        }
    }

    /**
     * click tout de suite maintenant sur l'élément sans attendre qu'il soit là ou pas;
     * Le résultat est tracé dans le rapport.
     */
    public void clickNow() {
        //WebDriver.getCurrentBrowserTabs().beforeSwitch();
        System.out.println("clickNow " + this.getName());
        String result = null;
        String errorMessage = null;
        try {
            findElementDisplayedMaxWait(0).click();
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
            result = "fail";
        }
        Driver.getReport().log(result, "clickNow", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * click tout de suite maintenant sur l'élément sans attendre qu'il soit là ou pas;
     * Le résultat est tracé dans le rapport.
     */
    public void clickException() {
        findElementDisplayedMaxWait(0).click();
    }

    /**
     * coche une checkbox des qu'elle est enabled si elle n'est pas cochée dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void check(boolean cocher) {
        if (cocher)check(); else uncheck();
    }

    /**
     * coche une checkbox des qu'elle est enabled si elle n'est pas cochée dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void check() {
        System.out.println("check " + this.getName());
        String result = "fail";
        startSearch("check");
        while (!isChecked() && !stopSearch(30, "check")) {
            clickIfPossible();
        }
        if (isChecked()) {
            result = "pass";
        }
        Driver.getReport().log(result, "check", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, null);
    }

    /**
     * décoche une checkbox des qu'elle est enabled si elle est coché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void uncheck() {
        System.out.println("uncheck " + this.getName());
        String result = "fail";
        startSearch("uncheck");
        while (isChecked() && !stopSearch(30, "uncheck")) {
            clickIfPossible();
        }
        if (!isChecked()) {
            result = "pass";
        }
        Driver.getReport().log(result, "uncheck", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, null);
    }

    /**
     * coche les checkbox si elles ne sont pas cochées dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void checkAll() {
        System.out.println("checkAll " + this.getName());
        String result = "error";
        String errorMessage = null;
        try {
            for (WebElement element:findAllElements()
            ) {
                if (element.getAttribute("checked") == null) {
                    element.click();
                }
            }
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "check", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * décoche les checkbox si elles sont cochées dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void uncheckAll() {
        System.out.println("uncheckAll " + this.getName());
        String result = "error";
        String errorMessage = null;
        try {
            for (WebElement element:findAllElements()
            ) {
                if (element.getAttribute("checked").equals("true")) {
                    element.click();
                }
            }
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "uncheck", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }


    /**
     * réalise un click sur des coordonnées dans un élément trouve dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     * @param x : abscisse du point à cliquer à partir du centre de l'élément
     * @param y : ordonnée du point à cliquer à partir du centre de l'élément
     */
    public void clickOnPoint(int x, int y) {
        System.out.println("clickOnPoint");
        String result = "error";
        String errorMessage = null;
        try {
            int[] dimCarte = this.getDimension();
            int centreX = 0;
            int centreY = 0;
            Actions action = new Actions(Driver.getCurrentDriver());
            //action.moveToElement(this.findElement(), centreX, centreY).build().perform();
            //action.moveByOffset(x, y).click().build().perform();
            action.moveToElement(this.findElement(), x, y).click().build().perform();
            while (!Driver.JSExecutor().executeScript("return document.readyState").toString().equals("complete")) {
            }
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "clickOnPoint " + x + "," + y , this.getName() + " (" + this.getLocator().toString() + ")",  null, null, errorMessage);
    }


    /**
     * drag et drop de +x, +y un élément trouvé dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     * @param x : déplacement de x horizontalement
     * @param y : déplacement de y verticalement
     */
    public void dragDrop(int x , int y) {
        System.out.println("dragDrop");
        String result = "error";
        String errorMessage = null;
        try {
            int centreX = 0;
            int centreY = 0;
            Actions action = new Actions(Driver.getCurrentDriver());
            action.dragAndDropBy(this.findElement(),x, y).build().perform();
            while (!Driver.JSExecutor().executeScript("return document.readyState").toString().equals("complete")) {
            }
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "dragDrop " + x + "," + y, this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }


    /////////////// ASSERTION
    /**
     * vérifie qu'un élément est coché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void assertChecked() {
        assertChecked(false);
    }
    /**
     * vérifie qu'un élément est coché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertChecked(boolean justWarning) {
        System.out.println("assertChecked " + this.getName());
        String result = (justWarning?"warning":"errornext");
        String errorMessage = null;
        try {
            if (isChecked()) {
                result = "pass";
            } else {
                result = (justWarning?"warning":"failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertChecked", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * vérifie qu'un élément est non coché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void assertUnchecked() {
        assertUnchecked(false);
    }
    /**
     * vérifie qu'un élément est non coché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertUnchecked(boolean justWarning) {
        System.out.println("assertUnchecked " + this.getName());
        String result = (justWarning?"warning":"errornext");
        String errorMessage = null;
        try {
            if (!isChecked()) {
                result = "pass";
            } else {
                result = (justWarning?"warning":"failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertUnchecked", this.getName() + " (" + this.getLocator().toString() + ")",  null ,null, errorMessage);
    }

    /**
     * vérifie que l'élément est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertEnabled() {
        assertEnabled(false);
    }

    /**
     * vérifie que l'élément est enabled dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertEnabled(boolean justWarning) {
        System.out.println("assertEnabled " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (this.findElement().isEnabled()) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertEnabled", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que l'élément est disabled dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertDisabled() {
        assertDisabled(false);
    }

    /**
     * vérifie que l'élément est disabled dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertDisabled(boolean justWarning) {
        System.out.println("assertDisabled " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (this.findElement().isEnabled()) {
                result = (justWarning ? "warning" : "failnext");
            } else {
                result = "pass";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertDisabled", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que l'élément est non visible dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertHidden() {
        assertHidden(false);
    }

    /**
     * vérifie que l'élément est non visible dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertHidden(boolean justWarning) {
        System.out.println("assertHidden " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (this.findElement().isDisplayed()) {
                result = (justWarning ? "warning" : "failnext");
            } else {
                result = "pass";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertHidden", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que l'élément est visible dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertVisible() {
        assertVisible(false);
    }

    /**
     * vérifie que l'élément est visible dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertVisible(boolean justWarning) {
        System.out.println("assertVisible " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (!this.findElement().isDisplayed()) {
                result = (justWarning ? "warning" : "failnext");
            } else {
                result = "pass";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertVisible", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que l'élément est obligatoire dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertRequired() {
        assertRequired(false);
    }

    /**
     * vérifie que l'élément est obligatoire dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertRequired(boolean justWarning) {
        System.out.println("assertRequired " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (this.getAttribute("required") != null && this.getAttribute("required").equals("true")) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertRequired", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que l'élément est facultatif dans un délai de : test_env.properties#implicit_wait secondes;
     */
    public void assertNotRequired() {
        assertNotRequired(false);
    }

    /**
     * vérifie que l'élément est facultatif dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertNotRequired(boolean justWarning) {
        System.out.println("assertNotRequired " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (this.getAttribute("required") == null || this.getAttribute("required").equals("null") || this.getAttribute("required").equals("false")) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertNotRequired", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie que la valeur d'un élément vaut 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * pour les vérifications massives via classe Formulaire et fichier json si la valeur vaut "element hidden" on appelle assertHidden, si ça vaut "element does not exist" on appelle assertNotExist
     * Le résultat est tracé dans le rapport.
     *
     * @param value : valeur attendu de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void assertValue(String value) {
        assertValue(value, false);
    }

    /**
     * vérifie que la valeur d'un élément vaut 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * pour les vérifications massives via classe Formulaire et fichier json si la valeur vaut "element hidden" on appelle assertHidden, si ça vaut "element does not exist" on appelle assertNotExist
     * Le résultat est tracé dans le rapport.
     *
     * @param value       : valeur attendu de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertValue(String value, boolean justWarning) {
        switch (value) {
            case "element hidden":
                assertHidden(justWarning);
                break;
            case "element does not exist":
                assertNotExists(0, justWarning);
                break;
            default:

                System.out.println("assertValue " + this.getName() + " >> " + value);
                String result = (justWarning ? "warning" : "errornext");
                String errorMessage = null;
                String elementValue = "not found";
                value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
                int timeout = TestProperties.implicit_wait;
                try {
                    for (int i=0;i<10;i++) {
                        elementValue = getValue(timeout);
                        if (elementValue.replaceAll("\n", System.getProperty("line.separator")).equals(value.trim())) {
                            result = "pass";
                            i=11;
                            System.out.println(elementValue + " " + value);
                        } else {
                            result = (justWarning ? "warning" : "failnext");
                            Thread.sleep(100);
                        }
                        timeout = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }
                Driver.getReport().log(result, "assertValue", this.getName() + " (" + this.getLocator().toString() + ")", value, elementValue, errorMessage);
        }
    }

    /**
     * vérifie que la valeur d'un élément contient 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value : valeur attendu de contenu dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void assertValueContains(String value) {
        assertValueContains(value, false);
    }

    /**
     * vérifie que la valeur d'un élément contient 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value       : valeur attendu de contenu dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertValueContains(String value, boolean justWarning) {
        System.out.println("assertValueContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.implicit_wait;
            for (int i=0;i<10;i++) {
                elementValue = getValue(timeout);
                if (elementValue.replaceAll("\n", System.getProperty("line.separator")).contains(value.trim())) {
                    result = "pass";
                    i=11;
                    System.out.println(elementValue + " " + value);
                } else {
                    result = (justWarning ? "warning" : "failnext");
                    Thread.sleep(100);
                }
                timeout = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertValueContains", this.getName() + " (" + this.getLocator().toString() + ")", value, elementValue, errorMessage);
    }

    /**
     * vérifie que la valeur d'un élément ne contient pas 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value : valeur non attendu de contenu dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void assertValueNotContains(String value) {
        assertValueNotContains(value, false);
    }

    /**
     * vérifie que la valeur d'un élément ne contient pas 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value       : valeur non attendu de contenu dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertValueNotContains(String value, boolean justWarning) {

        System.out.println("assertValueNotContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.implicit_wait;
            for (int i=0;i<10;i++) {
                elementValue = getValue(timeout);
                if (!elementValue.replaceAll("\n", System.getProperty("line.separator")).contains(value.trim())) {
                    result = "pass";
                    i=11;
                    System.out.println(elementValue + " " + value);
                } else {
                    result = (justWarning ? "warning" : "failnext");
                    Thread.sleep(100);
                }
                timeout = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertValueNotContains", this.getName() + " (" + this.getLocator().toString() + ")", value, elementValue, errorMessage);
    }

    /**
     * vérifie que la valeur d'un élément commence par 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value : valeur attendu de début dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void assertValueStartWith(String value) {
        assertValueStartWith(value, false);
    }

    /**
     * vérifie que la valeur d'un élément commence par 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value       : valeur attendu de début dans la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertValueStartWith(String value, boolean justWarning) {
        System.out.println("assertValueContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.implicit_wait;
            for (int i=0;i<10;i++) {
                elementValue = getValue(timeout);
                if (elementValue.replaceAll("\n", System.getProperty("line.separator")).startsWith(value.trim())) {
                    result = "pass";
                    i=11;
                    System.out.println(elementValue + " " + value);
                } else {
                    result = (justWarning ? "warning" : "failnext");
                    Thread.sleep(100);
                }
                timeout = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertValueContains", this.getName() + " (" + this.getLocator().toString() + ")", value, elementValue, errorMessage);
    }

    /**
     * vérifie que la valeur d'un élément est contenu dans 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value : valeur attendu contenant la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     */
    public void assertValueIncludedIn(String value) {
        assertValueIncludedIn(value, false);
    }

    /**
     * vérifie que la valeur d'un élément est contenu dans 'value' dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param value       : valeur attendu contenant la valeur de l'élément (clé de test_env.properties ou savedData.properties ou valeur en dur, la valeur est traduite si correspond à un clé de label_langue.properties)
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertValueIncludedIn(String value, boolean justWarning) {

        System.out.println("assertValueIncludedIn " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.implicit_wait;
            for (int i=0;i<10;i++) {
                elementValue = getValue(timeout);
                if (value.contains(elementValue.replaceAll("\n", System.getProperty("line.separator")))) {
                    result = "pass";
                    i=11;
                    System.out.println(elementValue + " " + value);
                } else {
                    result = (justWarning ? "warning" : "failnext");
                    Thread.sleep(100);
                }
                timeout = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertValueIncludedIn", this.getName() + " (" + this.getLocator().toString() + ")", value, elementValue, errorMessage);
    }

    /**
     * vérifie qu'un élément est affiché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void assertExists() {
        assertExists(false);
    }

    /**
     * vérifie qu'un élément est affiché dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertExists(boolean justWarning) {
        System.out.println("assertExists " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (findElementDisplayedMaxWait(TestProperties.implicit_wait) != null) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertExists", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie qu'un élément est affiché dans un délai de : timeout secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param timeout : timeout en seconde
     */
    public void assertExists(int timeout) {
        assertExists(timeout, false);
    }

    /**
     * vérifie qu'un élément est affiché dans un délai de : timeout secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param timeout     : timeout en seconde
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertExists(int timeout, boolean justWarning) {
        System.out.println("assertExists " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (findElementDisplayedMaxWait(timeout) != null) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertExists", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie qu'un élément est n'est affiché dans un délai de : 2 secondes;
     * Le résultat est tracé dans le rapport.
     */
    public void assertNotExists() {
        assertNotExists(2, false);
    }

    /**
     * vérifie qu'un élément est n'est affiché dans un délai de : 2 secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertNotExists(boolean justWarning) {
        assertNotExists(2, justWarning);
    }

    /**
     * vérifie qu'un élément n'est affiché pas dans un délai de : timeout secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param timeout : timeout en seconde
     */
    public void assertNotExists(int timeout) {
        assertNotExists(timeout, false);
    }

    /**
     * vérifie qu'un élément n'est affiché pas dans un délai de : timeout secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param timeout     : timeout en seconde
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertNotExists(int timeout, boolean justWarning) {
        System.out.println("assertNotExists " + this.getName());
        String result = "pass";
        String errorMessage = null;
        startSearch("assertNotExists");
        try {
            WebElement element = findElementDisplayedMaxWait(0);
            while (element != null && !stopSearch(timeout, "assertNotExists")) {
                element = findElementDisplayedMaxWait(0);
            }
            if (element != null) {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertNotExists", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * vérifie la valeur de l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute : nom de l'attribut
     * @param value     : valeur de l'attribut attendu
     */
    public void assertAttribute(String attribute, String value) {
        assertAttribute(attribute, value, false);
    }

    /**
     * vérifie la valeur de l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute   : nom de l'attribut
     * @param value       : valeur de l'attribut attendu
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertAttribute(String attribute, String value, boolean justWarning) {
        System.out.println("assertAttribute " + this.getName() + " >> " + attribute + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String actualValue = "";
        try {
            actualValue = String.valueOf(getAttribute(attribute));
            if (actualValue.equals(String.valueOf(value))) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertAttribute " + attribute, this.getName() + " (" + this.getLocator().toString() + ")", value, actualValue, errorMessage);
    }

    /**
     * vérifie une partie de la valeur de l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute : nom de l'attribut
     * @param value     : partie de valeur de l'attribut attendu
     */
    public void assertAttributeContains(String attribute, String value) {
        assertAttributeContains(attribute, value, false);
    }

    /**
     * vérifie une partie de la valeur de l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute   : nom de l'attribut
     * @param value       : partie de valeur de l'attribut attendu
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertAttributeContains(String attribute, String value, boolean justWarning) {
        System.out.println("assertAttributeContains " + this.getName() + " >> " + attribute + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String actualValue = "";
        try {
            actualValue = String.valueOf(getAttribute(attribute));
            if (actualValue.contains(String.valueOf(value))) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertAttributeContains " + attribute, this.getName() + " (" + this.getLocator().toString() + ")", value, actualValue, errorMessage);
    }

    /**
     * vérifie qu'une chaine n'est pas dans l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute : nom de l'attribut
     * @param value     : chaine non attendu dans l'attribut
     */
    public void assertAttributeNotContains(String attribute, String value) {
        assertAttributeNotContains(attribute, value, false);
    }

    /**
     * vérifie qu'une chaine n'est pas dans l'attribut d'un élément dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param attribute   : nom de l'attribut
     * @param value       : chaine non attendu dans l'attribut
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertAttributeNotContains(String attribute, String value, boolean justWarning) {
        System.out.println("assertAttributeNotContains " + this.getName() + " >> " + attribute + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String actualValue = "";
        try {
            actualValue = String.valueOf(getAttribute(attribute));
            if (!actualValue.contains(String.valueOf(value))) {
                result = "pass";
            } else {
                result = (justWarning ? "warning" : "failnext");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "assertAttributeNotContains " + attribute, this.getName() + " (" + this.getLocator().toString() + ")", value, actualValue, errorMessage);
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * copie dans le clipboard le contenu d'un élément trouve dans un délai de : test_env.properties#implicit_wait secondes;
     *
     * @return le contenu si trouvé, "unable to read clipboard string ", "error while copy to clipboard"
     */
    public String copyToClipBoard() {
        System.out.println("copyToClipBoard");
        String result;
        try {
            Actions action = new Actions(Driver.getCurrentDriver());
            //action.moveToElement(this.findElement()).build().perform();
            //action.moveByOffset(0, 0).click().sendKeys(Keys.chord(Keys.CONTROL, "a")).sendKeys(Keys.chord(Keys.CONTROL, "c")).build().perform();
            action.moveToElement(this.findElement(), 0, 0).click().click().click().click().build().perform();
            action.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).build().perform();
            while (!Driver.JSExecutor().executeScript("return document.readyState").toString().equals("complete")) {
            }
            try {
                result = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                result = "unable to read clipboard string " + e.getMessage();
            }
        } catch (Exception e) {
            result = "error while copy to clipboard";
        }
        return result;
    }

    /**
     * Se positionne dans un élément de type frame, iframe, object pour pouvoir agir sur les éléments contenus dans cet objet
     * Le résultat est tracé dans le rapport.
     */
    public void switchFrame() {
        System.out.println("switchFrame");
        //WebDriver.getCurrentBrowserTabs().switchDefault();
        String result = "error";
        String errorMessage = null;
        try {
            Driver.getCurrentDriver().switchTo().frame(findElement());
            result = "pass";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "switchFrame", this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
    }

    /**
     * upload un ou des fichiers dans un input type "file" dans un délai de : test_env.properties#implicit_wait secondes;
     * Le résultat est tracé dans le rapport.
     *
     * @param values : fichiers à charger séparés par des ;
     */
    public void uploadFile(String values) {
        if (values != null) {
            String action = "uploadFile >> " + values + " ";
            System.out.println(action + this.getName() + " (" + this.getLocator().toString() + ")");
            String result = "error";
            String errorMessage = null;
            try {
                String reference;
                String dowloadloadedfiles = "";
                for (String value : values.split(";")
                ) {
                    reference = "Test Files/"  + value;
                    dowloadloadedfiles += (dowloadloadedfiles.equals("") ? "" : "\n ") + Paths.get("").toAbsolutePath().toString() + File.separator + reference.replace("\\", File.separator);
                }
                findElement().sendKeys(dowloadloadedfiles);
                result = "pass";
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
            Driver.getReport().log(result, action, this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
        }
    }

}
