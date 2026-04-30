package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pageobjects.layout.SidebarPage;

public class GeneralPage extends SidebarPage {

    private final By lblWelcomeMessage = By.xpath("//div[@class='account']");

    protected WebElement getLblWelcomeMessage() {
        return driver.findElement(lblWelcomeMessage);
    }

    public String getWelcomeMessage() {
        return this.getLblWelcomeMessage().getText();
    }

    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        scrollToElement(element);
    }
}