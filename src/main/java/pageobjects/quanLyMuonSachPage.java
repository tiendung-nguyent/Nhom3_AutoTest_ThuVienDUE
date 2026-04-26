package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class quanLyMuonSachPage extends GeneralPage {

    private final By btnThemPhieuMuon = By.id("addBorrowBtn");
    private final By txtTimKiem = By.id("borrowSearchInput");
    private final By btnTimKiem = By.id("borrowSearchBtn");

    private final By txtMaNguoiDung = By.id("addUserId");
    private final By btnThemDongMoi = By.cssSelector("button.btn-add-dashed");
    private final By txtListMaSach = By.cssSelector("input.pm-input.book-code-input");

    private final By btnLuuPhieuMuon = By.cssSelector("button.btn-save");
    private final By lblSuccessMsg = By.cssSelector("div.toast-message");

    private final By suggestionItems = By.cssSelector(".suggestion-item");
    private final By btnEditGiaHan = By.cssSelector("button.btn-edit");

    public WebElement getBtnThemPhieuMuon() {
        return Constant.WEBDRIVER.findElement(btnThemPhieuMuon);
    }

    public WebElement getTxtMaNguoiDung() {
        return Constant.WEBDRIVER.findElement(txtMaNguoiDung);
    }

    public WebElement getBtnThemDongMoi() {
        return Constant.WEBDRIVER.findElement(btnThemDongMoi);
    }


    public void clickThemPhieuMuon() {
        this.scrollToElement(btnThemPhieuMuon); // Kế thừa từ GeneralPage
        this.getBtnThemPhieuMuon().click();
    }


    public void enterMaNguoiDung(String maND) {
        this.scrollToElement(txtMaNguoiDung); // Cuộn tới ô nhập
        this.getTxtMaNguoiDung().clear();
        this.getTxtMaNguoiDung().sendKeys(maND);
    }


    public void clickThemDongMoi() {
        this.scrollToElement(btnThemDongMoi); // Cuộn tới nút thêm dòng
        this.getBtnThemDongMoi().click();
    }


    public void MaSach(String maSach) {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(txtListMaSach);

        if (!inputs.isEmpty()) {

            this.scrollToElement(txtListMaSach);

            WebElement input = inputs.get(0);

            input.clear();
            input.sendKeys(maSach);

        }
    }
    public void enterMaSachAtRow(int index, String maSach) {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(txtListMaSach);
        if (index < inputs.size()) {
            WebElement input = inputs.get(index);

            ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

            input.clear();
            input.sendKeys(maSach);

            try {
                Thread.sleep(1000);

                List<WebElement> suggestions = Constant.WEBDRIVER.findElements(By.cssSelector(".suggestion-item"));

                if (!suggestions.isEmpty()) {
                    // Click vào cái đầu tiên hiện ra (thường là cái đúng nhất)
                    suggestions.get(0).click();
                    System.out.println("Đã click chọn sách từ gợi ý.");
                } else {
                    System.out.println("Cảnh báo: Không thấy danh sách gợi ý hiện ra!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickLuuPhieuMuon() {
        this.scrollToElement(btnLuuPhieuMuon); // Cuộn tới nút Lưu ở cuối modal
        Constant.WEBDRIVER.findElement(btnLuuPhieuMuon).click();
    }


    public String getToastMessage() {
        // Đợi tối đa 5 giây để thông báo (bất kể xanh hay đỏ) xuất hiện
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
            return toast.getText();
        } catch (Exception e) {
            return "Không có thông báo nào xuất hiện!";
        }
    }

    public void clickGiaHanDauTien() {
        System.out.println("Đang tìm nút Gia hạn...");
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnEditGiaHan));

            this.scrollToElement(btnEditGiaHan);

            btn.click();
            System.out.println("Đã click nút Gia hạn thành công.");
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy hoặc không click được nút Gia hạn! " + e.getMessage());
        }
    }
    public void enterNgayGiaHanMoiTuDong() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(4)");
            String ngayMuonText = Constant.WEBDRIVER.findElement(ngayMuonLocator).getText().trim();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, inputFormatter);
            LocalDate ngayGiaHanMoi = ngayMuonDate.plusDays(30);

            String strNgayMoi = ngayGiaHanMoi.format(DateTimeFormatter.ofPattern("MMddyyyy"));

            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
            this.scrollToElement(txtNgayMoi);

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
            js.executeScript("arguments[0].value = '';", input);

            input.click();

            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);

            input.sendKeys(strNgayMoi);

            input.sendKeys(org.openqa.selenium.Keys.TAB);

            System.out.println("Đã ép con trỏ về ô mm và gõ: " + strNgayMoi);

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi nhập ngày: " + e.getMessage());
        }
    }

    public void enterNgayGiaHanMoi(String ngayMoi) {
        By txtNgayMoi = By.id("extLgNewDue");
        this.scrollToElement(txtNgayMoi);
        WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
        input.clear();
        input.sendKeys(ngayMoi);
    }


    public void clickLuuThayDoiGiaHan() {
        try {
            System.out.println("Đang tìm nút Lưu thay đổi trong modal popup-extend-large...");

            By btnLuuGiaHan = By.cssSelector("#popup-extend-large .btn-save");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnLuuGiaHan));

            this.scrollToElement(btnLuuGiaHan);
            btn.click();

            System.out.println("Đã click nút Lưu thay đổi thành công!");

            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("LỖI: Không nhấn được nút Lưu thay đổi: " + e.getMessage());
        }
    }

    public void clickHuyGiaHan() {
        try {
            System.out.println("Đang tìm nút Hủy trong modal popup-extend-large...");

            By btnHuy = By.cssSelector("#popup-extend-large .btn-cancel");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnHuy));

            this.scrollToElement(btnHuy);
            btn.click();

            System.out.println("Đã click nút Hủy thành công!");

            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("LỖI: Không nhấn được nút Hủy: " + e.getMessage());
        }
    }

    public String getNgayDenHanDauTien() {
        By ngayDenHanLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(5)");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(ngayDenHanLocator));
            String ngayDenHan = cell.getText().trim();
            System.out.println("Đã lấy được ngày đến hạn: " + ngayDenHan);
            return ngayDenHan;
        } catch (Exception e) {
            System.out.println("LỖI: Bảng không load được dữ liệu hoặc trống!");
            throw e;
        }
    }


    public boolean isModalGiaHanClosed() {
        try {
            WebElement modal = Constant.WEBDRIVER.findElement(By.id("popup-extend-large"));
            return !modal.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return true;
        }
    }

    public void enterNgayGiaHanNhoHonNgayMuon() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(4)");
            String ngayMuonText = Constant.WEBDRIVER.findElement(ngayMuonLocator).getText().trim();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, inputFormatter);

            LocalDate ngayGiaHanLoi = ngayMuonDate.minusDays(5);

            String strNgayMoi = ngayGiaHanLoi.format(DateTimeFormatter.ofPattern("MMddyyyy"));

            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
            this.scrollToElement(txtNgayMoi);

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
            js.executeScript("arguments[0].value = '';", input);

            input.click();

            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);

            input.sendKeys(strNgayMoi);
            input.sendKeys(org.openqa.selenium.Keys.TAB);

            System.out.println("Đã cố tình gõ ngày lỗi (Nhỏ hơn ngày mượn): " + strNgayMoi);

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi nhập ngày: " + e.getMessage());
        }
    }
}