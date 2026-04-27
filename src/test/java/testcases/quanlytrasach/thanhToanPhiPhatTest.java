package testcases.quanlytrasach;
import common.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class thanhToanPhiPhatTest {
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

        // 1. Vào màn hình Trả sách
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        // 2. Chuyển sang tab Thanh toán phí phạt (tab-2)
        WebElement tabThanhToan = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@onclick, \"tab-2\")]")));
        clickByJS(tabThanhToan);

        // 3. Đợi nội dung tab-2 hiển thị ổn định để không bị nhảy về tab cũ
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tab-2")));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void TC_TP_01_HienThiDanhSachPhiPhat() {
        // 1. Đợi bảng hiển thị
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='tab-2']//div[contains(@class, 'data-table-scroll')]")));

        // 2. Lấy tất cả các dòng dữ liệu trong bảng
        List<WebElement> rows = driver.findElements(By.xpath("//div[@id='tab-2']//table/tbody/tr"));

        Assert.assertTrue(rows.size() > 0, "❌ Danh sách phí phạt trống, không có dữ liệu để test!");

        boolean foundUserA = false; // Người còn nợ > 0
        boolean foundUserB = false; // Người đã trả hết = 0

        System.out.println("📊 Bắt đầu kiểm tra danh sách phí phạt:");

        for (WebElement row : rows) {
            String tenNguoiDung = row.findElement(By.xpath("./td[2]")).getText();
            String tongTienRaw = row.findElement(By.xpath("./td[4]")).getText();

            // Làm sạch số tiền để kiểm tra
            String cleanAmount = tongTienRaw.replace(".", "").replace(",", "").replace("đ", "").trim();
            long soTien = Long.parseLong(cleanAmount);

            if (soTien > 0) {
                System.out.println("✅ User A (Còn nợ): " + tenNguoiDung + " - " + tongTienRaw);
                foundUserA = true;
            } else if (soTien == 0) {
                System.out.println("✅ User B (Hết nợ): " + tenNguoiDung + " - " + tongTienRaw);
                foundUserB = true;
            }
        }

        // 3. Kiểm tra xem có đủ cả 2 loại người dùng như yêu cầu không
        Assert.assertTrue(foundUserA, "❌ Không tìm thấy User A (người còn nợ) trong danh sách!");
        Assert.assertTrue(foundUserB, "❌ Không tìm thấy User B (người có tiền phạt = 0đ) trong danh sách!");

        System.out.println("🎉 Pass: Danh sách hiển thị đúng và đủ các loại đối tượng theo yêu cầu TC_TP_01.");
    }

    

    // --- CÁC HÀM BỔ TRỢ (HELPERS) ---

    private void clickByJS(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // THÊM HÀM NÀY: Dành cho trường hợp bạn đã có WebElement rồi (để hết bôi đỏ)
    private void clickByJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void loginAsUser() {
        driver.get(Constant.THUVIEN_URL);
        typeFirstVisible(Constant.USERNAME, By.id("username"), By.name("username"));
        typeFirstVisible(Constant.PASSWORD, By.id("password"), By.name("password"));
        clickFirstVisible(By.id("login-btn"), By.cssSelector("button[type='submit']"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));
    }

    private void typeFirstVisible(String text, By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                element.clear();
                element.sendKeys(text);
                return;
            } catch (Exception ignored) {}
        }
    }

    private void clickFirstVisible(By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (Exception ignored) {}
        }
    }
}
