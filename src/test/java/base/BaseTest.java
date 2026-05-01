package base;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pageobjects.auth.LoginPage;
import pageobjects.auth.HomePage;

import java.time.Duration;

public class BaseTest {
    protected org.openqa.selenium.WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new org.openqa.selenium.chrome.ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }
        driver = Constant.WEBDRIVER;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }

    protected void login() {
        HomePage homePage = new HomePage();
        homePage.open();
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        
        wait.until(driver -> 
            driver.getCurrentUrl().contains("dashboard") || 
            driver.getCurrentUrl().contains("tong-quan")
        );
    }
}
