package testcases.returnbook.paymentfine;

import base.BaseTest;
import core.TextUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.layout.SidebarPage;
import pageobjects.returnbook.PaymentFineTab;
import pageobjects.returnbook.ReturnBookPage;

import java.time.Duration;
import java.util.List;

public class PaymentFineTest extends BaseTest {

    private PaymentFineTab paymentFineTab;
    private static String paidUserNameAfterPayment;

    @BeforeMethod
    public void setUpTest() {
        login();
        openThanhToanPhiPhatTab();
    }

    private void openThanhToanPhiPhatTab() {
        SidebarPage sidebarPage = new SidebarPage();
        sidebarPage.gotoReturn();
        ReturnBookPage returnBookPage = new ReturnBookPage();
        paymentFineTab = returnBookPage.openPaymentFineTab();
        waitForPaymentTabLoaded();
    }

    private void refreshThanhToanPhiPhatTab() {
        openThanhToanPhiPhatTab();
    }

    private void openChiTietPhiPhat(WebElement row) {
        paymentFineTab.clickXemChiTietPhiPhatWithWait(row);
    }

    private void truyXuatDanhSachPhiPhatIfExists() {
        // Logic truy xuất nếu có nút
        List<WebElement> buttons = driver.findElements(
                By.xpath("//button[contains(normalize-space(), 'Truy xuất') or contains(normalize-space(), 'Tìm kiếm')]")
        );
        if (!buttons.isEmpty()) {
            WebElement button = buttons.get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);
            try {
                button.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }
        }
    }

    // =========================
    // FINDERS
    // =========================

    private List<WebElement> getFineRows() {
        truyXuatDanhSachPhiPhatIfExists();
        List<WebElement> rows = paymentFineTab.getRowsPhiPhat();
        Assert.assertFalse(rows.isEmpty(), "Không có bản ghi phí phạt nào.");
        return rows;
    }

    private WebElement findUnpaidFineRow() {
        for (WebElement row : getFineRows()) {
            if (TextUtils.parseMoney(getTongTien(row)) > 0) return row;
        }
        throw new AssertionError("Không có người dùng còn nợ phí phạt.");
    }

    private WebElement findPaidFineRow() {
        return getFineRows().stream()
                .filter(row -> TextUtils.parseMoney(getTongTien(row)) == 0)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Không có người dùng đã thanh toán hết phí phạt."));
    }

    private WebElement findMixedStatusFineRow() {
        for (WebElement row : getFineRows()) {
            String ten = getTenNguoiDung(row);
            openChiTietPhiPhat(row);
            boolean hasUnpaid = popupHasFineStatus("Chưa thanh toán");
            boolean hasPaid = popupHasFineStatus("Đã thanh toán");
            paymentFineTab.closePopupChiTietPhiPhat();
            if (hasUnpaid && hasPaid) return findFineRowByTenNguoiDung(ten);
        }
        throw new AssertionError("Không tìm thấy người dùng có cả khoản phạt Chưa và Đã thanh toán.");
    }

    private WebElement findFineRowByTenNguoiDung(String tenNguoiDung) {
        return getFineRows().stream()
                .filter(row -> getTenNguoiDung(row).equals(tenNguoiDung))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Không tìm thấy dòng: " + tenNguoiDung));
    }

    // =========================
    // GETTERS / UTILS
    // =========================

    private String getTenNguoiDung(WebElement row) {
        return TextUtils.normalizeText(row.findElement(paymentFineTab.getSelectorTenNguoiDung()).getText());
    }

    private String getTongTien(WebElement row) {
        return TextUtils.normalizeText(row.findElement(paymentFineTab.getSelectorTongTien()).getText());
    }

    private boolean popupHasFineStatus(String expectedStatus) {
        return paymentFineTab.getPopupChiTietKhoanPhat().stream()
                .anyMatch(row -> TextUtils.normalizeText(row.findElements(By.tagName("td")).get(7).getText()).equals(expectedStatus));
    }

    private void waitForPaymentTabLoaded() {
        wait.until(driver -> {
            String bodyText = TextUtils.normalizeText(driver.findElement(By.tagName("body")).getText());
            return bodyText.contains("Thanh toán phí phạt") || bodyText.contains("phí phạt");
        });
    }

    // =========================
    // TEST CASES
    // =========================

    @Test(priority = 1)
    public void TC_TP_01_hienThiDanhSachPhiPhatNguoiDung() {
        List<WebElement> rows = getFineRows();
        for (WebElement row : rows) {
            Assert.assertFalse(getTenNguoiDung(row).isEmpty());
            Assert.assertTrue(TextUtils.parseMoney(getTongTien(row)) >= 0);
        }
    }

    @Test(priority = 2)
    public void TC_TP_02_moPopupChiTietPhiPhat() {
        WebElement row = findMixedStatusFineRow();
        openChiTietPhiPhat(row);
        Assert.assertEquals(TextUtils.normalizeText(paymentFineTab.getPopupTenNguoiDung().getText()), getTenNguoiDung(row));
        paymentFineTab.closePopupChiTietPhiPhat();
    }

    @Test(priority = 3)
    public void TC_TP_03_loiChuaChonPhuongThucThanhToan() {
        WebElement row = findUnpaidFineRow();
        openChiTietPhiPhat(row);
        paymentFineTab.clickXacNhanThanhToan();
        String bodyText = TextUtils.normalizeText(driver.findElement(By.tagName("body")).getText());
        Assert.assertTrue(bodyText.contains("Vui lòng chọn phương thức thanh toán"));
        paymentFineTab.closePopupChiTietPhiPhat();
    }

    @Test(priority = 4)
    public void TC_TP_04_thanhToanPhiPhatThanhCong() {
        WebElement row = findUnpaidFineRow();
        String ten = getTenNguoiDung(row);
        paidUserNameAfterPayment = ten;
        openChiTietPhiPhat(row);
        paymentFineTab.chonPhuongThucThanhToanTienMat();
        paymentFineTab.clickXacNhanThanhToan();
        Assert.assertTrue(paymentFineTab.getSuccessMessage().contains("thành công"));
        refreshThanhToanPhiPhatTab();
        WebElement rowSau = findFineRowByTenNguoiDung(ten);
        Assert.assertEquals(TextUtils.parseMoney(getTongTien(rowSau)), 0);
    }

    @Test(priority = 5)
    public void TC_TP_05_huyThanhToanPhiPhat() {
        WebElement row = findUnpaidFineRow();
        long tienTruoc = TextUtils.parseMoney(getTongTien(row));
        openChiTietPhiPhat(row);
        paymentFineTab.clickHuyThanhToan();
        paymentFineTab.xacNhanHuyThanhToan();
        refreshThanhToanPhiPhatTab();
        WebElement rowSau = findFineRowByTenNguoiDung(getTenNguoiDung(row));
        Assert.assertEquals(TextUtils.parseMoney(getTongTien(rowSau)), tienTruoc);
    }

    @Test(priority = 6)
    public void TC_TP_06_xemChiTietNguoiDungDaThanhToanHet() {
        WebElement row = (paidUserNameAfterPayment != null) ? findFineRowByTenNguoiDung(paidUserNameAfterPayment) : findPaidFineRow();
        Assert.assertEquals(TextUtils.parseMoney(getTongTien(row)), 0);
        openChiTietPhiPhat(row);
        paymentFineTab.closePopupChiTietPhiPhat();
    }
}
