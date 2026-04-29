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

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void preCondition() {
        loginAsUser();

    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

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
        String tabClass = tabDenSach.getAttribute("class").toLowerCase();

        Assert.assertTrue(
                tabClass.contains("active")
                        || tabClass.contains("selected")
                        || tabClass.contains("orange"),
                "Tab Xác nhận đền sách chưa được kích hoạt!"
        );
        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@id,'tab-4') and not(contains(@style,'display: none'))]//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));

        Assert.assertFalse(
                rows.isEmpty(),
                "Không có hồ sơ nào trong danh sách chờ xác nhận đền sách!"
        );

        System.out.println("Tổng số hồ sơ hiển thị: " + rows.size());

        int validRecords = 0;
        boolean foundExpectedRecord = false;
        for (int i = 0; i < rows.size(); i++) {

            WebElement row = rows.get(i);

            if (row.getText().trim().isEmpty()) {
                continue;
            }

            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();
            String nguoiDen = row.findElement(By.xpath("./td[2]")).getText().trim();
            String maSach = row.findElement(By.xpath("./td[3]")).getText().trim();
            String sachCanDen = row.findElement(By.xpath("./td[4]")).getText().trim();
            String ngayBaoMat = row.findElement(By.xpath("./td[5]")).getText().trim();
            String trangThai = row.findElement(By.xpath("./td[6]")).getText().trim();

            System.out.println("Hồ sơ #" + (i + 1)
                    + " | Mã hồ sơ: " + maHoSo
                    + " | Sách cần đền: " + sachCanDen
                    + " | Mã sách: " + maSach
                    + " | Người đền: " + nguoiDen
                    + " | Ngày báo mất: " + ngayBaoMat
                    + " | Trạng thái: " + trangThai);

            // 7. VERIFY KHÔNG RỖNG
            Assert.assertFalse(maHoSo.isEmpty(), "Mã hồ sơ trống!");
            Assert.assertFalse(sachCanDen.isEmpty(), "Sách cần đền trống!");
            Assert.assertFalse(maSach.isEmpty(), "Mã sách trống!");
            Assert.assertFalse(nguoiDen.isEmpty(), "Người đền trống!");
            Assert.assertFalse(ngayBaoMat.isEmpty(), "Ngày báo mất trống!");
            Assert.assertTrue(
                    trangThai.equalsIgnoreCase("Chờ đền sách")
                            || trangThai.toLowerCase().contains("chờ đền sách")
                            || trangThai.toLowerCase().contains("cho den sach"),
                    "Phát hiện hồ sơ sai trạng thái: " + trangThai
            );
            if (
                    maHoSo.equals("HS-001")
                            && sachCanDen.equals("Kinh tế vi mô")
                            && maSach.equals("KT001")
                            && nguoiDen.equals("Trần Thị Bình")
                            && ngayBaoMat.equals("18/01/2026")
                            && (trangThai.equalsIgnoreCase("Chờ đền sách")
                            || trangThai.toLowerCase().contains("chờ đền sách"))
            ) {
                foundExpectedRecord = true;
            }

            validRecords++;
        }
        Assert.assertTrue(
                validRecords > 0,
                "Không tồn tại hồ sơ nào ở trạng thái Chờ đền sách!"
        );

        Assert.assertTrue(
                foundExpectedRecord,
                "Không tìm thấy hồ sơ mẫu: HS-001 | Kinh tế vi mô | KT001 | Trần Thị Bình | 18/01/2026"
        );}

    @Test
    public void TC_DS_02_MoPopupXacNhanDenSachVaHienThiDungThongTinHoSo() throws Exception {
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

        Assert.assertFalse(
                rows.isEmpty(),
                "Không có hồ sơ nào trong danh sách chờ xác nhận đền sách!"
        );

        WebElement selectedRow = null;

        String expectedMaHoSo = "HS-001";
        String expectedTenSach = "Kinh tế vi mô";
        String expectedMaSach = "KT001";
        String expectedNguoiDen = "Trần Thị Bình";
        String expectedNgayBaoMat = "18/01/2026";

        for (WebElement row : rows) {

            if (row.getText().trim().isEmpty()) {
                continue;
            }

            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();

            if (maHoSo.equalsIgnoreCase(expectedMaHoSo)) {
                selectedRow = row;
                break;
            }
        }

        Assert.assertNotNull(
                selectedRow,
                "Không tìm thấy hồ sơ HS-001!"
        );

        String maHoSo = selectedRow.findElement(By.xpath("./td[1]")).getText().trim();
        String nguoiDen = selectedRow.findElement(By.xpath("./td[2]")).getText().trim();
        String maSach = selectedRow.findElement(By.xpath("./td[3]")).getText().trim();
        String tenSach = selectedRow.findElement(By.xpath("./td[4]")).getText().trim();
        String ngayBaoMat = selectedRow.findElement(By.xpath("./td[5]")).getText().trim();
        String trangThai = selectedRow.findElement(By.xpath("./td[6]")).getText().trim();

        Assert.assertEquals(maHoSo, expectedMaHoSo, "Sai mã hồ sơ!");
        Assert.assertEquals(tenSach, expectedTenSach, "Sai tên sách!");
        Assert.assertEquals(maSach, expectedMaSach, "Sai mã sách!");
        Assert.assertEquals(nguoiDen, expectedNguoiDen, "Sai người đền!");
        Assert.assertEquals(ngayBaoMat, expectedNgayBaoMat, "Sai ngày báo mất!");
        Assert.assertTrue(
                trangThai.toLowerCase().contains("chờ đền sách") ||
                        trangThai.toLowerCase().contains("cho den sach"),
                "Sai trạng thái hồ sơ!"
        );

        System.out.println("Hồ sơ được chọn:");
        System.out.println("Mã hồ sơ: " + maHoSo);
        System.out.println("Người đền: " + nguoiDen);
        System.out.println("Mã sách: " + maSach);
        System.out.println("Sách cần đền: " + tenSach);
        System.out.println("Ngày báo mất: " + ngayBaoMat);

        WebElement btnMoPopup = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                btnMoPopup
        );

        Thread.sleep(500);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnMoPopup
        );

        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='popup-compensate-confirm' and not(contains(@style,'display: none'))]")
                )
        );

        Assert.assertTrue(
                popup.isDisplayed(),
                "Popup Xác nhận đền sách không hiển thị!"
        );
        String popupText = popup.getText();

        Assert.assertTrue(popupText.contains(maHoSo), "Popup sai Mã hồ sơ!");
        Assert.assertTrue(popupText.contains(tenSach), "Popup sai Sách cần đền!");
        Assert.assertTrue(popupText.contains(maSach), "Popup sai Mã sách!");
        Assert.assertTrue(popupText.contains(nguoiDen), "Popup sai Người đền!");
        Assert.assertTrue(popupText.contains(ngayBaoMat), "Popup sai Ngày báo mất!");

        WebElement btnHuy = popup.findElement(
                By.xpath(".//button[contains(@onclick,'closeAllPopups') and normalize-space()='Hủy']")
        );

        Assert.assertTrue(
                btnHuy.isDisplayed(),
                "Popup thiếu nút Hủy!"
        );

       WebElement btnConfirmPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[@onclick='submitCompensateConfirm()']")
        ));

        Assert.assertTrue(
                btnConfirmPopup.isDisplayed(),
                "Popup thiếu nút Xác nhận hoàn tất!"
        );
         System.out.println("PASS TC_DS_02");
        System.out.println("Popup mở đúng hồ sơ HS-001");
        System.out.println("Thông tin popup khớp dữ liệu dòng đã chọn");
        System.out.println("Nút 'Xác nhận hoàn tất' đã hiển thị sẵn sàng.");

        }


    @Test
    public void TC_DS_03_XacNhanDenSachThanhCongVoiHoSoHopLe() throws Exception {
        String targetMaHoSo = "HS-002";
        String newBookCode = "MS0001-010";

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick,'tab-4')]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("compensateTableBody")));

        WebElement selectedRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[@data-record-id='" + targetMaHoSo + "']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", selectedRow);
        Thread.sleep(500);

        WebElement btnChoDenSach = selectedRow.findElement(By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnChoDenSach);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-compensate-confirm")));

        WebElement radioPass = driver.findElement(By.xpath("//input[@value='pass']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioPass);

        WebElement txtMaSachDenBu = driver.findElement(By.id("compInspectionNote"));
        txtMaSachDenBu.clear();
        txtMaSachDenBu.sendKeys(newBookCode);

        WebElement btnXacNhanHoanTat = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhanHoanTat);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-compensate-confirm")));

        Thread.sleep(2000);
        List<WebElement> checkExist = driver.findElements(By.xpath("//tr[@data-record-id='" + targetMaHoSo + "']"));
        Assert.assertTrue(checkExist.isEmpty());
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
            if (currentMaHoSo.equals("HS-002")) {
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
            if (currentMaHoSo.equals("HS-002")) {
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
    public void TC_DS_05_KhongChoPhepXacNhanVoiHoSoKhongHopLe() throws Exception {
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick,'tab-4')]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        List<WebElement> completedRows = driver.findElements(
                By.xpath("//tr[descendant::span[contains(@class,'badge-green-solid')]]")
        );

        if (!completedRows.isEmpty()) {
            WebElement row = completedRows.get(0);
            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();

            List<WebElement> btnTriggers = row.findElements(
                    By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
            );

            Assert.assertTrue(btnTriggers.isEmpty());
        }

        WebElement rowPending = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[descendant::span[contains(text(),'Chờ đền sách')]]")
        ));

        WebElement trigger = rowPending.findElement(By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-compensate-confirm")));

        WebElement inputMaSach = driver.findElement(By.id("compInspectionNote"));
        inputMaSach.clear();
        inputMaSach.sendKeys("MA-SACH-AO-12345");

        WebElement btnConfirm = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirm);

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("compInspectionError")));

        Assert.assertTrue(errorMsg.isDisplayed());
    }

    @Test
    public void TC_DS_06_XacNhanDenSachThatBaiDoLoiHeThong() throws Exception {
        String targetMaHoSo = "HS-002";

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick,'tab-4')]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Danh sách hồ sơ chờ xác nhận đền sách')]")
        ));

        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='tab-4']//table/tbody")
                )
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));
        WebElement selectedRow = null;

        for (WebElement row : rows) {
            if (row.getText().trim().isEmpty()) continue;
            String maHoSo = row.findElement(By.xpath("./td[1]")).getText().trim();
            if (maHoSo.equalsIgnoreCase(targetMaHoSo)) {
                selectedRow = row;
                break;
            }
        }
        Assert.assertNotNull(selectedRow);

        String trangThaiBanDau = selectedRow.findElement(By.xpath("./td[6]")).getText().trim();

        WebElement btnMoPopup = selectedRow.findElement(By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btnMoPopup);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnMoPopup);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-compensate-confirm")));

        WebElement radioDatYeuCau = driver.findElement(By.xpath("//input[@name='compInspectionResult' and @value='pass']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioDatYeuCau);

        WebElement txtMaSach = driver.findElement(By.id("compInspectionNote"));
        txtMaSach.clear();
        txtMaSach.sendKeys("TEST-SYSTEM-ERROR-001");

        WebElement btnXacNhan = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Xác nhận đền sách thất bại')]")
        ));
        Assert.assertTrue(errorMsg.isDisplayed());

        WebElement btnHuy = driver.findElement(By.xpath("//div[@id='popup-compensate-confirm']//button[contains(@onclick,'closeAllPopups') and contains(.,'Hủy')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHuy);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-compensate-confirm")));

        Thread.sleep(1000);

        WebElement rowAfter = driver.findElement(By.xpath("//div[@id='tab-4']//table/tbody//td[text()='" + targetMaHoSo + "']/.."));
        String trangThaiSauLoi = rowAfter.findElement(By.xpath("./td[6]")).getText().trim();

        Assert.assertEquals(trangThaiSauLoi, trangThaiBanDau);
        Assert.assertTrue(rowAfter.isDisplayed());
    }

    @Test
    public void TC_DS_08_KhongTaoTrungXacNhanDenSachKhiThaoTacLap() throws Exception {
        String targetMaHoSo = "HS-002";
        String maSachMoi = "MS0001-006";

        WebElement menuTraSach = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Trả sách')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuTraSach);

        WebElement tabDenSach = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'Xác nhận đền sách')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        WebElement selectedRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[@data-record-id='" + targetMaHoSo + "']")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", selectedRow);

        WebElement btnTrigger = selectedRow.findElement(By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnTrigger);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-compensate-confirm")));

        WebElement radioDatYeuCau = driver.findElement(By.xpath("//input[@value='pass']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioDatYeuCau);

        WebElement txtMaSach = driver.findElement(By.id("compInspectionNote"));
        txtMaSach.clear();
        txtMaSach.sendKeys(maSachMoi);

        WebElement btnConfirm = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));

        for (int i = 0; i < 5; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirm);
            } catch (Exception e) {
                break;
            }
        }

        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'thành công')]")
        ));
        Assert.assertTrue(successToast.isDisplayed());

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-compensate-confirm")));

        Thread.sleep(2000);

        List<WebElement> rowsAfter = driver.findElements(
                By.xpath("//tr[@data-record-id='" + targetMaHoSo + "']")
        );

        Assert.assertTrue(rowsAfter.isEmpty());
    }


    private void clickByJS(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
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
    // Thêm test case cho chức năng xác nhận đền sách tại đây
}

