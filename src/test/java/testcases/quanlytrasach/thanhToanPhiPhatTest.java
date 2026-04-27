package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.quanLyTraSachPage;

import java.time.Duration;
import java.util.List;

public class thanhToanPhiPhatTest {

    private WebDriverWait wait;
    private quanLyTraSachPage traSachPage;

    private static final String URL_TRA_SACH = "http://127.0.0.1:8000/quan-ly-tra-sach/";
    private static final String MSG_THANH_TOAN_THANH_CONG = "Xác nhận thanh toán thành công";
    private static final String ERR_CHUA_CHON_PHUONG_THUC = "Vui lòng chọn phương thức thanh toán.";

    @BeforeMethod
    public void setUp() {
        initDriver();
        login();
        openThanhToanPhiPhatPage();
    }

    @AfterMethod
    public void tearDown() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }

    // =========================
    // SETUP / NAVIGATION
    // =========================

    private void initDriver() {
        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        traSachPage = new quanLyTraSachPage();
    }

    private void login() {
        HomePage homePage = new HomePage();
        homePage.open();

        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        wait.until(driver ->
                driver.getCurrentUrl().contains("dashboard")
                        || driver.getCurrentUrl().contains("tong-quan")
        );
    }

    private void openThanhToanPhiPhatPage() {
        Constant.WEBDRIVER.get(URL_TRA_SACH);
        traSachPage.openTabThanhToanPhiPhat();
        waitForFineRows();
    }

    private void refreshThanhToanPhiPhatTab() {
        Constant.WEBDRIVER.get(URL_TRA_SACH);
        traSachPage.openTabThanhToanPhiPhat();
        waitForFineRows();
    }

    private void openChiTietPhiPhat(WebElement row) {
        traSachPage.clickXemChiTietPhiPhat(row);
        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
    }

    // =========================
    // FINDERS
    // =========================

    private List<WebElement> getFineRows() {
        List<WebElement> rows = traSachPage.getRowsPhiPhat();

        Assert.assertFalse(
                rows.isEmpty(),
                "Không có bản ghi phí phạt nào. Cần chuẩn bị dữ liệu phí phạt trước khi chạy test."
        );

        return rows;
    }

    private WebElement findUnpaidFineRow() {
        return getFineRows().stream()
                .filter(row -> !normalizeMoney(getTongTien(row)).equals("0"))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không có người dùng còn nợ phí phạt. Cần chuẩn bị dữ liệu chưa thanh toán."
                ));
    }

    private WebElement findPaidFineRow() {
        return getFineRows().stream()
                .filter(row -> normalizeMoney(getTongTien(row)).equals("0"))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không có người dùng đã thanh toán hết phí phạt. Cần chuẩn bị dữ liệu đã thanh toán."
                ));
    }

    private WebElement findFineRowByTenNguoiDung(String tenNguoiDung) {
        return getFineRows().stream()
                .filter(row -> getTenNguoiDung(row).equals(tenNguoiDung))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không tìm thấy dòng phí phạt của người dùng: " + tenNguoiDung
                ));
    }

    // =========================
    // GETTERS / UTILS
    // =========================

    private String getTenNguoiDung(WebElement row) {
        return row.findElement(traSachPage.getSelectorTenNguoiDung()).getText().trim();
    }

    private String getTongTien(WebElement row) {
        return row.findElement(traSachPage.getSelectorTongTien()).getText().trim();
    }

    private String normalizeMoney(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("VNĐ", "")
                .replace("VND", "")
                .replace("đ", "")
                .replace("Đ", "")
                .replace(".", "")
                .replace(",", "")
                .trim();
    }

    private void waitForFineRows() {
        wait.until(driver -> !traSachPage.getRowsPhiPhat().isEmpty());
    }

    // =========================
    // TEST CASES
    // =========================

    /**
     * TC_TP_01: Hiển thị đúng danh sách phí phạt của người dùng
     */
    @Test
    public void TC_TP_01_hienThiDanhSachPhiPhatNguoiDung() {
        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            Assert.assertFalse(getTenNguoiDung(row).isEmpty(), "Tên người dùng không được rỗng");

            Assert.assertTrue(
                    getTongTien(row).matches(".*\\d+.*"),
                    "Tổng tiền phải có số"
            );
        }
    }

    /**
     * TC_TP_02: Mở popup chi tiết phí phạt và hiển thị đúng thông tin
     */
    @Test
    public void TC_TP_02_moPopupChiTietPhiPhat() {
        WebElement row = getFineRows().get(0);

        String tenNguoiDung = getTenNguoiDung(row);
        String tongTien = getTongTien(row);

        openChiTietPhiPhat(row);

        Assert.assertEquals(
                traSachPage.getPopupTenNguoiDung().getText().trim(),
                tenNguoiDung,
                "Tên người dùng trong popup không khớp"
        );

        Assert.assertTrue(
                traSachPage.getPopupTongTien().getText().contains(tongTien),
                "Tổng tiền trong popup không khớp"
        );

        Assert.assertFalse(
                traSachPage.getPopupChiTietKhoanPhat().isEmpty(),
                "Popup phải hiển thị ít nhất một khoản phạt"
        );
    }

    /**
     * TC_TP_03: Báo lỗi khi chưa chọn phương thức thanh toán
     */
    @Test
    public void TC_TP_03_loiChuaChonPhuongThucThanhToan() {
        WebElement row = findUnpaidFineRow();

        openChiTietPhiPhat(row);

        traSachPage.clickXacNhanThanhToan();

        Assert.assertEquals(
                traSachPage.getErrorPhuongThucThanhToan(),
                ERR_CHUA_CHON_PHUONG_THUC,
                "Thông báo lỗi khi chưa chọn phương thức thanh toán không đúng"
        );
    }

    /**
     * TC_TP_04: Thanh toán thành công các khoản phạt chưa thanh toán
     */
    @Test
    public void TC_TP_04_thanhToanPhiPhatThanhCong() {
        WebElement row = findUnpaidFineRow();
        String tenNguoiDung = getTenNguoiDung(row);

        openChiTietPhiPhat(row);

        traSachPage.chonPhuongThucThanhToanTienMat();
        traSachPage.clickXacNhanThanhToan();

        Assert.assertTrue(
                traSachPage.getSuccessMessage().contains(MSG_THANH_TOAN_THANH_CONG),
                "Thông báo thanh toán thành công không đúng"
        );

        refreshThanhToanPhiPhatTab();

        WebElement rowSauThanhToan = findFineRowByTenNguoiDung(tenNguoiDung);

        Assert.assertEquals(
                normalizeMoney(getTongTien(rowSauThanhToan)),
                "0",
                "Sau khi thanh toán, tổng tiền phải bằng 0"
        );
    }

    /**
     * TC_TP_05: Hủy thao tác thanh toán phí phạt
     */
    @Test
    public void TC_TP_05_huyThanhToanPhiPhat() {
        WebElement row = findUnpaidFineRow();

        String tenNguoiDung = getTenNguoiDung(row);
        String tongTienTruocKhiHuy = normalizeMoney(getTongTien(row));

        openChiTietPhiPhat(row);

        traSachPage.clickHuyThanhToan();

        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanHuyThanhToan()));

        traSachPage.xacNhanHuyThanhToan();

        wait.until(ExpectedConditions.invisibilityOf(traSachPage.getPopupChiTietPhiPhat()));

        refreshThanhToanPhiPhatTab();

        WebElement rowSauKhiHuy = findFineRowByTenNguoiDung(tenNguoiDung);
        String tongTienSauKhiHuy = normalizeMoney(getTongTien(rowSauKhiHuy));

        Assert.assertEquals(
                tongTienSauKhiHuy,
                tongTienTruocKhiHuy,
                "Sau khi hủy, tổng tiền phạt phải giữ nguyên"
        );
    }

    /**
     * TC_TP_06: Xem chi tiết người dùng đã thanh toán hết phí phạt
     */
    @Test
    public void TC_TP_06_xemChiTietNguoiDungDaThanhToanHet() {
        WebElement row = findPaidFineRow();

        openChiTietPhiPhat(row);

        List<WebElement> chiTiet = traSachPage.getPopupChiTietKhoanPhat();

        Assert.assertFalse(
                chiTiet.isEmpty(),
                "Phải có khoản phạt đã thanh toán"
        );

        for (WebElement item : chiTiet) {
            Assert.assertEquals(
                    item.findElement(traSachPage.getSelectorTrangThaiPhat()).getText().trim(),
                    "Đã thanh toán",
                    "Trạng thái khoản phạt phải là Đã thanh toán"
            );
        }

        Assert.assertFalse(
                traSachPage.isXacNhanThanhToanEnabled(),
                "Nút xác nhận thanh toán phải bị ẩn hoặc vô hiệu hóa"
        );
    }
}