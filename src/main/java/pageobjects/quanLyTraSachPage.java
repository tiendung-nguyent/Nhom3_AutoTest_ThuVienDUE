package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object cho phần Quản lý trả sách.
 * Đảm nhiệm các thao tác xác nhận trả sách và thanh toán phí phạt.
 */
public class quanLyTraSachPage extends GeneralPage {

    // =========================
    // Locators - Xác nhận trả sách
    // =========================

    private final By tabXacNhanTra = By.xpath("//button[contains(@onclick, 'tab-1')]");
    private final By rowMuon = By.cssSelector(".return-record");
    private final By btnXacNhanTra = By.cssSelector(".return-confirm-trigger");

    private final By popupXacNhanTra = By.id("popup-return-confirm");
    private final By radioTot = By.cssSelector("input[name='returnCondition'][value='good']");
    private final By radioHuHong = By.cssSelector("input[name='returnCondition'][value='damaged']");
    private final By inputMoTaHuHong = By.id("damageDescription");

    private final By btnXacNhan = By.xpath("//button[@onclick='submitReturnConfirm()']");
    private final By btnHuy = By.xpath("//button[@onclick='requestCloseReturnPopup()']");
    private final By btnXacNhanHuy = By.xpath("//button[@onclick='confirmCancelReturnPopup()']");

    private final By toastSuccess = By.cssSelector(".toast-success");
    private final By errorTinhTrang = By.id("returnConditionError");

    // =========================
    // Locators - Thanh toán phí phạt
    // =========================

    private final By tabThanhToanPhiPhat = By.xpath("//button[contains(@onclick, 'tab-2')]");
    private final By rowPhiPhat = By.cssSelector("#tab-2 tbody tr");

    /*
     * Bảng Thanh toán phí phạt hiện tại:
     * Cột 1: Mã phạt
     * Cột 2: Người dùng
     * Cột 3: Mã người dùng
     * Cột 4: Tổng tiền phạt chưa thanh toán
     * Cột 5: Nút Thanh toán nếu còn nợ
     */
    private final By tenNguoiDung = By.cssSelector("td:nth-child(2)");
    private final By tongTien = By.cssSelector("td:nth-child(4)");

    private final By popupChiTietPhiPhat = By.id("popup-payment");
    private final By popupTenNguoiDung = By.id("payUserName");
    private final By popupTongTien = By.id("payTotalAmount");
    private final By popupChiTietKhoanPhat = By.cssSelector("#paymentTableBody tr");

    private final By btnXacNhanThanhToan = By.xpath("//button[@onclick='submitPayment();']");
    private final By radioTienMat = By.xpath("//input[@id='radio-cash']/..");
    private final By errorPhuongThucThanhToan = By.cssSelector(".error-phuongthuc-thanhtoan");

    private final By btnClosePopupChiTietPhiPhat =
            By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') or contains(text(), 'Đóng')]");

    private final By btnHuyThanhToan =
            By.xpath("//div[@id='popup-payment']//button[normalize-space()='Hủy']");

    private final By popupXacNhanHuyThanhToan = By.id("popup-xacnhan-huy-thanhtoan");
    private final By btnXacNhanHuyThanhToan = By.id("btn-xacnhan-huy-thanhtoan-xacnhan");
    private final By trangThaiPhat = By.cssSelector("td:nth-child(8)");

    // =========================
    // Elements - Xác nhận trả sách
    // =========================

    public WebElement getTabXacNhanTra() {
        return Constant.WEBDRIVER.findElement(tabXacNhanTra);
    }

    public List<WebElement> getRowsMuon() {
        return Constant.WEBDRIVER.findElements(rowMuon);
    }

    public WebElement getBtnXacNhanTra(WebElement row) {
        return row.findElement(btnXacNhanTra);
    }

    public WebElement getPopupXacNhanTra() {
        return Constant.WEBDRIVER.findElement(popupXacNhanTra);
    }

    public WebElement getRadioTot() {
        return Constant.WEBDRIVER.findElement(radioTot);
    }

    public WebElement getRadioHuHong() {
        return Constant.WEBDRIVER.findElement(radioHuHong);
    }

    public WebElement getBtnXacNhan() {
        return Constant.WEBDRIVER.findElement(btnXacNhan);
    }

    public WebElement getBtnHuy() {
        return Constant.WEBDRIVER.findElement(btnHuy);
    }

    public WebElement getToastSuccess() {
        return Constant.WEBDRIVER.findElement(toastSuccess);
    }

    public WebElement getErrorTinhTrang() {
        return Constant.WEBDRIVER.findElement(errorTinhTrang);
    }

