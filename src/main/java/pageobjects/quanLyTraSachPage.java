package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

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

    /*
     * Chỉ lấy dòng dữ liệu thật trong bảng Thanh toán phí phạt.
     * Không lấy header, dòng trống, dòng "Không có dữ liệu" hoặc dòng thông báo.
     *
     * Bảng hiện tại:
     * Cột 1: Mã phạt
     * Cột 2: Người dùng
     * Cột 3: Mã người dùng
     * Cột 4: Tổng tiền phạt chưa thanh toán
     * Cột 5: Nút Thanh toán nếu còn nợ
     */
    private final By rowPhiPhat = By.xpath(
            "//div[@id='tab-2']//table//tbody/tr[" +
                    "td and " +
                    "count(td) >= 4 and " +
                    "not(contains(normalize-space(.), 'Không có dữ liệu')) and " +
                    "not(contains(normalize-space(.), 'Không có người dùng')) and " +
                    "not(contains(normalize-space(.), 'không có dữ liệu')) and " +
                    "not(contains(normalize-space(.), 'không có người dùng'))" +
                    "]"
    );

    private final By tenNguoiDung = By.cssSelector("td:nth-child(2)");
    private final By tongTien = By.cssSelector("td:nth-child(4)");

    private final By popupChiTietPhiPhat = By.id("popup-payment");
    private final By popupTenNguoiDung = By.id("payUserName");
    private final By popupTongTien = By.id("payTotalAmount");

    /*
     * Chỉ lấy dòng chi tiết khoản phạt thật trong popup.
     * Popup chi tiết có 8 cột:
     * Mã sách trong kho, Tên sách, Loại phạt, Lý do, Số tiền, Ngày tạo, Mã phiếu mượn, Trạng thái.
     */
    private final By popupChiTietKhoanPhat = By.xpath(
            "//tbody[@id='paymentTableBody']/tr[" +
                    "td and " +
                    "count(td) >= 8 and " +
                    "not(contains(normalize-space(.), 'Không có dữ liệu')) and " +
                    "not(contains(normalize-space(.), 'Không có khoản phạt'))" +
                    "]"
    );

    private final By btnXacNhanThanhToan = By.xpath(
            "//div[@id='popup-payment']//button[" +
                    "contains(@onclick,'submitPayment') or " +
                    "normalize-space()='Xác nhận' or " +
                    "contains(normalize-space(.),'Xác nhận thanh toán')" +
                    "]"
    );

    private final By errorPhuongThucThanhToan = By.cssSelector(".error-phuongthuc-thanhtoan");

    private final By btnClosePopupChiTietPhiPhat = By.xpath(
            "//div[@id='popup-payment']//button[" +
                    "contains(text(), '×') or " +
                    "contains(text(), 'Đóng')" +
                    "]"
    );

    private final By btnHuyThanhToan = By.xpath(
            "//div[@id='popup-payment']//button[normalize-space()='Hủy']"
    );

    private final By popupXacNhanHuyThanhToan = By.id("popup-payment-cancel-confirm");

    private final By btnXacNhanHuyThanhToan = By.xpath(
            "//div[@id='popup-payment-cancel-confirm']//button[contains(normalize-space(.), 'Thoát thanh')]"
    );
    private final By trangThaiPhat = By.cssSelector("td:nth-child(8)");

    // =========================
    // Common helpers
    // =========================

    private WebDriverWait getWait() {
        return new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    private void safeClick(WebElement element) {
        scrollToElement(element);

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", element);
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isValidDataRow(WebElement row, int minCellCount) {
        try {
            if (!row.isDisplayed()) {
                return false;
            }

            List<WebElement> cells = row.findElements(By.tagName("td"));

            if (cells.size() < minCellCount) {
                return false;
            }

            String text = normalizeText(row.getText());

            return !text.isEmpty()
                    && !text.contains("Không có dữ liệu")
                    && !text.contains("Không có người dùng")
                    && !text.contains("Không có khoản phạt")
                    && !text.toLowerCase().contains("không có dữ liệu")
                    && !text.toLowerCase().contains("không có người dùng")
                    && !text.toLowerCase().contains("không có khoản phạt");

        } catch (Exception e) {
            return false;
        }
    }

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
        WebDriverWait wait = getWait();

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                row.findElement(btnXacNhanTra)
        ));

        safeClick(button);
    }

    public void chonTinhTrangTot() {
        WebDriverWait wait = getWait();

        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioTot));

        scrollToElement(radio);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(radioTot)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", radio);
        }

        wait.until(driver -> radio.isSelected());
    }

    public void chonTinhTrangHuHong() {
        WebDriverWait wait = getWait();

        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioHuHong));

        scrollToElement(radio);

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
        WebDriverWait wait = getWait();

        By radioInput = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']");
        By radioLabel = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']/ancestor::label[1]");

        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(radioInput));

        scrollToElement(input);

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
        WebDriverWait wait = getWait();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputMoTaHuHong));

        scrollToElement(input);

        input.clear();
        input.sendKeys(moTa);
    }

    public void clickXacNhan() {
        WebDriverWait wait = getWait();
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhan));
        safeClick(button);
    }

    public void clickHuy() {
        WebDriverWait wait = getWait();
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuy));
        safeClick(button);
    }

    public void xacNhanHuy() {
        WebDriverWait wait = getWait();
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuy));
        safeClick(button);
    }

    public String getSuccessMessage() {
        WebDriverWait wait = getWait();
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
        return toast.getText().trim();
    }

    public String getErrorTinhTrangMessage() {
        WebDriverWait wait = getWait();
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
        WebDriverWait wait = getWait();

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabThanhToanPhiPhat));

        scrollToElement(tab);
        safeClick(tab);

        wait.until(driver -> {
            String bodyText = normalizeText(driver.findElement(By.tagName("body")).getText());

            return bodyText.contains("Thanh toán phí phạt")
                    || bodyText.contains("Danh sách người dùng có khoản phạt")
                    || !getRowsPhiPhat().isEmpty();
        });
    }

    /**
     * Lấy danh sách dòng phí phạt hợp lệ.
     * Hàm này đã lọc:
     * - dòng không hiển thị
     * - dòng không đủ cột
     * - dòng "Không có dữ liệu"
     * - dòng "Không có người dùng..."
     */
    public List<WebElement> getRowsPhiPhat() {
        return Constant.WEBDRIVER.findElements(rowPhiPhat)
                .stream()
                .filter(row -> isValidDataRow(row, 4))
                .collect(Collectors.toList());
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

    /**
     * Lấy danh sách dòng chi tiết khoản phạt trong popup.
     * Chỉ lấy dòng có đủ 8 cột dữ liệu thật.
     */
    public List<WebElement> getPopupChiTietKhoanPhat() {
        return Constant.WEBDRIVER.findElements(popupChiTietKhoanPhat)
                .stream()
                .filter(row -> isValidDataRow(row, 8))
                .collect(Collectors.toList());
    }

    public List<WebElement> getPopupChiTietKhoanPhatWithWait() {
        WebDriverWait wait = getWait();

        wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));

        wait.until(driver -> !getPopupChiTietKhoanPhat().isEmpty());

        return getPopupChiTietKhoanPhat();
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
        WebDriverWait wait = getWait();

        if (!isValidDataRow(row, 4)) {
            throw new AssertionError(
                    "Dòng phí phạt không hợp lệ, không thể mở popup. Nội dung dòng: "
                            + normalizeText(row.getText())
            );
        }

        scrollToElement(row);

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
                    "Không mở được popup chi tiết/thanh toán phí phạt. Nội dung dòng: "
                            + normalizeText(row.getText()),
                    e
            );
        }
    }

    public void closePopupChiTietPhiPhat() {
        WebDriverWait wait = getWait();

        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnClosePopupChiTietPhiPhat));
            safeClick(button);
        } catch (Exception e) {
            List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
                    By.xpath("//div[@id='popup-payment']//button[" +
                            "contains(text(), '×') or " +
                            "contains(text(), 'Đóng') or " +
                            "contains(text(), 'Hủy')" +
                            "]")
            );

            if (!closeButtons.isEmpty()) {
                safeClick(closeButtons.get(0));
            } else {
                throw e;
            }
        }

        wait.until(driver -> {
            List<WebElement> popups = driver.findElements(popupChiTietPhiPhat);

            if (popups.isEmpty()) {
                return true;
            }

            try {
                return !popups.get(0).isDisplayed();
            } catch (Exception ex) {
                return true;
            }
        });
    }

    public void clickXacNhanThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        By[] candidates = new By[]{
                By.id("btnConfirmPayment"),
                By.xpath("//div[@id='popup-payment']//button[contains(@onclick,'submitPayment')]"),
                By.xpath("//div[@id='popup-payment']//button[normalize-space()='Xác nhận']"),
                By.xpath("//div[@id='popup-payment']//button[contains(normalize-space(.),'Xác nhận thanh toán')]"),
                By.xpath("//button[contains(@onclick,'submitPayment')]")
        };

        Exception lastEx = null;

        for (By candidate : candidates) {
            try {
                List<WebElement> buttons = Constant.WEBDRIVER.findElements(candidate);

                if (buttons.isEmpty()) {
                    continue;
                }

                WebElement button = buttons.stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(buttons.get(0));

                ((JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

                wait.until(ExpectedConditions.elementToBeClickable(button));

                try {
                    button.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", button);
                }

                return;

            } catch (Exception e) {
                lastEx = e;
            }
        }

        throw new AssertionError("Không tìm thấy hoặc không thể click nút Xác nhận thanh toán", lastEx);
    }

    /**
     * Chọn phương thức thanh toán Tiền mặt.
     */
    public void chonPhuongThucThanhToanTienMat() {
        WebDriverWait wait = getWait();

        wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));

        By radioInputBy = By.cssSelector("input[type='radio'][name='paymentMethodNew'][value='cash']");

        List<WebElement> radios = Constant.WEBDRIVER.findElements(radioInputBy);

        if (radios.isEmpty()) {
            WebElement popup = Constant.WEBDRIVER.findElement(popupChiTietPhiPhat);
            System.out.println("DEBUG HTML popup-payment: " + popup.getAttribute("innerHTML"));

            throw new AssertionError(
                    "Không tìm thấy radio button Tiền mặt " +
                            "(name=paymentMethodNew, value=cash) trong popup!"
            );
        }

        WebElement input = radios.get(0);

        scrollToElement(input);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(input)).click();
        } catch (Exception e) {
            try {
                WebElement label = input.findElement(By.xpath("ancestor::label[1]"));
                wait.until(ExpectedConditions.elementToBeClickable(label)).click();
            } catch (Exception ex) {
                ((JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].click();", input);
            }
        }

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
        WebDriverWait wait = getWait();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorPhuongThucThanhToan));
        return error.getText().trim();
    }

    public void clickHuyThanhToan() {
        WebDriverWait wait = getWait();
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnHuyThanhToan));
        safeClick(button);
    }

    public WebElement getPopupXacNhanHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(popupXacNhanHuyThanhToan));
    }

    public void xacNhanHuyThanhToan() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(btnXacNhanHuyThanhToan));

        try {
            button.click();
        } catch (Exception e) {
            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click();", button);
        }
    }

    public boolean isXacNhanThanhToanEnabled() {
        try {
            List<WebElement> buttons = Constant.WEBDRIVER.findElements(btnXacNhanThanhToan);

            if (buttons.isEmpty()) {
                return false;
            }

            WebElement button = buttons.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(buttons.get(0));

            return button.isDisplayed() && button.isEnabled();

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra radio "Tiền mặt" đã được chọn chưa.
     */
    public boolean isPhuongThucTienMatSelected() {
        try {
            WebElement input = Constant.WEBDRIVER.findElement(
                    By.cssSelector("input[type='radio'][name='paymentMethodNew'][value='cash']")
            );

            return input.isSelected();

        } catch (Exception e) {
            return false;
        }
    }
}