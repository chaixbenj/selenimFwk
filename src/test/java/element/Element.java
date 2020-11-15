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
     * constructor
     * @param elementName element description, for report
     * @param elementLocator element locator
     */
    public Element(String elementName, By elementLocator) {
        super(elementName, elementLocator);
    }

    /**
     * set container of the element, container is an element containing the element
     * @param containerElement
     * @return
     */
    public Element setContainer(Element containerElement) {
        return (Element) setBaseContainer((BaseElement) containerElement);
    }

    /**
     * reset the container of the element
     * @return
     */
    public Element resetContainer() {
        return (Element) resetBaseContainer();
    }

    /**
     * get the container of the element
     * @return
     */
    public Element getContainer() {
        return (Element) getBaseContainer();
    }

    /**
     * replace some strings in locator by other values. For example :
     * Element element = new Element("element, By.xpath("//button[@class='{myClasse}'][contains(.,'{aValue}')]);
     * element.setParamater(new String[] {"{myClasse}","success_button", "{aValue}", "Enregister"});
     * @param params
     * @return
     */
    public Element setParameter(String[] params) {
        return (Element) setBaseParameter(params);
    }

    /**
     * replace one string in the locator by other values. For example :
     * Element element = new Element("element, By.xpath("//button[@class='{myClasse}']);
     * element.setParamater({"{myClasse}","success_button");
     * @param key
     * @param value
     * @return
     */
    public Element setParameter(String key, String value) {
        return (Element) setBaseParameter(key, value);
    }

    /**
     * return parent element
     *
     * @return parent elemnt
     */
    public WebElement getFather() {
        return findElement().findElement(By.xpath(".."));
    }

    /**
     * return Nth ancestor of the element
     *
     * @param nbFather : Nth ancestor degree
     * @return Nth ancestor element
     */
    public WebElement getFather(int nbFather) {
        WebElement element = findElement();
        for (int i = 0; i < nbFather; i++) {
            element = element.findElement(By.xpath(".."));
        }
        return element;
    }

    /**
     * return ancestor attribute
     *
     * @param nbFather : Nth ancestor degree
     * @param attr     : attribute name
     * @return attribute value
     */
    public String getFatherAttribute(int nbFather, String attr) {
        return getFather(nbFather).getAttribute(attr);
    }


    /**
     * return number of elements responding to the element locator
     *
     * @param timeout
     * @return number of elements
     */
    public int getElementsNumber(int timeout) {
        try {
            return findAllElements(timeout).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * return if the element exists in the DOM
     *
     * @param timeout
     * @return exists?= true
     */
    public boolean exists(int timeout) {
        if (findElementNoScrollBefore((int) timeout) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * return if the element is enabled
     *
     * @param timeout
     * @return enabled?=true
     */
    public boolean isEnabled(int timeout) {
        try {
            return findElementDisplayedNoScrollBefore(timeout).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * return is te element is displayed in the page
     *
     * @param timeout
     * @return displayed ?=true
     */
    public boolean isDisplayed(int timeout) {
        try {
            return findElementDisplayedNoScrollBefore(timeout).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * return if the element is checked
     * @return checked ?=true
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
     * return if the element is loaded
     *
     * @param timeout
     * @return loaded?=true
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
     * mouseover the element
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
     * return element value. TestProperties.timeout
     *
     * @return value or "not found".
     */
    public String getValue() {
        return getValue(TestProperties.timeout);
    }

    /**
     * return element value
     *
     * @param timeout
     * @return value or "not found"
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
                    }
                }
            }
        } catch (Exception e) {
            elementValue = "not found";
        }
        return String.valueOf(elementValue);
    }

    /**
     * return element attribute
     *
     * @param attribute name
     * @return attribute value
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
     * return position x,y of the element
     *
     * @return [X, Y] element position
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
     * return element dimension
     *
     * @return [X, Y] element dimension
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
     * set element value
     * @param value
     */
    public void setValue(String value) {
        if (value != null) {
            String action = "setValue >> " + value + " ";
            String result = "error";
            startSearch("setValue");
            WebElement element = findElementEnabled();
            String errorMessage = null;
            while (!stopSearch(TestProperties.timeout, "setValue") && !result.equals("pass")) {
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
            }
            Driver.getReport().log(result, action, this.getName() + " (" + this.getLocator().toString() + ")", null, null, errorMessage);
        }
    }

    /**
     * click element until on page is loaded
     * @param pageObjectToBeLoadedAfterClick class pageObjects to be loaded after click
     * @param assertLoadedMethod method of class pageObjectToBeLoadedAfterClick that must return "true" when page is loaded
     */
    public void click(Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        startSearch("clickpageObjectToBeLoadedAfterClick");
        click();
        while(exists(1) && !pageloaded(pageObjectToBeLoadedAfterClick, assertLoadedMethod) && !stopSearch(300,"clickpageObjectToBeLoadedAfterClick")) {click();};
    }

    /**
     * click element until on page is loaded, when fail click on a previous element before retruing the click on element
     * @param elementToReclickBeforeIfFail : element to be clicked before re-click the element if fail
     * @param pageObjectToBeLoadedAfterClick class pageObjects to be loaded after click
     * @param assertLoadedMethod method of class pageObjectToBeLoadedAfterClick that must return "true" when page is loaded
     */
    public void click(Element elementToReclickBeforeIfFail, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        startSearch("clickpageObjectToBeLoadedAfterClickAndClickBefore");
        click(elementToReclickBeforeIfFail);
        while(exists(1) && !pageloaded(pageObjectToBeLoadedAfterClick, assertLoadedMethod) && !stopSearch(300,"clickpageObjectToBeLoadedAfterClickAndClickBefore")) {
            click(elementToReclickBeforeIfFail);
        }
    }


    /**
     * click on the element
     */
    public void click() {
        System.out.println("click " + this.getName());
        String result = "error";
        String errorMessage = null;
        startSearch("click");
        findElementEnabled();
        while (!stopSearch(TestProperties.timeout, "click") && !result.equals("pass")) {
            errorMessage = null;
            try {
                findElement(0).click();
                result = "pass";
            }
            catch (Exception e) {
                errorMessage += e.getMessage() + e.toString();
            }
        }
        Driver.getReport().log(result, "click", this.getName() + " (" + this.getLocator().toString() + ")",  null , null, errorMessage);
    }

    /**
     * click on the element, and click on a previous element before retying if click fail
     * @param elementToReclickBeforeIfFail : element to be clicked before re-click the element if fail
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
     * select on element in list element such as SELECT/OPTION or UL/LI
     * @param option : option à choisir
     */
    public void selectInList(String option) {
        if (option!=null) {
            String result = "error";
            String errorMessage = "";
            startSearch("selectInList");
            try {
                if (option != null) {
                    this.findElementNoScrollBefore();
                    while (!this.getValue().contains(option) && !stopSearch(TestProperties.timeout, "selectInList")) {
                        this.clickIfPossible();
                        Element optionElement = new Element("Option '" + option + "'", By.xpath("li[contains(.,\"" + option + "\")]|option[contains(.,\"" + option + "\")]")).setContainer(this);
                        optionElement.clickIfPossible();
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
     * click on child element number N
     * @param subElementNumber numero du sous élément
     */
    public void clickSubElementNo(String subElementNumber) {
        Element sousElement = new Element("sous element {N} de " + this.getName(), By.xpath(this.locatorXpath() + "/*[{N}]")).setParameter("{N}", subElementNumber);
        sousElement.click();
    }

    /**
     * click the element until one element in the arg list is displayed
     * @param elementToBeDisplayed : elements list to be displayed
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
     * click the element until one element in the arg list is displayed, and click on a previous element before retying if click fail
     * @param elementToReclickBeforeIfFail : element to be clicked before re-click the element if fail
     * @param elementToBeDisplayed : element to be clicked before if fail
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
     * click element without scrolling to it and click on a previous element before retying if click fail
     * @param elementToReclickBeforeIfFail : element to be clicked before re-click the element if fail
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
     * click element without scrolling to it
     */
    public void clickNoScrollBefore() {
        clickNoScrollBefore(TestProperties.timeout);
    }

    /**
     * click element without scrolling to it
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
     * click element if possible. No error if can't click
     */
    public void clickIfPossible() {
        //WebDriver.getCurrentBrowserTabs().beforeSwitch();
        System.out.println("clickIfPossible " + this.getName());
        try {
            findElementDisplayed(5).click();
        } catch (Exception e) {
            // on fait rien
        }
    }

    /**
     * click element, timeout 0, no error but send an execption
     */
    public void clickException() {
        findElementDisplayed(0).click();
    }

    /**
     * check or uncheck element
     * @param checkEl check or uncheck ?
     */
    public void check(boolean checkEl) {
        if (checkEl)check(); else uncheck();
    }

    /**
     * check element
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
     * uncheck element
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
     * check all checkbox correspondig to the locator
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
     * uncheck all checkbox correspondig to the locator
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
     * click at position x,y in the element
     * @param x
     * @param y
     */
    public void clickOnPoint(int x, int y) {
        System.out.println("clickOnPoint");
        String result = "error";
        String errorMessage = null;
        try {
            Actions action = new Actions(Driver.getCurrentDriver());
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
     * drag and drop element from its postion to postion + x,y
     * @param x
     * @param y
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
     * assert element checked
     */
    public void assertChecked() {
        assertChecked(false);
    }

    /**
     * assert element checked
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element unchecked
     */
    public void assertUnchecked() {
        assertUnchecked(false);
    }

    /**
     * assert element unchecked
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element enabled
     */
    public void assertEnabled() {
        assertEnabled(false);
    }

    /**
     * assert element enabled
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element disabled
     */
    public void assertDisabled() {
        assertDisabled(false);
    }

    /**
     * assert element disabled
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element hidden
     */
    public void assertHidden() {
        assertHidden(false);
    }

    /**
     * assert element hidden
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element displayed
     */
    public void assertDisplayed() {
        assertDisplayed(false);
    }

    /**
     * assert element displayed
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertDisplayed(boolean justWarning) {
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
     * assert element required
     */
    public void assertRequired() {
        assertRequired(false);
    }

    /**
     * assert element required
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element not required
     */
    public void assertNotRequired() {
        assertNotRequired(false);
    }

    /**
     * assert element not required
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element value
     *
     * @param value
     */
    public void assertValue(String value) {
        assertValue(value, false);
    }

    /**
     * assert element value
     *
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
                int timeout = TestProperties.timeout;
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
     * assert element value contains
     *
     * @param value
     */
    public void assertValueContains(String value) {
        assertValueContains(value, false);
    }

    /**
     * assert element value contains
     *
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertValueContains(String value, boolean justWarning) {
        System.out.println("assertValueContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.timeout;
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
     * assert element value not contains
     *
     * @param value
     */
    public void assertValueNotContains(String value) {
        assertValueNotContains(value, false);
    }

    /**
     * assert element value not contains
     *
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertValueNotContains(String value, boolean justWarning) {

        System.out.println("assertValueNotContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.timeout;
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
     * assert element value starts with
     *
     * @param value
     */
    public void assertValueStartWith(String value) {
        assertValueStartWith(value, false);
    }

    /**
     * assert element value starts with
     *
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertValueStartWith(String value, boolean justWarning) {
        System.out.println("assertValueContains " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.timeout;
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
     * assert element value included in
     *
     * @param value
     */
    public void assertValueIncludedIn(String value) {
        assertValueIncludedIn(value, false);
    }

    /**
     * assert element value included in
     *
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertValueIncludedIn(String value, boolean justWarning) {
        System.out.println("assertValueIncludedIn " + this.getName() + " >> " + value);
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        String elementValue = "not found";
        value = String.valueOf(value).replace("\\n", System.getProperty("line.separator"));
        try {
            int timeout = TestProperties.timeout;
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
     * assert element exists displayed or not
     */
    public void assertExists() {
        assertExists(false);
    }

    /**
     * assert element exists displayed or not
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertExists(boolean justWarning) {
        System.out.println("assertExists " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (findElementDisplayed(TestProperties.timeout) != null) {
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
     * assert element exists displayed or not
     *
     * @param timeout : timeout en seconde
     */
    public void assertExists(int timeout) {
        assertExists(timeout, false);
    }

    /**
     * assert element exists displayed or not
     *
     * @param timeout     : timeout en seconde
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertExists(int timeout, boolean justWarning) {
        System.out.println("assertExists " + this.getName());
        String result = (justWarning ? "warning" : "errornext");
        String errorMessage = null;
        try {
            if (findElementDisplayed(timeout) != null) {
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
     * assert element does not exist
     */
    public void assertNotExists() {
        assertNotExists(2, false);
    }

    /**
     *  assert element does not exist
     *
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertNotExists(boolean justWarning) {
        assertNotExists(2, justWarning);
    }

    /**
     * assert element does not exist
     *
     * @param timeout
     */
    public void assertNotExists(int timeout) {
        assertNotExists(timeout, false);
    }

    /**
     * assert element does not exist
     *
     * @param timeout
     * @param justWarning true if you wish assertion to send a warning instead of a failure
     */
    public void assertNotExists(int timeout, boolean justWarning) {
        System.out.println("assertNotExists " + this.getName());
        String result = "pass";
        String errorMessage = null;
        startSearch("assertNotExists");
        try {
            WebElement element = findElementDisplayed(0);
            while (element != null && !stopSearch(timeout, "assertNotExists")) {
                element = findElementDisplayed(0);
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
     * assert element attribute value
     *
     * @param attribute
     * @param value
     */
    public void assertAttribute(String attribute, String value) {
        assertAttribute(attribute, value, false);
    }

    /**
     * assert element attribute value
     *
     * @param attribute
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element attribute value contains
     *
     * @param attribute
     * @param value
     */
    public void assertAttributeContains(String attribute, String value) {
        assertAttributeContains(attribute, value, false);
    }

    /**
     * assert element attribute value contains
     *
     * @param attribute
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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
     * assert element attribute value does not contain
     *
     * @param attribute
     * @param value
     */
    public void assertAttributeNotContains(String attribute, String value) {
        assertAttributeNotContains(attribute, value, false);
    }

    /**
     * assert element attribute value does not contain
     *
     * @param attribute
     * @param value
     * @param justWarning true if you wish assertion to send a warning instead of a failure
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


    /**
     * copy element value in clipboard
     *
     * @return
     */
    public String copyToClipBoard() {
        System.out.println("copyToClipBoard");
        String result;
        try {
            Actions action = new Actions(Driver.getCurrentDriver());
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
     * swith in the element, element must be an iframe
     */
    public void switchFrame() {
        System.out.println("switchFrame");
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
     * upload files in element such as input type="file"
     *
     * @param values files to be upload (must be in resources/Test Files, separate files name with ";")
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


    // private
    private static void startSearch(String from) {
        if (dateStartSearch.containsKey(from)) {
            dateStartSearch.remove(from);
        }
        dateStartSearch.put(from, LocalDateTime.now());
    }

    private static boolean stopSearch(int timeout, String from) {
        if (dateStartSearch.get(from).plusSeconds(timeout).isAfter(LocalDateTime.now())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean pageloaded(Class pageObjectToBeLoaded, String assertLoadedMethod) {
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


}
