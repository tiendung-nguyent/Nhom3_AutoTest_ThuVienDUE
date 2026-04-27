package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.SidebarPage;
import pageobjects.quanLyTraSachPage;

import java.time.Duration;
import java.util.List;

public class thanhToanPhiPhatTest {
    private WebDriverWait wait;
    private quanLyTraSachPage traSachPage;

    @BeforeMethod
    public void setupLogin() {
        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }
        wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        HomePage homePage = new HomePage();
        homePage.open();
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        wait.until(ExpectedConditions.urlContains("tong-quan"));
        traSachPage = new quanLyTraSachPage();
        SidebarPage sidebar = new SidebarPage();
        sidebar.gotoReturn();
        traSachPage.openTabThanhToanPhiPhat();
    }

    @AfterClass
    public void tearDown() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }

    // TC_TP_01: Hiển thị đúng danh sách phí phạt của người dùng
    @Test
    public void hienThiDanhSachPhiPhatNguoiDung() {
        List<WebElement> rows = traSachPage.getRowsPhiPhat();
        Assert.assertTrue(!rows.isEmpty(), "Không có bản ghi phí phạt nào!");
        for (WebElement row : rows) {
            String tongTien = row.findElement(traSachPage.getSelectorTongTien()).getText();
            Assert.assertNotNull(row.findElement(traSachPage.getSelectorTenNguoiDung()).getText());
            Assert.assertTrue(tongTien.matches(".*\\d+.*"), "Tổng tiền phải là số");
        }
    }

    // TC_TP_02: Mở popup chi tiết phí phạt và hiển thị đúng thông tin
    @Test
    public void moPopupChiTietPhiPhat() {
        WebElement row = traSachPage.getRowsPhiPhat().get(0);
        String tenNguoiDung = row.findElement(traSachPage.getSelectorTenNguoiDung()).getText();
        String tongTien = row.findElement(traSachPage.getSelectorTongTien()).getText();
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        Assert.assertEquals(traSachPage.getPopupTenNguoiDung().getText(), tenNguoiDung);
        Assert.assertTrue(traSachPage.getPopupTongTien().getText().contains(tongTien));
        List<WebElement> chiTiet = traSachPage.getPopupChiTietKhoanPhat();
        Assert.assertTrue(chiTiet.size() > 0, "Phải có ít nhất 1 khoản phạt hiển thị");
    }

    // TC_TP_03: Báo lỗi khi chưa chọn phương thức thanh toán
    @Test
    public void loiChuaChonPhuongThucThanhToan() {
        WebElement row = traSachPage.getRowsPhiPhat().stream()
            .filter(r -> !r.findElement(traSachPage.getSelectorTongTien()).getText().equals("0đ"))
            .findFirst().orElseThrow(() -> new AssertionError("Không có user còn nợ!"));
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        traSachPage.clickXacNhanThanhToan();
        String error = traSachPage.getErrorPhuongThucThanhToan();
        Assert.assertEquals(error, "Vui lòng chọn phương thức thanh toán.");
    }

    // TC_TP_04: Thanh toán thành công các khoản phạt chưa thanh toán
    @Test
    public void thanhToanPhiPhatThanhCong() {
        WebElement row = traSachPage.getRowsPhiPhat().stream()
            .filter(r -> !r.findElement(traSachPage.getSelectorTongTien()).getText().equals("0đ"))
            .findFirst().orElseThrow(() -> new AssertionError("Không có user còn nợ!"));
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        traSachPage.chonPhuongThucThanhToanTienMat();
        traSachPage.clickXacNhanThanhToan();
        String msg = traSachPage.getSuccessMessage();
        Assert.assertTrue(msg.contains("Xác nhận thanh toán thành công"));
        // Kiểm tra lại trạng thái khoản phạt
        traSachPage.closePopupChiTietPhiPhat();
        String tongTien = row.findElement(traSachPage.getSelectorTongTien()).getText();
        Assert.assertEquals(tongTien, "0đ", "Sau khi thanh toán, tổng tiền phải là 0đ");
    }

    // TC_TP_05: Hủy thao tác thanh toán phí phạt
    @Test
    public void huyThanhToanPhiPhat() {
        WebElement row = traSachPage.getRowsPhiPhat().stream()
            .filter(r -> !r.findElement(traSachPage.getSelectorTongTien()).getText().equals("0đ"))
            .findFirst().orElseThrow(() -> new AssertionError("Không có user còn nợ!"));
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        traSachPage.clickHuyThanhToan();
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanHuyThanhToan()));
        traSachPage.xacNhanHuyThanhToan();
        wait.until(ExpectedConditions.invisibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        // Kiểm tra dữ liệu giữ nguyên
        String tongTien = row.findElement(traSachPage.getSelectorTongTien()).getText();
        Assert.assertNotEquals(tongTien, "0đ", "Sau khi hủy, tổng tiền vẫn giữ nguyên");
    }

    // TC_TP_06: Xem chi tiết người dùng đã thanh toán hết phí phạt
    @Test
    public void xemChiTietNguoiDungDaThanhToanHet() {
        WebElement row = traSachPage.getRowsPhiPhat().stream()
            .filter(r -> r.findElement(traSachPage.getSelectorTongTien()).getText().equals("0đ"))
            .findFirst().orElseThrow(() -> new AssertionError("Không có user đã thanh toán hết!"));
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        List<WebElement> chiTiet = traSachPage.getPopupChiTietKhoanPhat();
        Assert.assertTrue(chiTiet.size() > 0, "Phải có khoản phạt đã thanh toán");
        for (WebElement item : chiTiet) {
            Assert.assertEquals(item.findElement(traSachPage.getSelectorTrangThaiPhat()).getText(), "Đã thanh toán");
        }
        Assert.assertFalse(traSachPage.isXacNhanThanhToanEnabled(), "Nút xác nhận phải bị ẩn/vô hiệu hóa");
    }
}
