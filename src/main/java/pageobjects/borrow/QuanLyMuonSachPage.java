package pageobjects.borrow;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.GeneralPage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyMuonSachPage extends GeneralPage {

    private final By btnThemPhieuMuon = By.id("addBorrowBtn");
    private final By txtSearch = By.id("borrowSearchInput");
    private final By btnSearch = By.id("borrowSearchBtn");
    private final By txtMaNguoiDung = By.id("addUserId");
    private final By btnThemDongMoi = By.cssSelector("button.btn-add-dashed");
    private final By txtListMaSach = By.cssSelector("input.pm-input.book-code-input");
    private final By btnLuuPhieuMuon = By.cssSelector("button.btn-save");
    private final By lblSuccessMsg = By.cssSelector("div.toast-message");
    private final By btnEditGiaHan = By.cssSelector("button.btn-edit");

    public void clickThemPhieuMuon() {
        safeClick(btnThemPhieuMuon);
    }

    public void enterMaNguoiDung(String maND) {
        WebElement input = driver.findElement(txtMaNguoiDung);
        scrollToElement(input);
        input.clear();
        input.sendKeys(maND);
    }

    public void clickThemDongMoi() {
        safeClick(btnThemDongMoi);
    }

    public void enterNgayMuonTrongQuaKhu() {
        try {
            LocalDate ngayTrongQuaKhu = LocalDate.now().minusMonths(3);
            String strNgayMuon = ngayTrongQuaKhu.format(DateTimeFormatter.ofPattern("MMddyyyy"));
            By txtNgayMuon = By.id("addBorrowDate");
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtNgayMuon));
            scrollToElement(input);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", input);
            input.click();
            input.sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ARROW_LEFT);
            input.sendKeys(strNgayMuon, Keys.TAB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enterMaSachAtRow(int index, String maSach) {
        List<WebElement> inputs = driver.findElements(txtListMaSach);
        if (index < inputs.size()) {
            WebElement input = inputs.get(index);
            scrollToElement(input);
            input.clear();
            input.sendKeys(maSach);
            try {
                Thread.sleep(1000);
                List<WebElement> suggestions = driver.findElements(By.cssSelector(".suggestion-item"));
                if (!suggestions.isEmpty()) {
                    suggestions.get(0).click();
                }
            } catch (InterruptedException ignored) {}
        }
    }

    public void clickLuuPhieuMuon() {
        safeClick(btnLuuPhieuMuon);
    }

    public String getToastMessage() {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
            return toast.getText();
        } catch (Exception e) {
            return "Không có thông báo nào xuất hiện!";
        }
    }

    public void clickGiaHanDauTien() {
        safeClick(btnEditGiaHan);
    }

    public void enterNgayGiaHanMoiTuDong() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(4)");
            String ngayMuonText = driver.findElement(ngayMuonLocator).getText().trim();
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String strNgayMoi = ngayMuonDate.plusDays(30).format(DateTimeFormatter.ofPattern("MMddyyyy"));
            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = driver.findElement(txtNgayMoi);
            scrollToElement(input);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", input);
            input.click();
            input.sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ARROW_LEFT);
            input.sendKeys(strNgayMoi, Keys.TAB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickLuuThayDoiGiaHan() {
        By btnLuuGiaHan = By.cssSelector("#popup-extend-large .btn-save");
        safeClick(btnLuuGiaHan);
    }

    public void clickHuyGiaHan() {
        By btnHuy = By.cssSelector("#popup-extend-large .btn-cancel");
        safeClick(btnHuy);
    }

    public String getNgayDenHanDauTien() {
        By ngayDenHanLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(5)");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ngayDenHanLocator)).getText().trim();
    }

    public boolean isModalGiaHanClosed() {
        try {
            return !driver.findElement(By.id("popup-extend-large")).isDisplayed();
        } catch (Exception e) {
            return true;
        }
    }

    public void clickDongYPopupXoa() {
        By btnDongYLocator = By.cssSelector("#popup-delete-confirm .btn-danger");
        safeClick(btnDongYLocator);
    }

    public void clickHuyPopupXoa() {
        By btnHuyLocator = By.cssSelector("#popup-delete-confirm .btn-cancel");
        safeClick(btnHuyLocator);
    }

    public boolean isPopupXoaClosed() {
        try {
            return !driver.findElement(By.id("popup-delete-confirm")).isDisplayed();
        } catch (Exception e) {
            return true;
        }
    }

    public String getNoiDungPopupXoa() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#popup-delete-confirm .del-title"))).getText().trim();
    }

    public boolean isPhieuMuonTonTai(String maPhieu) {
        return !driver.findElements(By.cssSelector("#borrowTableBody tr[data-id='" + maPhieu + "']")).isEmpty();
    }

    public String clickXoaPhieuMuonTheoTrangThai(String trangThai) {
        By rowLocator = By.xpath("//tbody[@id='borrowTableBody']//tr[td[6]//span[contains(normalize-space(.), '" + trangThai + "')]]");
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(rowLocator));
        String maPhieu = row.getAttribute("data-id");
        safeClick(row.findElement(By.cssSelector(".btn-del")));
        return maPhieu;
    }

    public String clickXoaPhieuMuonDauTien() {
        WebElement firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#borrowTableBody tr:first-child")));
        String maPhieu = firstRow.getAttribute("data-id");
        safeClick(firstRow.findElement(By.cssSelector(".btn-del")));
        return maPhieu;
    }

    public void nhapTuKhoaTimKiem(String tuKhoa) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtSearch));
        input.clear();
        input.sendKeys(tuKhoa);
    }

    public void clickNutTimKiem() {
        safeClick(btnSearch);
    }

    public void clearOInputSearch() {
        WebElement input = driver.findElement(txtSearch);
        input.clear();
        input.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
    }

    public String getMaPhieuMuonDauTien() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#borrowTableBody tr:first-child td:nth-child(1)"))).getText().trim();
    }

    public String getMaNguoiDungDauTien() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#borrowTableBody tr:first-child td:nth-child(2)"))).getText().trim();
    }

    public int getSoLuongDongHienTai() {
        return driver.findElements(By.cssSelector("#borrowTableBody tr")).size();
    }

    public boolean checkTatCaDongKhopMaNguoiDung(String maMongMuon) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#borrowTableBody tr"));
        if (rows.isEmpty()) return false;
        for (WebElement row : rows) {
            String maThucTe = row.findElement(By.xpath("./td[2]")).getText().trim();
            if (!maThucTe.equals(maMongMuon)) return false;
        }
        return true;
    }

    public void enterNgayGiaHanNhoHonNgayMuon() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(4)");
            String ngayMuonText = driver.findElement(ngayMuonLocator).getText().trim();
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String strNgayMoi = ngayMuonDate.minusDays(5).format(DateTimeFormatter.ofPattern("MMddyyyy"));
            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = driver.findElement(txtNgayMoi);
            scrollToElement(input);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", input);
            input.click();
            input.sendKeys(Keys.ARROW_LEFT, Keys.ARROW_LEFT, Keys.ARROW_LEFT);
            input.sendKeys(strNgayMoi, Keys.TAB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
