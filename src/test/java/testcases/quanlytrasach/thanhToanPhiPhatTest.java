package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.SidebarPage;
import pageobjects.quanLyTraSachPage;

import java.time.Duration;
import java.util.List;

public class thanhToanPhiPhatTest {

    private WebDriverWait wait;
    private quanLyTraSachPage traSachPage;

    private static String paidUserNameAfterPayment;

    @BeforeMethod
    public void setUp() {
        initDriver();
        login();
        openThanhToanPhiPhatTab();
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

    private void openThanhToanPhiPhatTab() {
        SidebarPage sidebarPage = new SidebarPage();

        sidebarPage.gotoReturn();
        traSachPage.openTabThanhToanPhiPhat();

        waitForPaymentTabLoaded();
    }

    private void refreshThanhToanPhiPhatTab() {
        openThanhToanPhiPhatTab();
    }

    private void openChiTietPhiPhat(WebElement row) {
        traSachPage.clickXemChiTietPhiPhatWithWait(row);

        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupChiTietPhiPhat()));
        wait.until(driver -> !traSachPage.getPopupChiTietKhoanPhat().isEmpty());
    }

    private void truyXuatDanhSachPhiPhatIfExists() {
        List<WebElement> buttons = Constant.WEBDRIVER.findElements(
                By.xpath("//button[contains(normalize-space(), 'Truy xuất') " +
                        "or contains(normalize-space(), 'Tìm kiếm') " +
                        "or contains(normalize-space(), 'Lọc') " +
                        "or contains(normalize-space(), 'Xem danh sách') " +
                        "or contains(normalize-space(), 'Tải danh sách') " +
                        "or contains(normalize-space(), 'Tra cứu') " +
                        "or contains(@onclick, 'lay_danh_sach_phi_phat') " +
                        "or contains(@onclick, 'load') " +
                        "or contains(@onclick, 'filter') " +
                        "or contains(@onclick, 'search')]")
        );

        if (!buttons.isEmpty()) {
            WebElement button = buttons.get(0);

            ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

            try {
                wait.until(ExpectedConditions.elementToBeClickable(button)).click();
            } catch (Exception e) {
                ((JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].click();", button);
            }
        }

        try {
            wait.until(driver -> !traSachPage.getRowsPhiPhat().isEmpty());
        } catch (Exception e) {
            System.out.println("DEBUG: Sau khi truy xuất, chưa thấy dòng phí phạt nào.");
        }
    }

    // =========================
    // FINDERS
    // =========================

    private List<WebElement> getFineRows() {
        truyXuatDanhSachPhiPhatIfExists();

        List<WebElement> rows = traSachPage.getRowsPhiPhat();

        System.out.println("DEBUG: Số dòng phí phạt lấy được: " + rows.size());

        Assert.assertFalse(
                rows.isEmpty(),
                "Không có bản ghi phí phạt nào sau khi truy xuất danh sách. " +
                        "Cần kiểm tra dữ liệu phí phạt hoặc selector rowPhiPhat trong quanLyTraSachPage."
        );

        return rows;
    }

    private WebElement findUnpaidFineRow() {
        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            long tongTien = parseMoney(getTongTien(row));

            System.out.println(
                    "DEBUG UNPAID ROW | User: " + getTenNguoiDung(row)
                            + " | Tổng tiền: " + getTongTien(row)
                            + " | Parse: " + tongTien
                            + " | Row: " + row.getText()
            );

            if (tongTien > 0) {
                return row;
            }
        }

        throw new AssertionError(
                "Không có người dùng còn nợ phí phạt. " +
                        "Cần chuẩn bị dữ liệu có Tổng tiền cần thanh toán > 0."
        );
    }

    private WebElement findPaidFineRow() {
        List<WebElement> rows = getFineRows();

        return rows.stream()
                .filter(row -> parseMoney(getTongTien(row)) == 0)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không có người dùng đã thanh toán hết phí phạt. " +
                                "Theo đề, cần có User B với Tổng tiền cần thanh toán = 0đ."
                ));
    }

    private WebElement findMixedStatusFineRow() {
        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            String tenNguoiDung = getTenNguoiDung(row);

            openChiTietPhiPhat(row);

            boolean hasUnpaid = popupHasFineStatus("Chưa thanh toán");
            boolean hasPaid = popupHasFineStatus("Đã thanh toán");

            closePaymentPopupSafely();

            if (hasUnpaid && hasPaid) {
                return findFineRowByTenNguoiDung(tenNguoiDung);
            }
        }

        throw new AssertionError(
                "Không tìm thấy người dùng có cả khoản phạt Chưa thanh toán và Đã thanh toán. " +
                        "Theo TC_TP_02, cần chuẩn bị User A có 2 khoản chưa thanh toán và 1 khoản đã thanh toán."
        );
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
        return normalizeText(row.findElement(traSachPage.getSelectorTenNguoiDung()).getText());
    }

    private String getTongTien(WebElement row) {
        try {
            return normalizeText(row.findElement(traSachPage.getSelectorTongTien()).getText());
        } catch (Exception e) {
            List<WebElement> cells = row.findElements(By.tagName("td"));

            if (cells.size() >= 4) {
                return normalizeText(cells.get(3).getText());
            }

            throw new AssertionError(
                    "Không lấy được Tổng tiền cần thanh toán từ dòng phí phạt. Nội dung dòng: "
                            + row.getText()
            );
        }
    }

    private long parseMoney(String value) {
        if (value == null) {
            return 0;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.isEmpty()) {
            return 0;
        }

        return Long.parseLong(digits);
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

    private List<WebElement> getPopupFineRows() {
        List<WebElement> rows = traSachPage.getPopupChiTietKhoanPhat();

        Assert.assertFalse(
                rows.isEmpty(),
                "Popup chi tiết phí phạt phải hiển thị ít nhất một khoản phạt."
        );

        return rows;
    }

    private List<WebElement> getCells(WebElement row) {
        List<WebElement> cells = row.findElements(By.tagName("td"));

        Assert.assertTrue(
                cells.size() >= 8,
                "Mỗi dòng chi tiết khoản phạt phải có đủ 8 cột: " +
                        "Mã sách trong kho, Tên sách, Loại phạt, Lý do, Số tiền, Ngày tạo, Mã phiếu mượn, Trạng thái. " +
                        "Số cột thực tế: " + cells.size()
        );

        return cells;
    }

    private String getSoTien(WebElement fineRow) {
        return normalizeText(getCells(fineRow).get(4).getText());
    }

    private String getMaPhieuMuon(WebElement fineRow) {
        return normalizeText(getCells(fineRow).get(6).getText());
    }

    private String getTrangThaiPhat(WebElement fineRow) {
        return normalizeText(getCells(fineRow).get(7).getText());
    }

    private long getTongTienChuaThanhToanTrongPopup() {
        long total = 0;

        for (WebElement fineRow : getPopupFineRows()) {
            if (getTrangThaiPhat(fineRow).equals("Chưa thanh toán")) {
                total += parseMoney(getSoTien(fineRow));
            }
        }

        return total;
    }

    private int countUnpaidFineRowsInPopup() {
        int count = 0;

        for (WebElement fineRow : getPopupFineRows()) {
            if (getTrangThaiPhat(fineRow).equals("Chưa thanh toán")) {
                count++;
            }
        }

        return count;
    }

    private boolean popupHasFineStatus(String expectedStatus) {
        return getPopupFineRows().stream()
                .anyMatch(row -> getTrangThaiPhat(row).equals(expectedStatus));
    }

    private boolean isConfirmPaymentDisabledOrHidden() {
        try {
            return !traSachPage.isXacNhanThanhToanEnabled();
        } catch (Exception e) {
            return true;
        }
    }

    private void waitForPaymentTabLoaded() {
        wait.until(driver -> {
            String bodyText = normalizeText(
                    Constant.WEBDRIVER.findElement(By.tagName("body")).getText()
            );

            return bodyText.contains("Thanh toán phí phạt")
                    || bodyText.contains("phí phạt");
        });
    }

    private void closePaymentPopupSafely() {
        try {
            traSachPage.closePopupChiTietPhiPhat();
            waitPaymentPopupClosed();
        } catch (Exception e) {
            List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
                    By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') " +
                            "or contains(text(), 'Đóng') " +
                            "or contains(text(), 'Hủy')]")
            );

            if (!closeButtons.isEmpty()) {
                try {
                    closeButtons.get(0).click();
                } catch (Exception ex) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", closeButtons.get(0));
                }
            }
        }
    }

    private void waitPaymentPopupClosed() {
        wait.until(driver -> {
            List<WebElement> popups = Constant.WEBDRIVER.findElements(By.id("popup-payment"));

            if (popups.isEmpty()) {
                return true;
            }

            try {
                return !popups.get(0).isDisplayed();
            } catch (Exception e) {
                return true;
            }
        });
    }

    private void waitCancelPaymentPopupDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanHuyThanhToan()));
        } catch (Exception e) {
            throw new AssertionError(
                    "Hệ thống không hiển thị popup xác nhận hủy thao tác thanh toán. " +
                            "Theo TC_TP_05, sau khi bấm Hủy phải hiển thị popup xác nhận hủy thao tác.",
                    e
            );
        }
    }

    // =========================
    // ASSERT HELPERS
    // =========================

    private void assertPaymentTabDisplayed() {
        String bodyText = normalizeText(Constant.WEBDRIVER.findElement(By.tagName("body")).getText());

        Assert.assertTrue(
                bodyText.contains("Thanh toán phí phạt")
                        || bodyText.contains("phí phạt"),
                "Hệ thống phải hiển thị đúng tab Thanh toán phí phạt"
        );
    }

    private void assertPopupUserInfoMatchesList(WebElement row) {
        String tenNguoiDung = getTenNguoiDung(row);
        long tongTienDanhSach = parseMoney(getTongTien(row));

        Assert.assertEquals(
                normalizeText(traSachPage.getPopupTenNguoiDung().getText()),
                tenNguoiDung,
                "Tên người dùng trong popup không khớp với danh sách"
        );

        long tongTienPopup = parseMoney(traSachPage.getPopupTongTien().getText());

        Assert.assertEquals(
                tongTienPopup,
                tongTienDanhSach,
                "Tổng tiền cần thanh toán trong popup không khớp với danh sách"
        );
    }

    private void assertFineDetailRowsHaveRequiredColumns() {
        for (WebElement fineRow : getPopupFineRows()) {
            List<WebElement> cells = getCells(fineRow);

            Assert.assertFalse(normalizeText(cells.get(0).getText()).isEmpty(), "Mã sách trong kho không được rỗng");
            Assert.assertFalse(normalizeText(cells.get(1).getText()).isEmpty(), "Tên sách không được rỗng");
            Assert.assertFalse(normalizeText(cells.get(2).getText()).isEmpty(), "Loại phạt không được rỗng");
            Assert.assertFalse(normalizeText(cells.get(3).getText()).isEmpty(), "Lý do không được rỗng");
            Assert.assertTrue(parseMoney(cells.get(4).getText()) >= 0, "Số tiền phải hợp lệ");
            Assert.assertFalse(normalizeText(cells.get(5).getText()).isEmpty(), "Ngày tạo không được rỗng");
            Assert.assertFalse(getMaPhieuMuon(fineRow).isEmpty(), "Mã phiếu mượn không được rỗng");

            String status = getTrangThaiPhat(fineRow);

            Assert.assertTrue(
                    status.equals("Chưa thanh toán")
                            || status.equals("Đã thanh toán"),
                    "Trạng thái khoản phạt không hợp lệ: " + status
            );
        }
    }

    private void assertPopupTotalOnlyIncludesUnpaidFines() {
        long expectedTotal = getTongTienChuaThanhToanTrongPopup();
        long actualTotal = parseMoney(traSachPage.getPopupTongTien().getText());

        Assert.assertEquals(
                actualTotal,
                expectedTotal,
                "Tổng tiền cần thanh toán chỉ được tính trên các khoản phạt Chưa thanh toán"
        );
    }

    private void assertPaymentMethodErrorDisplayed() {
        WebDriverWait shortWait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));

        boolean errorDisplayed = shortWait.until(driver -> {
            String bodyText = normalizeText(driver.findElement(By.tagName("body")).getText());

            return bodyText.contains("Vui lòng chọn phương thức thanh toán.")
                    || bodyText.contains("Vui lòng chọn phương thức thanh toán")
                    || bodyText.contains("chọn phương thức thanh toán");
        });

        Assert.assertTrue(
                errorDisplayed,
                "Hệ thống phải hiển thị lỗi “Vui lòng chọn phương thức thanh toán.”"
        );
    }

    private void assertPaymentSuccessMessageDisplayed() {
        try {
            Assert.assertTrue(
                    traSachPage.getSuccessMessage().contains("Xác nhận thanh toán thành công"),
                    "Hệ thống phải hiển thị thông báo “Thanh toán phí phạt thành công”."
            );
        } catch (Exception e) {
            String bodyText = normalizeText(Constant.WEBDRIVER.findElement(By.tagName("body")).getText());

            Assert.assertTrue(
                    bodyText.contains("Thanh toán phí phạt thành công")
                            || bodyText.contains("thanh toán thành công")
                            || bodyText.contains("Thanh toán thành công")
                            || bodyText.contains("Xác nhận thanh toán thành công"),
                    "Hệ thống phải hiển thị thông báo thanh toán thành công. Nội dung thực tế: " + bodyText
            );
        }
    }

    // =========================
    // TEST CASES
    // =========================

    @Test(priority = 1)
    public void TC_TP_01_hienThiDanhSachPhiPhatNguoiDung() {
        assertPaymentTabDisplayed();

        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            String tenNguoiDung = getTenNguoiDung(row);
            long tongTien = parseMoney(getTongTien(row));

            Assert.assertFalse(tenNguoiDung.isEmpty(), "Tên người dùng không được rỗng");

            Assert.assertTrue(
                    tongTien >= 0,
                    "Tổng tiền cần thanh toán phải >= 0"
            );
        }
    }

    @Test(priority = 2)
    public void TC_TP_02_moPopupChiTietPhiPhat() {
        WebElement row = findMixedStatusFineRow();

        openChiTietPhiPhat(row);

        assertPopupUserInfoMatchesList(row);
        assertFineDetailRowsHaveRequiredColumns();

        Assert.assertTrue(
                popupHasFineStatus("Chưa thanh toán"),
                "Popup phải hiển thị khoản phạt Chưa thanh toán"
        );

        Assert.assertTrue(
                popupHasFineStatus("Đã thanh toán"),
                "Popup phải hiển thị khoản phạt Đã thanh toán"
        );

        assertPopupTotalOnlyIncludesUnpaidFines();
    }

    @Test(priority = 3)
    public void TC_TP_03_loiChuaChonPhuongThucThanhToan() {
        WebElement row = findUnpaidFineRow();

        openChiTietPhiPhat(row);

        int unpaidBefore = countUnpaidFineRowsInPopup();
        long totalBefore = parseMoney(traSachPage.getPopupTongTien().getText());

        Assert.assertTrue(
                unpaidBefore > 0,
                "Tiền điều kiện: popup phải có ít nhất một khoản phạt Chưa thanh toán"
        );

        Assert.assertTrue(
                totalBefore > 0,
                "Tiền điều kiện: tổng tiền cần thanh toán phải > 0"
        );

        traSachPage.clickXacNhanThanhToan();

        assertPaymentMethodErrorDisplayed();

        Assert.assertEquals(
                countUnpaidFineRowsInPopup(),
                unpaidBefore,
                "Khi chưa chọn phương thức thanh toán, trạng thái khoản phạt không được thay đổi"
        );

        Assert.assertEquals(
                parseMoney(traSachPage.getPopupTongTien().getText()),
                totalBefore,
                "Khi chưa chọn phương thức thanh toán, tổng tiền không được thay đổi"
        );
    }

    @Test(priority = 4)
    public void TC_TP_04_thanhToanPhiPhatThanhCong() {
        WebElement row = findUnpaidFineRow();

        String tenNguoiDung = getTenNguoiDung(row);
        long tongTienTruocThanhToan = parseMoney(getTongTien(row));

        paidUserNameAfterPayment = tenNguoiDung;

        Assert.assertTrue(
                tongTienTruocThanhToan > 0,
                "Tiền điều kiện: người dùng phải có Tổng tiền cần thanh toán > 0"
        );

        openChiTietPhiPhat(row);

        int unpaidBefore = countUnpaidFineRowsInPopup();
        long totalBefore = parseMoney(traSachPage.getPopupTongTien().getText());

        Assert.assertTrue(
                unpaidBefore > 0,
                "Tiền điều kiện: phải có ít nhất một khoản phạt Chưa thanh toán"
        );

        Assert.assertTrue(
                totalBefore > 0,
                "Tiền điều kiện: tổng tiền cần thanh toán trong popup phải > 0"
        );

        traSachPage.chonPhuongThucThanhToanTienMat();

        traSachPage.clickXacNhanThanhToan();

        assertPaymentSuccessMessageDisplayed();

        refreshThanhToanPhiPhatTab();

        WebElement rowSauThanhToan = findFineRowByTenNguoiDung(tenNguoiDung);
        long tongTienSauThanhToan = parseMoney(getTongTien(rowSauThanhToan));

        Assert.assertEquals(
                tongTienSauThanhToan,
                0,
                "Sau khi thanh toán hết, Tổng tiền cần thanh toán phải được cập nhật bằng 0đ"
        );

        openChiTietPhiPhat(rowSauThanhToan);

        List<WebElement> fineRowsAfterPayment = getPopupFineRows();

        Assert.assertFalse(
                fineRowsAfterPayment.isEmpty(),
                "Dữ liệu phí phạt vẫn phải được lưu và hiển thị để xem chi tiết sau khi thanh toán"
        );

        for (WebElement fineRow : fineRowsAfterPayment) {
            Assert.assertEquals(
                    getTrangThaiPhat(fineRow),
                    "Đã thanh toán",
                    "Sau khi thanh toán, tất cả khoản phạt của người dùng phải chuyển sang Đã thanh toán"
            );
        }

        Assert.assertEquals(
                parseMoney(traSachPage.getPopupTongTien().getText()),
                0,
                "Sau khi thanh toán hết, Tổng tiền cần thanh toán trong popup phải bằng 0đ"
        );

        Assert.assertTrue(
                isConfirmPaymentDisabledOrHidden(),
                "Sau khi thanh toán hết, nút Xác nhận thanh toán phải bị ẩn hoặc vô hiệu hóa"
        );
    }

    @Test(priority = 5)
    public void TC_TP_05_huyThanhToanPhiPhat() {
        WebElement row = findUnpaidFineRow();

        String tenNguoiDung = getTenNguoiDung(row);
        long tongTienTruocKhiHuy = parseMoney(getTongTien(row));

        openChiTietPhiPhat(row);

        int unpaidBefore = countUnpaidFineRowsInPopup();

        Assert.assertTrue(
                unpaidBefore > 0,
                "Tiền điều kiện: người dùng phải có khoản phạt Chưa thanh toán"
        );

        Assert.assertTrue(
                tongTienTruocKhiHuy > 0,
                "Tiền điều kiện: tổng tiền trước khi hủy phải > 0"
        );

        traSachPage.clickHuyThanhToan();

        waitCancelPaymentPopupDisplayed();

        traSachPage.xacNhanHuyThanhToan();

        waitPaymentPopupClosed();

        refreshThanhToanPhiPhatTab();

        WebElement rowSauKhiHuy = findFineRowByTenNguoiDung(tenNguoiDung);
        long tongTienSauKhiHuy = parseMoney(getTongTien(rowSauKhiHuy));

        Assert.assertEquals(
                tongTienSauKhiHuy,
                tongTienTruocKhiHuy,
                "Sau khi hủy, tổng tiền phạt phải giữ nguyên"
        );

        openChiTietPhiPhat(rowSauKhiHuy);

        Assert.assertEquals(
                countUnpaidFineRowsInPopup(),
                unpaidBefore,
                "Sau khi hủy, trạng thái các khoản phạt không được thay đổi"
        );
    }

    @Test(priority = 6)
    public void TC_TP_06_xemChiTietNguoiDungDaThanhToanHet() {
        WebElement row;

        if (paidUserNameAfterPayment != null && !paidUserNameAfterPayment.trim().isEmpty()) {
            refreshThanhToanPhiPhatTab();
            row = findFineRowByTenNguoiDung(paidUserNameAfterPayment);
        } else {
            row = findPaidFineRow();
        }

        Assert.assertEquals(
                parseMoney(getTongTien(row)),
                0,
                "Người dùng đã thanh toán hết phải có Tổng tiền cần thanh toán = 0đ"
        );

        openChiTietPhiPhat(row);

        List<WebElement> chiTiet = getPopupFineRows();

        Assert.assertFalse(
                chiTiet.isEmpty(),
                "Người dùng đã thanh toán hết vẫn phải có dữ liệu phí phạt để xem chi tiết"
        );

        for (WebElement item : chiTiet) {
            Assert.assertEquals(
                    getTrangThaiPhat(item),
                    "Đã thanh toán",
                    "Tất cả khoản phạt của người dùng đã thanh toán hết phải có trạng thái Đã thanh toán"
            );
        }

        Assert.assertTrue(
                isConfirmPaymentDisabledOrHidden(),
                "Người dùng đã thanh toán hết không được phép thanh toán lại. Nút Xác nhận phải bị ẩn hoặc vô hiệu hóa"
        );
    }
}