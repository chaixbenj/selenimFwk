package element;

public abstract class By extends org.openqa.selenium.By {
    public static org.openqa.selenium.By model(String value) {
        return org.openqa.selenium.By.cssSelector("[ng-model='" + value + "']");
    }
    public static org.openqa.selenium.By dataprovider(String value) {
        return org.openqa.selenium.By.cssSelector("[dataprovider='" + value + "']");
    }
}
