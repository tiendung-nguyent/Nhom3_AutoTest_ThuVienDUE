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
     * Bảng Thanh toán phí phạt:
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

    /*
     * Nút xác nhận thanh toán.
     * Không chờ success trong Page Object, vì TC03 cần click nút này khi chưa chọn PTTT
     * và hệ thống chỉ hiển thị lỗi, popup vẫn mở.
     */
    private final By btnXacNhanThanhToan = By.xpath(
            "//div[@id='popup-payment']//button[" +
                    "contains(normalize-space(), 'Xác nhận thanh toán') " +
                    "or contains(normalize-space(), 'Xác nhận') " +
                    "or contains(@onclick, 'submitPayment')" +
                    "]"
    );

    private final By errorPhuongThucThanhToan = By.cssSelector(".error-phuongthuc-thanhtoan");

    private final By btnClosePopupChiTietPhiPhat =
            By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') or contains(normalize-space(), 'Đóng')]");

    private final By btnHuyThanhToan =
            By.xpath("//div[@id='popup-payment']//button[normalize-space()='Hủy']");

    private final By popupXacNhanHuyThanhToan = By.id("popup-xacnhan-huy-thanhtoan");

    private final By btnXacNhanHuyThanhToan = By.xpath(
            "//div[@id='popup-xacnhan-huy-thanhtoan']//button[" +
                    "contains(normalize-space(), 'Thoát thanh toán') " +
                    "or contains(normalize-space(), 'Thoát') " +
                    "or contains(normalize-space(), 'Hủy thanh toán') " +
                    "or contains(normalize-space(), 'Xác nhận')" +
                    "]"
    );

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
     * @param mucDoId Giá trị tương ứng value của radio mức độ hư hỏng.
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

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", button);
        }
    }

    public void clickHuy() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuy));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", button);
        }
    }

    public void xacNhanHuy() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuy));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", button);
        }
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

        try {
            tab.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", tab);
        }

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
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", row);

        try {
            List<WebElement> buttons = row.findElements(By.tagName("button"));

            if (!buttons.isEmpty()) {
                WebElement button = buttons.get(buttons.size() - 1);

                try {
                    wait.until(ExpectedConditions.elementToBeClickable(button)).click();
                } catch (Exception e) {
                    js.executeScript("arguments[0].click();", button);
                }
            } else {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(row)).click();
                } catch (Exception e) {
                    js.executeScript("arguments[0].click();", row);
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
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnClosePopupChiTietPhiPhat));

        try {
            button.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    /**
     * Chỉ thực hiện click nút Xác nhận thanh toán.
     * Không chờ success/popup đóng ở đây, vì TC03 cần click khi chưa chọn phương thức
     * và hệ thống phải giữ popup mở để hiển thị lỗi.
     */
    public void clickXacNhanThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));

            List<WebElement> buttons = Constant.WEBDRIVER.findElements(btnXacNhanThanhToan);

            if (buttons.isEmpty()) {
                throw new AssertionError(
                        "Không tìm thấy nút Xác nhận thanh toán trong popup. Nội dung popup: "
                                + Constant.WEBDRIVER.findElement(popupChiTietPhiPhat).getText()
                );
            }

            WebElement button = null;

            for (WebElement item : buttons) {
                try {
                    String text = item.getText() == null ? "" : item.getText().trim();

                    if (item.isDisplayed()
                            && item.isEnabled()
                            && !text.contains("Hủy")
                            && !text.contains("Thoát")
                            && !text.contains("Tiếp tục")) {
                        button = item;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }

            if (button == null) {
                button = buttons.get(0);
            }

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", button);
            }

        } catch (Exception e) {
            throw new AssertionError("Không tìm thấy hoặc không thể click nút Xác nhận thanh toán", e);
        }
    }

    // =========================
    // Chọn phương thức thanh toán
    // =========================

    private WebElement findRadioTienMatInput(WebDriverWait wait) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));

        By[] candidates = new By[]{
                By.cssSelector("#popup-payment input[type='radio'][name='paymentMethodNew'][value='cash']"),
                By.cssSelector("#popup-payment input[type='radio'][value='cash']"),
                By.xpath("//div[@id='popup-payment']//label[contains(normalize-space(.), 'Tiền mặt')]//input[@type='radio']"),
                By.xpath("//div[@id='popup-payment']//input[@type='radio' and (contains(@id, 'cash') or contains(@name, 'cash'))]"),
                By.xpath("//div[@id='popup-payment']//input[@type='radio' and (contains(@value, 'tien') or contains(@name, 'tien'))]"),
                By.xpath("//div[@id='popup-payment']//input[@type='radio']")
        };

        for (By locator : candidates) {
            List<WebElement> elements = Constant.WEBDRIVER.findElements(locator);

            if (!elements.isEmpty()) {
                return elements.get(0);
            }
        }

        String popupHtml = "";

        try {
            popupHtml = Constant.WEBDRIVER.findElement(popupChiTietPhiPhat).getAttribute("innerHTML");
        } catch (Exception ignored) {
        }

        throw new AssertionError(
                "Không tìm thấy radio phương thức thanh toán Tiền mặt trong popup. HTML popup: " + popupHtml
        );
    }

    private boolean isRadioChecked(WebElement radio) {
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        Object checked = js.executeScript(
                "return arguments[0].checked === true;",
                radio
        );

        return Boolean.TRUE.equals(checked);
    }

    public void chonPhuongThucThanhToanTienMat() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        WebElement radio = findRadioTienMatInput(wait);

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", radio);

        try {
            WebElement label = radio.findElement(By.xpath("./ancestor::label[1]"));
            wait.until(ExpectedConditions.elementToBeClickable(label)).click();
        } catch (Exception e1) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(radio)).click();
            } catch (Exception e2) {
                js.executeScript("arguments[0].click();", radio);
            }
        }

        if (!isRadioChecked(radio)) {
            js.executeScript(
                    "arguments[0].checked = true;" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    radio
            );
        }

        wait.until(driver -> isRadioChecked(radio));

        if (!isRadioChecked(radio)) {
            throw new AssertionError("Không chọn được phương thức thanh toán Tiền mặt.");
        }
    }

    public boolean isPhuongThucTienMatSelected() {
        try {
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement radio = findRadioTienMatInput(wait);

            return isRadioChecked(radio);
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorPhuongThucThanhToan() {
        return Constant.WEBDRIVER.findElement(errorPhuongThucThanhToan).getText().trim();
    }

    public void clickHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuyThanhToan));

        try {
            button.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    public WebElement getPopupXacNhanHuyThanhToan() {
        return Constant.WEBDRIVER.findElement(popupXacNhanHuyThanhToan);
    }

    public void xacNhanHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuyThanhToan));

        try {
            button.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }
    }

    public boolean isXacNhanThanhToanEnabled() {
        try {
            List<WebElement> buttons = Constant.WEBDRIVER.findElements(btnXacNhanThanhToan);

            if (buttons.isEmpty()) {
                return false;
            }

            for (WebElement button : buttons) {
                try {
                    String text = button.getText() == null ? "" : button.getText().trim();

                    if (button.isDisplayed()
                            && button.isEnabled()
                            && !text.contains("Hủy")
                            && !text.contains("Thoát")
                            && !text.contains("Tiếp tục")) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}