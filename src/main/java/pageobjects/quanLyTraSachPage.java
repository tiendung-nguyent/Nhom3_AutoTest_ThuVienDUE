package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class quanLyTraSachPage extends GeneralPage {
    // --- Locators cho Xác nhận trả sách ---
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

    // --- Locators cho Thanh toán phí phạt ---
    private final By tabThanhToanPhiPhat = By.xpath("//button[contains(@onclick, 'tab-2')]");
    private final By rowPhiPhat = By.cssSelector("#tab-2 tbody tr");
    private final By tenNguoiDung = By.cssSelector("td:nth-child(2)"); // Cột 2
    private final By tongTien = By.cssSelector("td:nth-child(5) span"); // Cột 5
    private final By btnXemChiTietPhiPhat = By.cssSelector("button[onclick*='openPaymentPopup']");
    private final By popupChiTietPhiPhat = By.id("popup-payment");
    private final By popupTenNguoiDung = By.id("payUserName");
    private final By popupTongTien = By.id("payTotalAmount");
    private final By popupChiTietKhoanPhat = By.cssSelector("#paymentTableBody tr");
    private final By btnXacNhanThanhToan = By.xpath("//button[@onclick='submitPayment();']");
    private final By radioTienMat = By.xpath("//input[@id='radio-cash']/.."); // Click thẻ bọc ngoài do input có pointer-events:none
    private final By errorPhuongThucThanhToan = By.cssSelector(".error-phuongthuc-thanhtoan");
    private final By btnClosePopupChiTietPhiPhat = By.xpath("//div[@id='popup-payment']//button[contains(@onclick, 'none') and contains(text(), '×')]");
    private final By btnHuyThanhToan = By.xpath("//div[@id='popup-payment']//button[text()='Hủy']");

    // Fallback cho các pop-up/button xác nhận hủy thanh toán (Không thấy trong HTML mới, giữ dự phòng)
    private final By popupXacNhanHuyThanhToan = By.id("popup-xacnhan-huy-thanhtoan");
    private final By btnXacNhanHuyThanhToan = By.id("btn-xacnhan-huy-thanhtoan-xacnhan");
    private final By trangThaiPhat = By.cssSelector("td:nth-child(8)"); // Cột 8 ở trong popup

    // --- Elements ---
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

    // --- Actions ---

    /**
     * Chọn mức độ hư hỏng (bây giờ dùng Radio thay vì input text)
     * Truyền vào value của nút Radio tương ứng (ví dụ: "1", "2")
     */
    public void nhapMucDoHuHong(String mucDoId) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        By radioMucDo = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']/..");
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(radioMucDo));
        input.click();
    }

    /**
     * Nhập mô tả hư hỏng vào popup xác nhận trả sách
     */
    public void nhapMoTaHuHong(String moTa) {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputMoTaHuHong));
        input.clear();
        input.sendKeys(moTa);
    }

    /**
     * Xác nhận hủy thao tác xác nhận trả sách
     */
    public void xacNhanHuy() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(btnXacNhanHuy));
        btn.click();
    }

    public void openQuanLyTraSachPage() {
        Constant.WEBDRIVER.get("http://localhost:8000/quanlytrasach");
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(tabXacNhanTra)).click();
    }

    public void clickXacNhanTra(WebElement row) {
        this.scrollToElement(btnXacNhanTra);
        getBtnXacNhanTra(row).click();
    }

    public void chonTinhTrangTot() {
        getRadioTot().click();
    }
    public void chonTinhTrangHuHong() {
        getRadioHuHong().click();
    }
    public void clickXacNhan() {
        getBtnXacNhan().click();
    }
    public void clickHuy() {
        getBtnHuy().click();
    }
    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
        this.scrollToElement(toastSuccess);
        return toast.getText();
    }
    public String getErrorTinhTrangMessage() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorTinhTrang));
        return error.getText();
    }

    // --- Actions cho Thanh toán phí phạt ---
    public void openTabThanhToanPhiPhat() {
        Constant.WEBDRIVER.findElement(tabThanhToanPhiPhat).click();
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
    public void clickXemChiTietPhiPhat(WebElement row) {
        row.findElement(btnXemChiTietPhiPhat).click();
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
    public void clickXacNhanThanhToan() {
        Constant.WEBDRIVER.findElement(btnXacNhanThanhToan).click();
    }
    public void chonPhuongThucThanhToanTienMat() {
        Constant.WEBDRIVER.findElement(radioTienMat).click();
    }
    public String getErrorPhuongThucThanhToan() {
        return Constant.WEBDRIVER.findElement(errorPhuongThucThanhToan).getText();
    }
    public void closePopupChiTietPhiPhat() {
        Constant.WEBDRIVER.findElement(btnClosePopupChiTietPhiPhat).click();
    }
    public void clickHuyThanhToan() {
        Constant.WEBDRIVER.findElement(btnHuyThanhToan).click();
    }
    public WebElement getPopupXacNhanHuyThanhToan() {
        return Constant.WEBDRIVER.findElement(popupXacNhanHuyThanhToan);
    }
    public void xacNhanHuyThanhToan() {
        Constant.WEBDRIVER.findElement(btnXacNhanHuyThanhToan).click();
    }
    public By getSelectorTrangThaiPhat() {
        return trangThaiPhat;
    }
    public boolean isXacNhanThanhToanEnabled() {
        return Constant.WEBDRIVER.findElement(btnXacNhanThanhToan).isEnabled();
    }
}