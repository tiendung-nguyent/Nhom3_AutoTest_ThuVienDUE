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
import org.openqa.selenium.By;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Random;

public class xacNhanDenSachTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setupLogin() {
        System.out.println("========== SETUP LOGIN ==========");

        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        driver = Constant.WEBDRIVER;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        loginAsUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));

        ensureQuanLyTraSachPage();

        System.out.println("========== READY FOR TEST ==========");
    }

    @AfterClass
    public void tearDown() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
    private void clickFirstVisible(By... locators) {

        WebElement element = findFirstVisible(locators);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});",
                    element
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    element
            );
        }
    }

    // =========================
// CHECK ĐÃ LOGIN HAY CHƯA
// =========================
    private boolean isLoggedIn() {
        try {
            return driver != null
                    && !driver.findElements(By.xpath("//aside")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }


    // =========================
// ĐẢM BẢO ĐANG Ở ĐÚNG TRANG QUẢN LÝ TRẢ SÁCH
// =========================
    private void ensureQuanLyTraSachPage() {
        try {
            boolean isCorrectPage =
                    driver.getCurrentUrl().contains("return")
                            || !driver.findElements(
                            By.xpath("//*[contains(text(),'Quản lý trả sách')]")
                    ).isEmpty();

            if (!isCorrectPage) {
                System.out.println("Sai trang -> Điều hướng lại");
                goToQuanLyTraSach();
            } else {
                System.out.println("Đúng trang Quản lý trả sách");
            }

        } catch (Exception e) {
            System.out.println("Không xác định được trang -> Điều hướng lại");
            goToQuanLyTraSach();
        }
    }


    // =========================
// LOGIN USER NHANH
// ========================
    @Test
    public void TC_DS_01_HienThiDungTabVaDanhSachChoXacNhanDenSach() {

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Danh sách hồ sơ chờ xác nhận đền sách')]")
                )
        );

        System.out.println("Đã truy cập tab: " + title.getText());

        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4') and not(contains(@style,'display: none'))]//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));

        Assert.assertFalse(rows.isEmpty(), "Không có hồ sơ nào!");

        System.out.println("Tổng số hồ sơ: " + rows.size());

        boolean foundExpectedRecord = false;

        for (WebElement row : rows) {

            if (row.getText().trim().isEmpty()) continue;

            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();
            String nguoiDen = row.findElement(By.xpath("./td[2]")).getText().trim();
            String maSach = row.findElement(By.xpath("./td[3]")).getText().trim();
            String sachCanDen = row.findElement(By.xpath("./td[4]")).getText().trim();
            String ngayBaoMat = row.findElement(By.xpath("./td[5]")).getText().trim();
            String trangThai = row.findElement(By.xpath("./td[6]")).getText().trim();

            System.out.println(maHoSo + " | " + sachCanDen + " | " + trangThai);

            // validate cơ bản
            Assert.assertFalse(maHoSo.isEmpty());
            Assert.assertFalse(maSach.isEmpty());
            Assert.assertFalse(sachCanDen.isEmpty());
            Assert.assertFalse(nguoiDen.isEmpty());
            Assert.assertFalse(ngayBaoMat.isEmpty());

            Assert.assertTrue(
                    trangThai.toLowerCase().contains("chờ đền sách") ||
                            trangThai.toLowerCase().contains("cho den sach")
            );

            // chỉ check record mẫu theo MA HO SO là đủ (KHÔNG nên fix full data)
            if (maHoSo.equals("PM0000001")) {
                foundExpectedRecord = true;

                Assert.assertEquals(maSach, "MS0002-001");
                Assert.assertEquals(nguoiDen, "Trần Thị B");
                Assert.assertEquals(sachCanDen, "Cấu Trúc Dữ Liệu");
            }
        }

        Assert.assertTrue(foundExpectedRecord, "Không tìm thấy hồ sơ PM0000001");
    }
    @Test
    public void TC_DS_02_MoPopupXacNhanDenSachVaHienThiDungThongTinHoSo() {

        // 1. Vào tab Trả sách
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        );
        tabDenSach.click();

        // 2. Chờ load danh sách
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Danh sách hồ sơ chờ xác nhận đền sách')]")
        ));

        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4') and not(contains(@style,'display: none'))]//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));

        Assert.assertFalse(rows.isEmpty(),
                "Không có hồ sơ nào trong danh sách chờ xác nhận đền sách!");

        // 3. Tìm dòng test data
        WebElement selectedRow = null;

        String expectedMaHoSo = "PM0000001";
        String expectedTenSach = "Cấu Trúc Dữ Liệu";
        String expectedMaSach = "MS0002-001";
        String expectedNguoiDen = "Trần Thị B";

        for (WebElement row : rows) {

            if (row.getText().trim().isEmpty()) continue;

            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();

            if (maHoSo.equalsIgnoreCase(expectedMaHoSo)) {
                selectedRow = row;
                break;
            }
        }

        Assert.assertNotNull(selectedRow,
                "Không tìm thấy hồ sơ " + expectedMaHoSo);

        // 4. Lấy data thật từ UI (KHÔNG hardcode ngày nữa)
        String maHoSo = selectedRow.findElement(By.xpath("./td[1]")).getText().trim();
        String nguoiDen = selectedRow.findElement(By.xpath("./td[2]")).getText().trim();
        String maSach = selectedRow.findElement(By.xpath("./td[3]")).getText().trim();
        String tenSach = selectedRow.findElement(By.xpath("./td[4]")).getText().trim();
        String ngayBaoMat = selectedRow.findElement(By.xpath("./td[5]")).getText().trim();
        String trangThai = selectedRow.findElement(By.xpath("./td[6]")).getText().trim();

        // 5. Verify dữ liệu dòng
        Assert.assertEquals(maHoSo, expectedMaHoSo, "Sai mã hồ sơ!");
        Assert.assertEquals(tenSach, expectedTenSach, "Sai tên sách!");
        Assert.assertEquals(maSach, expectedMaSach, "Sai mã sách!");
        Assert.assertEquals(nguoiDen, expectedNguoiDen, "Sai người đền!");

        // ❗ FIX QUAN TRỌNG: chỉ check format ngày, không fix cứng
        Assert.assertTrue(
                ngayBaoMat.matches("\\d{2}/\\d{2}/\\d{4}"),
                "Sai định dạng ngày báo mất!"
        );

        Assert.assertTrue(
                trangThai.toLowerCase().contains("chờ đền sách")
                        || trangThai.toLowerCase().contains("cho den sach"),
                "Sai trạng thái!"
        );

        // 6. Mở popup
        WebElement btnMoPopup = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(btnMoPopup)).click();

        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='popup-compensate-confirm' and not(contains(@style,'display: none'))]")
                )
        );

        Assert.assertTrue(popup.isDisplayed(),
                "Popup không hiển thị!");

        String popupText = popup.getText();

        // 7. Verify popup
        Assert.assertTrue(popupText.contains(maHoSo), "Popup sai mã hồ sơ!");
        Assert.assertTrue(popupText.contains(tenSach), "Popup sai tên sách!");
        Assert.assertTrue(popupText.contains(maSach), "Popup sai mã sách!");
        Assert.assertTrue(popupText.contains(nguoiDen), "Popup sai người đền!");
        Assert.assertTrue(popupText.contains(ngayBaoMat), "Popup sai ngày báo mất!");

        // 8. Check buttons
        Assert.assertTrue(
                popup.findElement(By.xpath(".//button[contains(text(),'Hủy')]")).isDisplayed(),
                "Thiếu nút Hủy!"
        );

        Assert.assertTrue(
                driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']")).isDisplayed(),
                "Thiếu nút Xác nhận!"
        );

        // 9. Log
        System.out.println("PASS TC_DS_02");
        System.out.println("Popup mở đúng hồ sơ: " + maHoSo);
    }
    @Test
    public void TC_DS_03_XacNhanDenSachThanhCongVoiHoSoHopLe() {

        String targetMaHoSo = "PM0000001";
        String newBookCode = "MS0001-010";

        // =========================
        // 1. Vào màn hình Trả sách
        // =========================
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        );
        tabDenSach.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Danh sách hồ sơ chờ xác nhận đền sách')]")
        ));

        // =========================
        // 2. Load bảng
        // =========================
        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4')]//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));
        Assert.assertFalse(rows.isEmpty(), "Danh sách rỗng!");

        // =========================
        // 3. Tìm row theo mã hồ sơ
        // =========================
        WebElement selectedRow = null;

        for (WebElement row : rows) {
            if (row.getText().trim().isEmpty()) continue;

            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();

            if (maHoSo.equals(targetMaHoSo)) {
                selectedRow = row;
                break;
            }
        }

        Assert.assertNotNull(selectedRow, "Không tìm thấy hồ sơ: " + targetMaHoSo);

        // =========================
        // 4. Click mở popup
        // =========================
        WebElement btnOpen = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
        );

        wait.until(ExpectedConditions.elementToBeClickable(btnOpen));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                btnOpen
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnOpen);

        // =========================
        // 5. Chờ popup
        // =========================
        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("popup-compensate-confirm")
                )
        );

        Assert.assertTrue(popup.isDisplayed(), "Popup không hiển thị");

        // =========================
        // 6. Chọn PASS
        // =========================
        WebElement radioPass = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//input[@name='compInspectionResult' and @value='pass']")
                )
        );
        radioPass.click();

        // =========================
        // 7. Nhập mã sách đền
        // =========================
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("compInspectionNote")
                )
        );
        input.clear();
        input.sendKeys(newBookCode);

        // =========================
        // 8. Submit
        // =========================
        WebElement btnConfirm = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[@onclick='submitCompensateConfirm()']")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirm);

        // =========================
        // 9. Chờ popup biến mất
        // =========================
        wait.until(
                ExpectedConditions.invisibilityOf(popup)
        );

        // =========================
        // 10. VERIFY kết quả (KHÔNG dùng data-record-id)
        // =========================
        boolean stillExist = driver.findElements(
                By.xpath("//div[contains(@id,'tab-4')]//td[contains(text(),'" + targetMaHoSo + "')]")
        ).size() > 0;

        Assert.assertFalse(stillExist, "Hồ sơ vẫn còn tồn tại sau khi xác nhận!");

        System.out.println("PASS TC03 - Xác nhận đền sách thành công");
    }

    @Test
    public void TC_DS_04_HuyThaoTacKhiSachDenKhongDatYeuCau() throws Exception {
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Danh sách hồ sơ chờ xác nhận đền sách')]")
        ));

        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4') and not(contains(@style,'display: none'))]//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));

        Assert.assertFalse(rows.isEmpty());

        WebElement selectedRow = null;

        for (WebElement row : rows) {
            if (row.getText().trim().isEmpty()) {
                continue;
            }
            String currentMaHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();
            if (currentMaHoSo.equals("PM0000002")) {
                selectedRow = row;
                break;
            }
        }

        Assert.assertNotNull(selectedRow);

        String maHoSo = selectedRow.findElement(By.xpath("./td[1]")).getText().trim();
        String trangThaiBanDau = selectedRow.findElement(By.xpath("./td[6]")).getText().trim();
        int soLuongTruoc = rows.size();

        WebElement btnChoDenSach = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btnChoDenSach);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnChoDenSach);

        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='popup-compensate-confirm' and not(contains(@style,'display: none'))]")
                )
        );

        Assert.assertTrue(popup.isDisplayed());

        WebElement radioKhongDat = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='popup-compensate-confirm']//input[@name='compInspectionResult' and @value='fail']")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", radioKhongDat);
        Thread.sleep(300);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioKhongDat);

        Assert.assertTrue(radioKhongDat.isSelected());

        WebElement btnHuy = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='popup-compensate-confirm']//button[contains(@onclick,'closeAllPopups')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btnHuy);
        Thread.sleep(300);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHuy);

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath("//div[@id='popup-compensate-confirm' and not(contains(@style,'display: none'))]")
                )
        );

        WebElement refreshedTableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4') and not(contains(@style,'display: none'))]//table/tbody")
                )
        );

        List<WebElement> refreshedRows = refreshedTableBody.findElements(By.xpath("./tr"));

        WebElement sameRow = null;
        for (WebElement row : refreshedRows) {
            if (row.getText().trim().isEmpty()) {
                continue;
            }
            String currentMaHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();
            if (currentMaHoSo.equals("PM0000002")) {
                sameRow = row;
                break;
            }
        }

        Assert.assertNotNull(sameRow);
        String trangThaiSauHuy = sameRow.findElement(By.xpath("./td[6]")).getText().trim();
        Assert.assertEquals(trangThaiSauHuy, trangThaiBanDau);
        Assert.assertEquals(refreshedRows.size(), soLuongTruoc);
    }

    @Test
    public void TC_DS_05_KiemTraQuyenXacNhanDenSach() throws Exception {

        // 1. Login user KHÔNG có quyền (quan trọng)
        loginWithNoPermissionUser();

        // 2. Vào màn hình Trả sách
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);

        // 3. Chờ table load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='tab-4']//table")
        ));

        // 4. Kiểm tra KHÔNG có nút xác nhận
        List<WebElement> buttons = driver.findElements(
                By.xpath("//span[contains(@class,'compensate-confirm-trigger')]")
        );

        // ❗ EXPECT: không được thấy nút
        Assert.assertTrue(buttons.isEmpty(),
                "User không có quyền nhưng vẫn thấy nút Xác nhận đền sách");
    }

    // =========================

    private void loginAsUser() {
        // Nếu đã đang ở hệ thống và sidebar tồn tại => bỏ qua login
        try {
            if (driver.getCurrentUrl().contains("dashboard") ||
                    !driver.findElements(By.xpath("//aside")).isEmpty()) {

                System.out.println("⚡ Đã đăng nhập sẵn - Bỏ qua login");
                return;
            }
        } catch (Exception ignored) {}

        // Load trang
        driver.get(Constant.THUVIEN_URL);

        // Giảm thời gian chờ bằng wait ngắn riêng cho login
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));

        // Username
        WebElement username = findFast(shortWait,
                By.id("username"),
                By.name("username"),
                By.cssSelector("input[type='text']")
        );
        username.clear();
        username.sendKeys(Constant.USERNAME);

        // Password
        WebElement password = findFast(shortWait,
                By.id("password"),
                By.name("password"),
                By.cssSelector("input[type='password']")
        );
        password.clear();
        password.sendKeys(Constant.PASSWORD);

        // Submit nhanh bằng ENTER nếu có thể
        password.sendKeys(Keys.ENTER);

        // Nếu ENTER fail thì click nút
        try {
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));
        } catch (Exception e) {
            WebElement loginBtn = findFast(shortWait,
                    By.id("login-btn"),
                    By.cssSelector("button[type='submit']")
            );

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
        }

        // Chờ dashboard
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));

        System.out.println("✅ Login thành công");
    }
    private boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public void loginWithNoPermissionUser() {

        driver.get("http://your-url-login-page");

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );
        username.sendKeys("user_khong_quyen");

        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys("123456");

        WebElement btnLogin = driver.findElement(By.id("btnLogin"));
        btnLogin.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Dashboard')]")
        ));
    }
    private WebElement findFast(WebDriverWait customWait, By... locators) {
        for (By locator : locators) {
            try {
                return customWait.until(
                        ExpectedConditions.presenceOfElementLocated(locator)
                );
            } catch (Exception ignored) {}
        }
        throw new NoSuchElementException("❌ Không tìm thấy phần tử với các locator đã cung cấp!");
    }