    // =========================
    // Actions - Xác nhận trả sách
    // =========================

    public void clickXacNhanTra(WebElement row) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                row.findElement(btnXacNhanTra)
        ));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", button);
        }
    }

    public void chonTinhTrangTot() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioTot));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", radio);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(radioTot)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", radio);
        }

        wait.until(driver -> radio.isSelected());
    }

    public void chonTinhTrangHuHong() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioHuHong));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", radio);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(radioHuHong)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", radio);
        }

        wait.until(driver -> radio.isSelected());

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//input[@name='damageLevel']")
        ));
    }

    /**
     * Chọn mức độ hư hỏng.
     *
     * @param mucDoId Giá trị: "1" = Nhẹ, "2" = Vừa, "3" = Nặng
     */
    public void nhapMucDoHuHong(String mucDoId) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        By radioInput = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']");
        By radioLabel = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']/ancestor::label[1]");

        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(radioInput));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

        try {
            WebElement label = wait.until(ExpectedConditions.elementToBeClickable(radioLabel));
            label.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", input);
        }

        wait.until(driver -> input.isSelected());

        if (!input.isSelected()) {
            throw new AssertionError("Không chọn được mức độ hư hỏng có value = " + mucDoId);
        }
    }

    public void nhapMoTaHuHong(String moTa) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputMoTaHuHong));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

        input.clear();
        input.sendKeys(moTa);
    }

    public void clickXacNhan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhan));
        button.click();
    }

    public void clickHuy() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuy));
        button.click();
    }

    public void xacNhanHuy() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuy));
        button.click();
    }

    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
        return toast.getText().trim();
    }

    public String getErrorTinhTrangMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorTinhTrang));
        return error.getText().trim();
    }

    // =========================
    // Elements / Actions - Thanh toán phí phạt
    // =========================

    public WebElement getTabThanhToanPhiPhat() {
        return Constant.WEBDRIVER.findElement(tabThanhToanPhiPhat);
    }

    public void openTabThanhToanPhiPhat() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabThanhToanPhiPhat));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", tab);

        tab.click();

        wait.until(driver ->
                Constant.WEBDRIVER.getPageSource().contains("Thanh toán phí phạt")
                        || !Constant.WEBDRIVER.findElements(rowPhiPhat).isEmpty()
        );
    }

    public List<WebElement> getRowsPhiPhat() {
        return Constant.WEBDRIVER.findElements(rowPhiPhat);
    }

    public By getSelectorTongTien() {
        return tongTien;
    }

    public By getSelectorTenNguoiDung() {
        return tenNguoiDung;
    }

    public By getSelectorTrangThaiPhat() {
        return trangThaiPhat;
    }

    public WebElement getPopupChiTietPhiPhat() {
        return Constant.WEBDRIVER.findElement(popupChiTietPhiPhat);
    }

    public WebElement getPopupTenNguoiDung() {
        return Constant.WEBDRIVER.findElement(popupTenNguoiDung);
    }

    public WebElement getPopupTongTien() {
        return Constant.WEBDRIVER.findElement(popupTongTien);
    }

    public List<WebElement> getPopupChiTietKhoanPhat() {
        return Constant.WEBDRIVER.findElements(popupChiTietKhoanPhat);
    }

    public List<WebElement> getPopupChiTietKhoanPhatWithWait() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));
        wait.until(driver -> !Constant.WEBDRIVER.findElements(popupChiTietKhoanPhat).isEmpty());

        return Constant.WEBDRIVER.findElements(popupChiTietKhoanPhat);
    }

    /**
     * Mở popup chi tiết/thanh toán phí phạt.
     */
    public void clickXemChiTietPhiPhat(WebElement row) {
        clickXemChiTietPhiPhatWithWait(row);
    }

    /**
     * Nếu dòng còn nợ có nút Thanh toán thì click nút Thanh toán.
     * Nếu dòng đã thanh toán hết không có nút thì click trực tiếp vào dòng để xem chi tiết.
     */
    public void clickXemChiTietPhiPhatWithWait(WebElement row) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", row);

        try {
            List<WebElement> buttons = row.findElements(By.tagName("button"));

            if (!buttons.isEmpty()) {
                WebElement button = buttons.get(buttons.size() - 1);

                try {
                    wait.until(ExpectedConditions.elementToBeClickable(button)).click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", button);
                }
            } else {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(row)).click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", row);
                }
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));

        } catch (Exception e) {
            throw new AssertionError(
                    "Không mở được popup chi tiết/thanh toán phí phạt. Nội dung dòng: " + row.getText(),
                    e
            );
        }
    }

    public void closePopupChiTietPhiPhat() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnClosePopupChiTietPhiPhat));
        button.click();
    }

    public void clickXacNhanThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        // Try several strategies to find the confirm payment button (id, onclick, or visible text)
        By[] candidates = new By[]{
                By.id("btnConfirmPayment"),
                By.xpath("//button[contains(@onclick,'submitPayment')]") ,
                By.xpath("//div[@id='popup-payment']//button[normalize-space()='Xác nhận']"),
                By.xpath("//button[contains(normalize-space(.),'Xác nhận')]")
        };

        Exception lastEx = null;
        for (By candidate : candidates) {
            try {
                List<WebElement> els = Constant.WEBDRIVER.findElements(candidate);
                if (els.isEmpty()) continue;

                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(candidate));
                ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
                try {
                    button.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].click();", button);
                }

                // Wait for either success toast OR popup closed OR total updated to 0
                boolean outcome = wait.until(driver -> {
                    try {
                        // toast
                        List<WebElement> toasts = driver.findElements(toastSuccess);
                        if (!toasts.isEmpty() && toasts.get(0).isDisplayed()) return true;
                    } catch (Exception ignored) {}

                    try {
                        List<WebElement> popups = driver.findElements(popupChiTietPhiPhat);
                        if (popups.isEmpty()) return true; // closed
                        try {
                            if (!popups.get(0).isDisplayed()) return true;
                        } catch (Exception ignored) {}
                    } catch (Exception ignored) {}

                    try {
                        WebElement totalEl = driver.findElement(popupTongTien);
                        String txt = totalEl.getText();
                        if (txt != null && txt.trim().matches("^\\D*0\\D*$")) return true;
                    } catch (Exception ignored) {}

                    return false;
                });

                if (outcome) return;

            } catch (Exception e) {
                lastEx = e;
            }
        }

        // If none succeeded, throw an informative error
        throw new AssertionError("Không tìm thấy hoặc không thể click nút Xác nhận thanh toán", lastEx);
    }

    /**
     * Chọn phương thức thanh toán Tiền mặt (radio theo name/value) một cách an toàn.
     */
    public void chonPhuongThucThanhToanTienMat() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-payment")));
        By radioTienMat = By.cssSelector("input[type='radio'][name='paymentMethodNew'][value='cash']");
        List<WebElement> radios = Constant.WEBDRIVER.findElements(radioTienMat);
        if (radios.isEmpty()) {
            WebElement popup = Constant.WEBDRIVER.findElement(By.id("popup-payment"));
            System.out.println("DEBUG HTML popup-payment: " + popup.getAttribute("innerHTML"));
            throw new AssertionError("Không tìm thấy radio button Tiền mặt (name=paymentMethodNew, value=cash) trong popup!");
        }
        WebElement input = radios.get(0);
        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].scrollIntoView({block: 'center'});", input);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(input)).click();
        } catch (Exception e) {
            try {
                // fallback: click the label if input click fails
                WebElement label = input.findElement(By.xpath("ancestor::label[1]"));
                wait.until(ExpectedConditions.elementToBeClickable(label)).click();
            } catch (Exception ex) {
                ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].click();", input);
            }
        }
        // small wait for UI to update selection
        wait.until(driver -> {
            try {
                return input.isSelected();
            } catch (Exception e) {
                return false;
            }
        });
        if (!input.isSelected()) {
            throw new AssertionError("Không chọn được phương thức thanh toán Tiền mặt!");
        }
    }

    public String getErrorPhuongThucThanhToan() {
        return Constant.WEBDRIVER.findElement(errorPhuongThucThanhToan).getText().trim();
    }

    public void clickHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuyThanhToan));
        button.click();
    }

    public WebElement getPopupXacNhanHuyThanhToan() {
        return Constant.WEBDRIVER.findElement(popupXacNhanHuyThanhToan);
    }

    public void xacNhanHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuyThanhToan));
        button.click();
    }

    public boolean isXacNhanThanhToanEnabled() {
        try {
            return Constant.WEBDRIVER.findElement(btnXacNhanThanhToan).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra radio "Tiền mặt" đã được chọn chưa.
     */
    public boolean isPhuongThucTienMatSelected() {
        try {
            WebElement input = Constant.WEBDRIVER.findElement(By.cssSelector("input[type='radio'][name='paymentMethodNew'][value='cash']"));
            return input.isSelected();
        } catch (Exception e) {
            return false;
        }
    }
}
