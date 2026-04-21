package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import common.Constant;

public class GeneralPage extends SidebarPage {

    // Các locators dùng chung cho header/footer (nếu có)
    private final By lblWelcomeMessage = By.xpath("//div[@class='account']");

    protected WebElement getLblWelcomeMessage() {
        return Constant.WEBDRIVER.findElement(lblWelcomeMessage);
    }

    public String getWelcomeMessage() {
        return this.getLblWelcomeMessage().getText();
    }

    /**
     * Cuộn trang đến phần tử được chỉ định
     * @param locator locator của phần tử cần cuộn tới
     */
    public void scrollToElement(By locator) {
        WebElement element = Constant.WEBDRIVER.findElement(locator);
        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }


}