// ĐI TỚI QUẢN LÝ TRẢ SÁCH
// =========================
    private void goToQuanLyTraSach() {
        WebElement traSachMenu = findFirstClickable(
                By.xpath("//span[contains(text(),'Trả sách')]"),
                By.xpath("//a[contains(.,'Trả sách')]"),
                By.xpath("//div[contains(text(),'Trả sách')]")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                traSachMenu
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                traSachMenu
        );

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Quản lý trả sách')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(@onclick,'tab-3')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(@onclick,'tab-4')]")
                )
        ));

        System.out.println(" Đã vào màn hình Quản lý trả sách!");
    }


    // =========================
// FIND ELEMENT ĐẦU TIÊN HIỂN THỊ
// =========================
    private WebElement findFirstVisible(By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                );
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception ignored) {}
        }

        throw new NoSuchElementException(" Không tìm thấy phần tử hiển thị!");
    }


    // =========================
// FIND ELEMENT ĐẦU TIÊN CLICK ĐƯỢC
// =========================
    private WebElement findFirstClickable(By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(
                        ExpectedConditions.elementToBeClickable(locator)
                );
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception ignored) {}
        }

        throw new NoSuchElementException(" Không tìm thấy phần tử click được!");
    }


    // =========================
// CLICK JS CHUẨN
// =========================
    private void clickByJS(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                element
        );
    }

}