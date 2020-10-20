package element;
import driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;

public class Loader {
    private static By loader1 = By.id("loader");

    public static void waitUntilJSReady() {
        LocalDateTime dateStartSearch = LocalDateTime.now();
        // js complete
        try {
            boolean jsReady = Driver.JSExecutor().executeScript("return document.readyState").toString().equals("complete");
            while (!jsReady && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
                jsReady = Driver.JSExecutor().executeScript("return document.readyState").toString().equals("complete");
            }
        }catch (Exception ignore) {}

        //jquery complete
        try {
            boolean jQueryDefined = !Driver.JSExecutor().executeScript("return typeof jQuery").toString().equals("undefined");
            if (jQueryDefined) {
                boolean jQueryLoad = Driver.JSExecutor().executeScript("return jQuery.active").toString().equals("0");
                while (!jQueryLoad && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
                    jQueryLoad = Driver.JSExecutor().executeScript("return jQuery.active").toString().equals("0");
                }
            }
        }catch (Exception ignore) {}

        //PF complete
        try {
            boolean jsPrimefacesDefined = !Driver.JSExecutor().executeScript("return typeof PrimeFaces").toString().equals("undefined");
            if (jsPrimefacesDefined) {
                boolean jsPFLoad = Driver.JSExecutor().executeScript("return PrimeFaces.ajax.Queue.isEmpty();").toString().equals("true");
                while (!jsPFLoad && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
                    jsPFLoad = Driver.JSExecutor().executeScript("return PrimeFaces.ajax.Queue.isEmpty();").toString().equals("true");
                }
            }
        }catch (Exception ignore) {}

        //Angular complete
        try {
            Object angular8Check = Driver.JSExecutor().executeScript("return getAllAngularRootElements()[0].attributes['ng-version']");
            if (angular8Check != null) {
                String angular8Ready = "return (window.getAllAngularTestabilities().findIndex(x=>!(x[\"_isZoneStable\"])) && \n" +
                        "window.getAllAngularTestabilities().findIndex(x=>!(x[\"_pendingCount\"]===0)) &&\n" +
                        "window.getAllAngularTestabilities().findIndex(x=>!(x[\"_ngZone\"][\"isStable\"])));";
                while (
                        Boolean.valueOf(Driver.JSExecutor().executeScript(angular8Ready).toString()) &&
                                dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
                }
            }
        }catch (Exception ignore) {}
    }

    /**
     * Attend que les loaders ne soient plus displayed :2 types de loader sont identifes dans cette classe, correspondant aux xpath //span[contains(@class,'spinner-loader')] et //div[@id='uiGridLoader']
     */
    public static void waitNotVisible() {
        waitUntilJSReady();
        try {
            boolean continuWaiting = true;
            LocalDateTime dateStartSearch = LocalDateTime.now();
            WebElement loader = (new WebDriverWait(Driver.getCurrentDriver(), 0)).until(ExpectedConditions.elementToBeClickable(loader1));
            while (continuWaiting && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
                continuWaiting = false;
                try {
                    loader.click();
                    continuWaiting = true;
                } catch (Exception e) {
                    // element non clickable donc non visible
                }
            }
        }catch (Exception e) {
        }
    }


    /**
     * Attend que les loaders ne soient plus displayed :2 types de loader sont identifes dans cette classe, correspondant aux xpath //span[contains(@class,'spinner-loader')] et //div[@id='uiGridLoader']
     * et que tous les éléments en paramètres soient chargés
     * @param elements liste des éléments dont on attend le chargement après disparition des loaders
     * @return true si tout est chargé
     */
    public static boolean waitNotVisibleAndAllElementsLoaded(Element[] elements) {
        boolean loaded = true;
        waitNotVisible();
        for (Element element: elements
             ) {
            if (!element.loaded()) {
                loaded = false;
                break;
            }
        }
        waitNotVisible();
        return loaded;
    }

    /**
     * Attend que les loaders ne soient plus displayed :2 types de loader sont identifes dans cette classe, correspondant aux xpath //span[contains(@class,'spinner-loader')] et //div[@id='uiGridLoader']
     * et que au moins 1 élément en paramètres soient chargés
     * @param elements liste des éléments dont on attend qu'au moins 1 s'affiche après disparition des loaders
     * @return true si 1 élément est chargé
     */
    public static boolean waitNotVisibleAndAtLeastOneElementLoaded(Element[] elements) {
        waitNotVisible();
        return waitAtLeastOneElementLoaded(elements)!=null;
    }

    /**
     * Attend que au moins 1 élément en paramètres soient chargés
     * @param elements liste des éléments dont on attend qu'au moins 1 s'affiche après disparition des loaders
     * @return renvoi le premier élément chargé
     */
    public static Element waitAtLeastOneElementLoaded(Element[] elements) {
        Element loadElement = null;
        LocalDateTime dateStartSearch = LocalDateTime.now();
        while (loadElement==null && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
            waitNotVisible();
            for (Element element : elements
            ) {
                if (element.loaded(0)) {
                    loadElement = element;
                    break;
                }
            }
        }
        return loadElement;
    }

    /**
     * Attend que au moins 1 élément en paramètres soient chargés
     * @param elements liste des éléments dont on attend qu'au moins 1 s'affiche après disparition des loaders
     * @return renvoi le premier élément chargé
     */
    public static Element waitAtLeastOneElementLoadedAndDisplayed(Element[] elements) {
        Element loadElement = null;
        LocalDateTime dateStartSearch = LocalDateTime.now();
        while (loadElement==null && dateStartSearch.plusMinutes(1).isAfter(LocalDateTime.now())) {
            waitNotVisible();
            for (Element element : elements
            ) {
                if (element.loaded(0)) {
                }
                if (element.isDisplayed(0)) {
                    loadElement = element;
                    break;
                }
            }
        }
        return loadElement;
    }
}
