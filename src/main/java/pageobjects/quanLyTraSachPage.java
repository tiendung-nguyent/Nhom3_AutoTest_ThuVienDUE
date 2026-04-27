package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class quanLyTraSachPage extends GeneralPage {
            private final By inputMucDoHuHong = By.id("mucdo-huhong");
            private final By inputMoTaHuHong = By.id("mota-huhong");
            private final By btnXacNhanHuy = By.id("btn-xacnhan-huy");
    // --- Locators ---
    private final By tabXacNhanTra = By.id("tab-xacnhantra");
    private final By rowMuon = By.cssSelector(".row-muon");
    private final By btnXacNhanTra = By.cssSelector(".btn-xacnhantra");
    private final By popupXacNhanTra = By.id("popup-xacnhantra");
    private final By radioTot = By.id("radio-tot");
    private final By radioHuHong = By.id("radio-huhong");
    private final By btnXacNhan = By.id("btn-xacnhan");
    private final By btnHuy = By.id("btn-huy");
    private final By toastSuccess = By.cssSelector(".toast-success");
    private final By errorTinhTrang = By.cssSelector(".error-tinhtrang");

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
             * Nhập mức độ hư hỏng vào popup xác nhận trả sách
             */
            public void nhapMucDoHuHong(String mucDo) {
                WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
                WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputMucDoHuHong));
                input.clear();
                input.sendKeys(mucDo);
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

    // --- Locators cho Thanh toán phí phạt ---
    private final By tabThanhToanPhiPhat = By.id("tab-thanhtoanphiphat");
    private final By rowPhiPhat = By.cssSelector(".row-phiphat");
    private final By tenNguoiDung = By.cssSelector(".ten-nguoidung");
    private final By tongTien = By.cssSelector(".tong-tien");
    private final By btnXemChiTietPhiPhat = By.cssSelector(".btn-xemchitiet-phiphat");
    private final By popupChiTietPhiPhat = By.id("popup-chitiet-phiphat");
    private final By popupTenNguoiDung = By.id("popup-ten-nguoidung");
    private final By popupTongTien = By.id("popup-tong-tien");
    private final By popupChiTietKhoanPhat = By.cssSelector(".popup-chitiet-khoanphat tr");
    private final By btnXacNhanThanhToan = By.id("btn-xacnhan-thanhtoan");
    private final By radioTienMat = By.id("radio-tienmat");
    private final By errorPhuongThucThanhToan = By.cssSelector(".error-phuongthuc-thanhtoan");
    private final By btnClosePopupChiTietPhiPhat = By.cssSelector(".btn-close-chitiet-phiphat");
    private final By btnHuyThanhToan = By.id("btn-huy-thanhtoan");
    private final By popupXacNhanHuyThanhToan = By.id("popup-xacnhan-huy-thanhtoan");
    private final By btnXacNhanHuyThanhToan = By.id("btn-xacnhan-huy-thanhtoan-xacnhan");
    private final By trangThaiPhat = By.cssSelector(".trangthai-phat");

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
