package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;

public class LoginPage extends GeneralPage {

    // --- Locators từ hình ảnh bạn cung cấp ---
    private final By txtUsername = By.id("loginUsername");
    private final By txtPassword = By.id("loginPassword");
    private final By btnLogin = By.xpath("//button[@type='submit' and contains(text(),'Đăng nhập')]");

    // --- Elements ---
    public WebElement getTxtUsername() {
        return Constant.WEBDRIVER.findElement(txtUsername);
    }

    public WebElement getTxtPassword() {
        return Constant.WEBDRIVER.findElement(txtPassword);
    }

    public WebElement getBtnLogin() {
        return Constant.WEBDRIVER.findElement(btnLogin);
    }

    // --- Actions ---

    /**
     * Thực hiện các bước đăng nhập
     * @param username Tên đăng nhập (Mã sinh viên)
     * @param password Mật khẩu
     */
    public void login(String username, String password) {
        // Cuộn tới form đăng nhập nếu cần
        this.scrollToElement(txtUsername);

        this.getTxtUsername().sendKeys(username);
        this.getTxtPassword().sendKeys(password);

        this.getBtnLogin().click();
    }
}