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

public class timKiemTraSachTest {

    private static final String MA_PHIEU_MUON = "PM0000001";
    private static final String TEN_NGUOI_MUON = "Nguyễn Văn A";
    private static final String TU_KHOA_KHONG_TON_TAI = "Trần Văn Z";

    private WebDriver driver;
    private WebDriverWait wait;

    // =====================================================
    // LOGIN 1 LẦN DUY NHẤT TRƯỚC TOÀN BỘ TEST CASE
    // =====================================================
    @BeforeClass
    public void setupOnce() {
        System.out.println("========== SETUP 1 LẦN ==========");

        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        driver = Constant.WEBDRIVER;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        loginAsUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));

        System.out.println("========== LOGIN THÀNH CÔNG ==========");
    }

    // =====================================================
    // MỖI TEST CHỈ CẦN VÀO LẠI TRANG QUẢN LÝ TRẢ SÁCH
    // KHÔNG LOGIN LẠI
    // =====================================================
    @BeforeMethod
    public void goToReturnPageBeforeEachTest() {
        System.out.println("========== VÀO QUẢN LÝ TRẢ SÁCH ==========");
        goToQuanLyTraSach();
    }

    // =====================================================
    // ĐÓNG TRÌNH DUYỆT SAU KHI CHẠY XONG TOÀN BỘ
    // =====================================================
    @AfterClass
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
    // ĐI TỚI QUẢN LÝ TRẢ SÁCH
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
    // TC01 - TÌM KIẾM THEO MÃ PHIẾU MƯỢN
    // =====================================================
    @Test
    public void TS_F001_timKiemThanhCongBangMaPhieuMuon() {

        timKiem(MA_PHIEU_MUON);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("table"),
                MA_PHIEU_MUON
        ));

        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(),'" + MA_PHIEU_MUON + "')]]")
        ));

        System.out.println("Row: " + row.getText());

        Assert.assertTrue(
                row.getText().contains(MA_PHIEU_MUON),
                "Không tìm thấy mã phiếu mượn!"
        );
    }

    // =====================================================
    // TC02 - TÌM KIẾM THEO TÊN NGƯỜI MƯỢN
    // =====================================================
    @Test
    public void TS_F002_timKiemThanhCongBangTenNguoiMuon() {

        timKiem(TEN_NGUOI_MUON);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("table"),
                TEN_NGUOI_MUON
        ));

        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(),'" + TEN_NGUOI_MUON + "')]]")
        ));

        System.out.println("Row: " + row.getText());

        Assert.assertTrue(
                row.getText().contains(TEN_NGUOI_MUON),
                "Không tìm thấy tên người mượn!"
        );
    }

    // =====================================================
    // TC03 - TÌM KIẾM KHÔNG TỒN TẠI
    // =====================================================
    @Test
    public void TS_F003_timKiemKhongThanhCongBangTenHoacMa() {

        timKiem(TU_KHOA_KHONG_TON_TAI);

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // chờ table load xong (có thể có row hoặc không)
        shortWait.until(d -> true);

        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));

        boolean hasRealData = false;

        for (WebElement row : rows) {
            String text = row.getText().toLowerCase();

            // loại row rác / empty row
            if (!text.contains("không có") &&
                    !text.contains("no data") &&
                    !text.trim().isEmpty()) {
                hasRealData = true;
                break;
            }
        }

        Assert.assertFalse(hasRealData,
                "Search không tồn tại nhưng vẫn còn dữ liệu trong bảng!");

        System.out.println("PASS: Không có kết quả tìm kiếm.");
    }

    // =====================================================
    // HÀM TÌM KIẾM CHUNG
    // =====================================================
    private void timKiem(String tuKhoa) {

        System.out.println("Tìm kiếm: " + tuKhoa);

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

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnSearch);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSearch);

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tbody//tr")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Không tìm thấy')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Khong tim thay')]"))
        ));
    }

    // =====================================================
    // INPUT
    // =====================================================
    private void typeFirstVisible(String value, By... locators) {
        WebElement element = findFirstVisible(locators);
        element.clear();
        element.sendKeys(value);
    }

    // =====================================================
    // CLICK
    // =====================================================
    private void clickFirstVisible(By... locators) {
        WebElement element = findFirstVisible(locators);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // =====================================================
    // FIND FIRST VISIBLE
    // =====================================================
    private WebElement findFirstVisible(By... locators) {

        for (By locator : locators) {

            List<WebElement> elements = driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
        }

        throw new NoSuchElementException("Không tìm thấy phần tử với các locator đã khai báo.");
    }
}