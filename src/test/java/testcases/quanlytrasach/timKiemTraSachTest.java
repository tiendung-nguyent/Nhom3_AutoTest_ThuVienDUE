package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class timKiemTraSachTest {
    private static final String MA_PHIEU_MUON = "PM0000001";
    private static final String TEN_NGUOI_MUON = "Lê Thị Bình";
    private static final String TU_KHOA_KHONG_TON_TAI = "Trần Văn Z";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void preCondition() {
        loginAsUser();
        goToQuanLyTraSach();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsUser() {
        driver.get(Constant.THUVIEN_URL);

        typeFirstVisible(
                Constant.USERNAME,
                By.id("username"),
                By.name("username"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input[placeholder*='username']"),
                By.cssSelector("input[placeholder*='Ten']"),
                By.cssSelector("input[placeholder*='dang nhap']")
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
                By.xpath("//button[contains(normalize-space(), 'Dang nhap')]"),
                By.xpath("//button[contains(normalize-space(), 'Login')]")
        );

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("tong-quan"),
                ExpectedConditions.urlContains("dashboard"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside"))
        ));
    }

    private void goToQuanLyTraSach() {
        clickFirstVisible(
                By.xpath("//aside//a[@href='/return/']"),
//                By.xpath("//aside//*[contains(normalize-space(), 'Tra sach')]"),
                By.xpath("//aside//a[.//span[normalize-space()='Trả sách']]")
        );

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("return"),
                ExpectedConditions.urlContains("quan-ly-tra-sach"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(normalize-space(), 'Tim kiem')]")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='search']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table"))
        ));
    }

    @Test
    public void TS_F001_timKiemThanhCongBangMaPhieuMuon() {

        timKiem(MA_PHIEU_MUON);

        // SỬA CHỖ NÀY: Dùng By.tagName("table") thay vì By.id("loan-table")
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("table"),
                MA_PHIEU_MUON
        ));

        // Lấy dòng dữ liệu (Giữ nguyên đoạn này của bạn vì nó đang viết đúng)
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(), '" + MA_PHIEU_MUON + "')]]")
        ));

        System.out.println("✅ Row text = " + row.getText());

        Assert.assertTrue(
                row.getText().trim().contains(MA_PHIEU_MUON),
                "Không tìm thấy mã phiếu mượn trong kết quả"
        );
    }

    @Test
    public void TS_F002_timKiemThanhCongBangTenNguoiMuon() {

        timKiem(TEN_NGUOI_MUON);

        // SỬA CHỖ NÀY: Dùng By.tagName("table") thay vì By.id("loan-table")
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("table"),
                TEN_NGUOI_MUON
        ));

        // Lấy dòng dữ liệu (Giữ nguyên đoạn này của bạn vì nó đang viết đúng)
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(), '" + TEN_NGUOI_MUON + "')]]")
        ));

        System.out.println("✅ Row text = " + row.getText());

        Assert.assertTrue(
                row.getText().trim().contains(TEN_NGUOI_MUON),
                "Không tìm thấy tên người mượn trong kết quả"
        );
    }

    @Test
    public void TS_F003_timKiemKhongThanhCongBangTenHoacMa() {

        timKiem(TU_KHOA_KHONG_TON_TAI);

        // SỬA CHỖ NÀY: Dùng By.tagName("table") thay vì By.id("loan-table")
        try {
            System.out.println("🔍 Đang tìm kiếm từ khóa không tồn tại: " + TU_KHOA_KHONG_TON_TAI);
            Thread.sleep(3000); // Đợi 3 giây rồi mới chạy tiếp các lệnh dưới
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lấy dòng dữ liệu (Giữ nguyên đoạn này của bạn vì nó đang viết đúng)
        WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//tbody//tr[td[contains(normalize-space(), '" + TU_KHOA_KHONG_TON_TAI + "')]]")
        ));

        System.out.println("✅ Row text = " + row.getText());

        Assert.assertTrue(
                row.getText().trim().contains(TU_KHOA_KHONG_TON_TAI),
                "Không tìm thấy sách mượn"
        );
    }


    private void timKiem(String tuKhoa) {

        System.out.println("🔍 Tim kiem: " + tuKhoa);

        // ===== TÌM INPUT =====
        WebElement searchInput = wait.until(d -> {
            for (By locator : new By[]{
                    By.id("search-loan-code"),
                    By.name("maPhieuMuon"),
                    By.xpath("//input[@placeholder='Nhập thông tin để tìm kiếm']"),
                    By.cssSelector("input[type='text']")
            }) {
                List<WebElement> els = d.findElements(locator);
                for (WebElement el : els) {
                    if (el.isDisplayed() && el.isEnabled()) return el;
                }
            }
            return null;
        });

        // ===== NHẬP =====
        searchInput.click();   // ⚠️ rất quan trọng (UI custom)
        searchInput.clear();
        searchInput.sendKeys(tuKhoa);

        // ===== CLICK BUTTON =====
        WebElement btnSearch = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@onclick='performSearch()']")
        ));
        // scroll + click JS (tránh bị che)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnSearch);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSearch);

        // ===== CHỜ LOAD =====
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tr")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Không tìm thấy')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'Khong tim thay')]"))
        ));
    }

    private void typeFirstVisible(String value, By... locators) {
        WebElement element = findFirstVisible(locators);
        element.clear();
        element.sendKeys(value);
    }

    private void clickFirstVisible(By... locators) {
        WebElement element = findFirstVisible(locators);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    private WebElement findFirstVisible(By... locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return element;
                }
            }
        }
        throw new NoSuchElementException("Khong tim thay phan tu voi cac locator da khai bao.");
    }

    private String layTextNeuCo(WebElement row, String cssSelector) {
        List<WebElement> elements = row.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            return "";
        }
        return elements.get(0).getText().trim();
    }
}
