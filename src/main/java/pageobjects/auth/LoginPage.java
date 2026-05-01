package pageobjects.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pageobjects.GeneralPage;

public class LoginPage extends GeneralPage {

    private final By txtUsername = By.id("loginUsername");
    private final By txtPassword = By.id("loginPassword");
    private final By btnLogin = By.xpath("//button[@type='submit' and contains(text(),'Đăng nhập')]");

    public WebElement getTxtUsername() {
        return driver.findElement(txtUsername);
    }

    public WebElement getTxtPassword() {
        return driver.findElement(txtPassword);
    }

    public WebElement getBtnLogin() {
        return driver.findElement(btnLogin);
    }

    public void login(String username, String password) {
        this.scrollToElement(txtUsername);
        this.getTxtUsername().sendKeys(username);
        this.getTxtPassword().sendKeys(password);
        this.getBtnLogin().click();
    }
}
