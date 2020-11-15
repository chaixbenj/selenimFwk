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

/**
 * class Element extends that class. Class element allows to make assertion and actions, this class manage the way we find element in the DOM.
 */
class BaseElement {
    private String name;
    private String notValuedName;
    private By locator;
    private By notValuedLocator;
    private BaseElement container;

    private static LocalDateTime startT;

    /**
     * constructor
     * @param elementName name of the element as it wil appear in the report
     * @param elementLocator selenium locator in order to find the element in the DOM
     */
    public BaseElement(String elementName, By elementLocator) {
        name = elementName;
        notValuedName = elementName;
        locator = elementLocator;
        notValuedLocator = elementLocator;
        container = null;
    }

    /**
     * set a name to the element
     * @param elementName
     */
    public void setName(String elementName) {
        name = elementName;
        notValuedName = elementName;
    }

    /**
     * set a locator to the element
     * @param elementLocator
     */
    public void setLocator(By elementLocator) {
        locator = elementLocator;
        notValuedLocator = elementLocator;
    }

    /**
     * get the name of the element
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * get the locator of the element
     * @return
     */
    public By getLocator() {
        return locator;
    }

    /**
     * element can be in an other element. In order to limitate the search of the element in one ancestor we can set a container that is another element
     * @param containerElement
     * @return
     */
    public BaseElement setBaseContainer(BaseElement containerElement) {
        container = containerElement;
        String locatorPath = locator.toString();
        if (container != null && locatorPath.contains("By.xpath: ")) {
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
     * reset the container : element will be search in all the body
     * @return
     */
    public BaseElement resetBaseContainer() {
        container = null;
        String locatorPath = locator.toString();
        if (locatorPath.contains("By.xpath: ")) {
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
     * get the container of the element
     * @return
     */
    public BaseElement getBaseContainer() {
        return container;
    }

    /**
     * replace some strings in locator by other values. For example :
     * Element element = new Element("element, By.xpath("//button[@class='{myClasse}'][contains(.,'{aValue}')]);
     * element.setParamater(new String[] {"{myClasse}","success_button", "{aValue}", "Enregister"});
     * @param params
     * @return
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
     * replace one string in the locator by other values. For example :
     * Element element = new Element("element, By.xpath("//button[@class='{myClasse}']);
     * element.setParamater({"{myClasse}","success_button");
     * @param key
     * @param value
     * @return
     */
    public BaseElement setBaseParameter(String key, String value) {
        name = name.replace(key, (value));
        setLocatorFromString(notValuedLocator.toString().replace(key, (value)));
        return this;
    }

    /**
     * set locator with a string after converting string locator in By locator
     * @param path
     */
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
     * convert locator in By.xpath locator
     * @return
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


    /**
     * find an element (display or hidden). Timeout defined in the test_xx.properties
     * @return
     */
    public WebElement findElement() {
        return findElement(TestProperties.timeout);
    }

    /**
     * find an element (display or hidden). Timeout defined in argument
     * @param timeout
     * @return
     */
    public WebElement findElement(int timeout) {
        WebElement mon_element = findElementNoScrollBefore(timeout);
        if (mon_element != null) scrollElement(mon_element);
        return mon_element;
    }

    /**
     * find an element (display or hidden) but no scroll. Timeout defined in the test_xx.properties
     * @return
     */
    public WebElement findElementNoScrollBefore() {
        return findElementNoScrollBefore(TestProperties.timeout);
    }

    /**
     * find an element (display or hidden) but no scroll. Timeout defined in argument
     * @param timeout
     * @return
     */
    public WebElement findElementNoScrollBefore(int timeout) {
        Loader.waitNotVisible();
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
            return mon_element;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * find all elements corresponding to the element locator. Timeout defined in the test_xx.properties
     * @return
     */
    public List<WebElement> findAllElements() {
        return findAllElements(TestProperties.timeout);
    }

    /**
     * find all elements corresponding to the element locator. Timeout defined in argument
     * @return
     */
    public List<WebElement> findAllElements(int timeout) {
        Loader.waitNotVisible();
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
            return mon_element;
        } catch (Exception e) {
            return mon_element;
        }
    }

    /**
     * find an element display and enabled. Timeout defined in the test_xx.properties
     * @return
     */
    public WebElement findElementEnabled() {
        Loader.waitNotVisible();
        By locator = this.getLocator();
        BaseElement container = this.getBaseContainer();
        WebElement mon_element = null;
        try {
            if (container == null) {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.timeout)).until(
                        ExpectedConditions.elementToBeClickable(locator)
                );
            } else {
                mon_element = (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.timeout)).until(
                        ExpectedConditions.elementToBeClickable(
                                (new WebDriverWait(Driver.getCurrentDriver(), TestProperties.timeout)).until(
                                        ExpectedConditions.presenceOfNestedElementLocatedBy(container.getLocator(), locator)
                                ))
                );
            }
            if (mon_element != null) scrollElement(mon_element);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("=============> " + searchTime() + " ms");
        return mon_element;
    }

    /**
     * find an element display. Timeout defined in the test_xx.properties
     * @return
     */
    public WebElement findElementDisplayed() {
        return findElementDisplayed(TestProperties.timeout);
    }

    /**
     * find an element display. Timeout defined in argument
     * @return
     */
    public WebElement findElementDisplayed(int timeout) {
        WebElement mon_element = findElementDisplayedNoScrollBefore(timeout);
        if (mon_element!=null) scrollElement(mon_element);
        return mon_element;
    }

    /**
     * find an element display but no scroll. Timeout defined in argument
     * @return
     */
    public WebElement findElementDisplayedNoScrollBefore(int timeout) {
        Loader.waitNotVisible();
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
            return mon_element;
        } catch (Exception e) {
            return null;
        }
    }


    // PRIVATE -------------------------------------------------------------------------------------------------

    /**
     * if element is not the 2nd or 3rd quarter of the page ==> scroll to the element
     * you can tune that method if you have header or footer i you page that can hide the element. If you don't you can comment this method
     * @param element
     */
    private void scrollElement(WebElement element) {
        try {
            if (element != null) {
                int y = element.getLocation().getY();
                long scroll = (long) Driver.JSExecutor().executeScript("return window.pageYOffset;");
                int screenHeight = Driver.getCurrentDriver().manage().window().getSize().height;
                if (Math.abs(y - scroll) < (screenHeight/4) || Math.abs(y - scroll) > (screenHeight - screenHeight/4)) {
                    Driver.JSExecutor().executeScript("window.scrollBy(0 , " + (y - scroll - screenHeight/4) + ");");
                }
            }
        } catch (Exception ignore) {
        }
    }

}
