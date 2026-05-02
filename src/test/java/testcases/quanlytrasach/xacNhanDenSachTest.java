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
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Random;

import static java.lang.Thread.sleep;

public class xacNhanDenSachTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private Random rand = new Random();

    @BeforeMethod
    public void setupLogin(Method method) {


        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        driver = Constant.WEBDRIVER;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        if (!method.getName().equals("TC_DS_05_KiemTraQuyenXacNhanDenSach")) {
            loginAsUser();
        } else {
            try {
                driver.manage().deleteAllCookies();
                driver.get(Constant.THUVIEN_URL);
            } catch (Exception ignored) {}
        }

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
    private boolean isLoggedIn() {
        try {
            return driver != null
                    && !driver.findElements(By.xpath("//aside")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureQuanLyTraSachPage() {
        try {
            boolean isCorrectPage =
                    driver.getCurrentUrl().contains("return")
                            || !driver.findElements(
                            By.xpath("//*[contains(text(),'Quản lý trả sách')]")
                    ).isEmpty();

            if (!isCorrectPage) {
                goToQuanLyTraSach();
            } else {
            }

        } catch (Exception e) {
            goToQuanLyTraSach();
        }
    }

    @Test
    public void PA_24() {
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick,'tab-4')]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        // 3. Đợi bảng hồ sơ hiển thị (Dựa trên ID từ image_cd73e0.png)
        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("compensateTableBody"))
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));

        Assert.assertFalse(rows.isEmpty(), "FAILED: Không có hồ sơ nào hiển thị trong danh sách chờ xác nhận!");


        String firstRowText = rows.get(0).getText().trim();
        Assert.assertFalse(firstRowText.isEmpty(), "FAILED: Hồ sơ hiển thị nhưng không có dữ liệu chữ!");
    }


    @Test
    public void PA_25() {

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick,'tab-4')]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        WebElement tableBody = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("compensateTableBody"))
        );

        List<WebElement> rows = tableBody.findElements(By.xpath("./tr"));
        Assert.assertFalse(rows.isEmpty(), "Không có hồ sơ nào để test!");

        Random rand = new Random();
        WebElement selectedRow = rows.get(rand.nextInt(rows.size()));

        String maHoSo = selectedRow.findElement(By.xpath("./td[1]")).getText().trim();
        String nguoiDen = selectedRow.findElement(By.xpath("./td[2]")).getText().trim();
        String maSach = selectedRow.findElement(By.xpath("./td[3]")).getText().trim();
        String tenSach = selectedRow.findElement(By.xpath("./td[4]")).getText().trim();
        String ngayBaoMat = selectedRow.findElement(By.xpath("./td[5]")).getText().trim();


        WebElement btnMoPopup = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]")
        );
        wait.until(ExpectedConditions.elementToBeClickable(btnMoPopup)).click();

        // 6. ĐỢI POPUP HIỂN THỊ
        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='popup-compensate-confirm' and not(contains(@style,'display: none'))]")
                )
        );

        Assert.assertTrue(popup.isDisplayed(), "Popup xác nhận đền sách không hiển thị!");

        String popupContent = popup.getText();

        Assert.assertTrue(popupContent.contains(maHoSo), "Popup sai Mã hồ sơ! Đợi: " + maHoSo);
        Assert.assertTrue(popupContent.contains(tenSach), "Popup sai Tên sách! Đợi: " + tenSach);
        Assert.assertTrue(popupContent.contains(maSach), "Popup sai Mã sách! Đợi: " + maSach);
        Assert.assertTrue(popupContent.contains(nguoiDen), "Popup sai Người đền! Đợi: " + nguoiDen);
        Assert.assertTrue(popupContent.contains(ngayBaoMat), "Popup sai Ngày báo mất! Đợi: " + ngayBaoMat);

        WebElement btnHuy = popup.findElement(By.xpath(".//button[contains(text(),'Hủy')]"));
        WebElement btnXacNhan = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));

        Assert.assertTrue(btnHuy.isDisplayed(), "Thiếu nút Hủy trên popup!");
        Assert.assertTrue(btnXacNhan.isDisplayed(), "Thiếu nút Xác nhận trên popup!");

    }


    @Test
    public void PA_26() {
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        WebElement tabDenSach = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@onclick,'tab-4')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("compensateTableBody")));
        safeSleep(2000);

        List<WebElement> rowsWithButtons = driver.findElements(
                By.xpath("//tbody[@id='compensateTableBody']//tr[.//span[contains(@class,'compensate-confirm-trigger')]]")
        );

        Assert.assertFalse(rowsWithButtons.isEmpty(), "FAILED: Không tìm thấy hồ sơ có nút Xác nhận!");

        int randomIndex = rand.nextInt(rowsWithButtons.size());
        WebElement selectedRow = rowsWithButtons.get(randomIndex);
        String maHoSoTarget = selectedRow.findElement(By.xpath("./td[1]")).getText().trim();

        WebElement btnOpen = selectedRow.findElement(By.xpath(".//span[contains(@class,'compensate-confirm-trigger')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnOpen);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-compensate-confirm")));

        driver.findElement(By.xpath("//input[@name='compInspectionResult' and @value='pass']")).click();

        WebElement inputNote = driver.findElement(By.id("compInspectionNote"));
        inputNote.clear();
        inputNote.sendKeys("MS-NEW-" + (System.currentTimeMillis() % 100000));

        WebElement btnConfirm = driver.findElement(By.xpath("//button[@onclick='submitCompensateConfirm()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnConfirm);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-compensate-confirm")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tabDenSach);

        safeSleep(2500);
        boolean isDisappeared = false;
        try {

            isDisappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath("//tbody[@id='compensateTableBody']//td[normalize-space()='" + maHoSoTarget + "']")
            ));
        } catch (TimeoutException e) {
            isDisappeared = false;
        }

        Assert.assertTrue(isDisappeared, "LỖI: Hồ sơ " + maHoSoTarget + " vẫn còn tồn tại trong danh sách sau khi xác nhận!");
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
        sleep(500);
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
        sleep(300);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioKhongDat);

        Assert.assertTrue(radioKhongDat.isSelected());

        WebElement btnHuy = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='popup-compensate-confirm']//button[contains(@onclick,'closeAllPopups')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btnHuy);
        sleep(300);
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


    private void loginAsUser() {
        try {
            if (driver.getCurrentUrl().contains("dashboard") ||
                    !driver.findElements(By.xpath("//aside")).isEmpty()) {

                return;
            }
        } catch (Exception ignored) {}

        driver.get(Constant.THUVIEN_URL);

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));

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

    }
    private void safeSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    }



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