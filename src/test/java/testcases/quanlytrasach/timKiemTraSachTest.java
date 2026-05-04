package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class timKiemTraSachTest {

    private static final String TEN_NGUOI_MUON = "Nguyễn Văn A";
    private static final String TU_KHOA_KHONG_TON_TAI = "Trần Văn Z";

    private WebDriver driver;
    private WebDriverWait wait;
    @BeforeMethod
    public void setUp() {
        System.out.println("========== SETUP MỖI TEST ==========");

        Constant.WEBDRIVER = new ChromeDriver();
        Constant.WEBDRIVER.manage().window().maximize();

        driver = Constant.WEBDRIVER;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        loginAsUser();          // login
        goToQuanLyTraSach();    // vào trang luôn
    }

    @AfterMethod
    public void tearDown() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================
    private void loginAsUser() {
        driver.get(Constant.THUVIEN_URL);

        typeFirstVisible(
                Constant.USERNAME,
                By.id("username"),
                By.name("username"),
                By.cssSelector("input[type='text']")
        );

        typeFirstVisible(
                Constant.PASSWORD,
                By.id("password"),
                By.name("password"),
                By.cssSelector("input[type='password']")
        );

        clickFirstVisible(
                By.id("login-btn"),
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(text(),'Đăng nhập')]"),
                By.xpath("//button[contains(text(),'Login')]")
        );

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")),
                ExpectedConditions.urlContains("dashboard"),
                ExpectedConditions.urlContains("tong-quan")
        ));
    }

    // =====================================================
    // NAVIGATION
    // =====================================================
    private void goToQuanLyTraSach() {

        clickFirstVisible(
                By.xpath("//aside//a[@href='/return/']"),
                By.xpath("//aside//a[contains(.,'Trả sách')]"),
                By.xpath("//aside//*[contains(text(),'Trả sách')]"),
                By.xpath("//span[contains(text(),'Trả sách')]")
        );

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("return"),
                ExpectedConditions.urlContains("quan-ly-tra-sach"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@onclick,'performSearch')]")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='search']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table"))
        ));
    }

    // =====================================================
    // TEST CASES
    // =====================================================

    // TC01 - tìm theo mã ngẫu nhiên
    @Test
    public void PA_28() {
        List<WebElement> columns = driver.findElements(By.xpath("//table//tbody//tr/td[1]"));

        if (columns.isEmpty()) {
            Assert.fail("Không có dữ liệu trong bảng để test!");
        }

        Random rand = new Random();
        int randomIndex = rand.nextInt(columns.size());
        String maNgauNhien = columns.get(randomIndex).getText().trim();

        timKiem(maNgauNhien);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(),'" + maNgauNhien + "')]]")
        ));

        WebElement row = driver.findElement(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(),'" + maNgauNhien + "')]]")
        );

        Assert.assertTrue(
                row.getText().contains(maNgauNhien),
                "Kết quả không đúng!"
        );
    }

    // TC02 - tìm theo tên
    @Test
    public void PA_29() {
        timKiem(TEN_NGUOI_MUON);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("table"),
                TEN_NGUOI_MUON
        ));

        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(),'" + TEN_NGUOI_MUON + "')]]")
        ));

        Assert.assertTrue(
                row.getText().contains(TEN_NGUOI_MUON),
                "Không tìm thấy tên!"
        );
    }

    // TC03 - tìm không tồn tại
    @Test
    public void PA_30() {
        timKiem(TU_KHOA_KHONG_TON_TAI);

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));

        boolean hasRealData = false;

        for (WebElement row : rows) {
            String text = row.getText().toLowerCase();

            if (!text.contains("không có") &&
                    !text.contains("no data") &&
                    !text.trim().isEmpty()) {
                hasRealData = true;
                break;
            }
        }

        Assert.assertFalse(hasRealData,
                "Search sai: vẫn còn dữ liệu!");
    }

    // =====================================================
    // SEARCH FUNCTION
    // =====================================================
    private void timKiem(String tuKhoa) {

        WebElement searchInput = wait.until(driver -> {
            for (By locator : new By[]{
                    By.id("search-loan-code"),
                    By.name("maPhieuMuon"),
                    By.xpath("//input[@placeholder='Nhập thông tin để tìm kiếm']"),
                    By.cssSelector("input[type='search']"),
                    By.cssSelector("input[type='text']")
            }) {
                List<WebElement> elements = driver.findElements(locator);

                for (WebElement el : elements) {
                    if (el.isDisplayed() && el.isEnabled()) {
                        return el;
                    }
                }
            }
            return null;
        });

        searchInput.clear();
        searchInput.sendKeys(tuKhoa);

        WebElement btnSearch = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@onclick='performSearch()']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSearch);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tbody//tr")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Không tìm thấy')]"))
        ));
    }

    // =====================================================
    // UTIL
    // =====================================================
    private void typeFirstVisible(String value, By... locators) {
        WebElement element = findFirstVisible(locators);
        element.clear();
        element.sendKeys(value);
    }

    private void clickFirstVisible(By... locators) {
        WebElement element = findFirstVisible(locators);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private WebElement findFirstVisible(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {}
            }
        }

        throw new NoSuchElementException("Không tìm thấy element!");
    }
}