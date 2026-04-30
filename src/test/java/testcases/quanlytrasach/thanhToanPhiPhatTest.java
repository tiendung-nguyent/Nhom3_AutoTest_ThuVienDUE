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
import java.util.ArrayList;
import java.util.List;

public class thanhToanPhiPhatTest {

    private WebDriverWait wait;
    private quanLyTraSachPage traSachPage;

    private static final String TRANG_THAI_CHUA_THANH_TOAN = "Chưa thanh toán";
    private static final String TRANG_THAI_DA_THANH_TOAN = "Đã thanh toán";

    private static final String MSG_CHUA_CHON_PTTT = "Vui lòng chọn phương thức thanh toán.";
    private static final String MSG_THANH_TOAN_THANH_CONG_1 = "Xác nhận thanh toán thành công";
    private static final String MSG_THANH_TOAN_THANH_CONG_2 = "Thanh toán phí phạt thành công";

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

        List<WebElement> allRows = traSachPage.getRowsPhiPhat();
        List<WebElement> dataRows = new ArrayList<>();

        System.out.println("DEBUG: Tổng số dòng Selenium lấy được: " + allRows.size());

        for (int i = 0; i < allRows.size(); i++) {
            WebElement row = allRows.get(i);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String rowText = normalizeText(row.getText());

            System.out.println(
                    "DEBUG ROW " + (i + 1)
                            + " | displayed: " + row.isDisplayed()
                            + " | td count: " + cells.size()
                            + " | text: " + rowText
            );

            /*
             * Chỉ lấy dòng dữ liệu thật.
             *
             * Bảng danh sách phí phạt:
             * Cột 1: Mã phạt
             * Cột 2: Người dùng
             * Cột 3: Mã người dùng
             * Cột 4: Tổng tiền phạt chưa thanh toán
             */
            if (row.isDisplayed()
                    && cells.size() >= 4
                    && !rowText.isEmpty()) {
                dataRows.add(row);
            }
        }

        System.out.println("DEBUG: Số dòng dữ liệu phí phạt hợp lệ: " + dataRows.size());

        Assert.assertFalse(
                dataRows.isEmpty(),
                "Không có dòng dữ liệu phí phạt hợp lệ. " +
                        "Cần kiểm tra dữ liệu phí phạt hoặc selector rowPhiPhat trong quanLyTraSachPage."
        );

        return dataRows;
    }

    private WebElement findUnpaidFineRow() {
        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            long tongTien = parseMoney(getTongTien(row));

            System.out.println(
                    "DEBUG UNPAID ROW | User: " + getTenNguoiDung(row)
                            + " | Tổng tiền: " + getTongTien(row)
                            + " | Parse: " + tongTien
                            + " | Row: " + normalizeText(row.getText())
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
        return getFineRows().stream()
                .filter(row -> parseMoney(getTongTien(row)) == 0)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không có người dùng đã thanh toán hết phí phạt. " +
                                "Cần có người dùng với Tổng tiền cần thanh toán = 0đ."
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

    /**
     * Dùng cho TC_TP_02.
     * Mở popup một lần duy nhất với user có cả khoản Chưa thanh toán và Đã thanh toán.
     * Nếu dòng đang xét không đúng thì đóng popup rồi xét dòng tiếp theo.
     */
    private WebElement openPopupCuaNguoiDungCoCaChuaThanhToanVaDaThanhToan() {
        List<WebElement> rows = getFineRows();

        for (WebElement row : rows) {
            openChiTietPhiPhat(row);

            boolean hasUnpaid = popupHasFineStatus(TRANG_THAI_CHUA_THANH_TOAN);
            boolean hasPaid = popupHasFineStatus(TRANG_THAI_DA_THANH_TOAN);

            if (hasUnpaid && hasPaid) {
                return row;
            }

            closePaymentPopupSafely();
        }

        throw new AssertionError(
                "Không tìm thấy người dùng có cả khoản phạt Chưa thanh toán và Đã thanh toán. " +
                        "Theo TC_TP_02, cần chuẩn bị User A có 2 khoản chưa thanh toán và 1 khoản đã thanh toán."
        );
    }

    // =========================
    // GETTERS / UTILS
    // =========================

    private String getTenNguoiDung(WebElement row) {
        List<WebElement> cells = row.findElements(By.tagName("td"));

        Assert.assertTrue(
                cells.size() >= 2,
                "Dòng phí phạt không đủ cột để lấy tên người dùng. " +
                        "Số cột thực tế: " + cells.size() +
                        ". Nội dung dòng: " + normalizeText(row.getText())
        );

        return normalizeText(cells.get(1).getText());
    }

    private String getMaNguoiDung(WebElement row) {
        List<WebElement> cells = row.findElements(By.tagName("td"));

        Assert.assertTrue(
                cells.size() >= 3,
                "Dòng phí phạt không đủ cột để lấy mã người dùng. " +
                        "Số cột thực tế: " + cells.size() +
                        ". Nội dung dòng: " + normalizeText(row.getText())
        );

        return normalizeText(cells.get(2).getText());
    }

    private String getTongTien(WebElement row) {
        List<WebElement> cells = row.findElements(By.tagName("td"));

        Assert.assertTrue(
                cells.size() >= 4,
                "Dòng phí phạt không đủ cột để lấy Tổng tiền. " +
                        "Số cột thực tế: " + cells.size() +
                        ". Nội dung dòng: " + normalizeText(row.getText())
        );

        return normalizeText(cells.get(3).getText());
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
            if (getTrangThaiPhat(fineRow).equals(TRANG_THAI_CHUA_THANH_TOAN)) {
                total += parseMoney(getSoTien(fineRow));
            }
        }

        return total;
    }

    private int countUnpaidFineRowsInPopup() {
        int count = 0;

        for (WebElement fineRow : getPopupFineRows()) {
            if (getTrangThaiPhat(fineRow).equals(TRANG_THAI_CHUA_THANH_TOAN)) {
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

    // =========================
    // POPUP HELPERS
    // =========================

    private void forceClosePaymentPopups() {
        try {
            ((JavascriptExecutor) Constant.WEBDRIVER).executeScript(
                    "var ids = ['popup-payment', 'popup-xacnhan-huy-thanhtoan', 'popupOverlay'];" +
                            "ids.forEach(function(id) {" +
                            "  var el = document.getElementById(id);" +
                            "  if (el) {" +
                            "    el.style.display = 'none';" +
                            "    el.classList.remove('show', 'active', 'open');" +
                            "  }" +
                            "});" +
                            "document.body.classList.remove('modal-open');" +
                            "document.body.style.overflow = '';"
            );
        } catch (Exception ignored) {
        }
    }

    private void waitPaymentPopupClosed() {
        try {
            wait.until(driver -> {
                List<WebElement> cancelPopups = Constant.WEBDRIVER.findElements(
                        By.xpath("//*[contains(normalize-space(), 'Xác nhận hủy') " +
                                "and .//*[contains(normalize-space(), 'Thoát thanh toán')]]")
                );

                for (WebElement popup : cancelPopups) {
                    try {
                        if (popup.isDisplayed()) {
                            return false;
                        }
                    } catch (Exception ignored) {
                    }
                }

                List<WebElement> paymentPopups = Constant.WEBDRIVER.findElements(By.id("popup-payment"));

                if (paymentPopups.isEmpty()) {
                    return true;
                }

                try {
                    return !paymentPopups.get(0).isDisplayed();
                } catch (Exception e) {
                    return true;
                }
            });
        } catch (Exception e) {
            /*
             * TC05 đã bấm xác nhận hủy rồi.
             * Nếu popup/overlay bị treo ở UI, đóng bằng JS để quay về danh sách kiểm tra dữ liệu.
             */
            forceClosePaymentPopups();
        }
    }

    private boolean isCancelPaymentPopupDisplayed() {
        List<WebElement> popups = Constant.WEBDRIVER.findElements(
                By.xpath(
                        "//*[contains(normalize-space(), 'Xác nhận hủy') " +
                                "and .//*[contains(normalize-space(), 'Thoát thanh toán')]]"
                )
        );

        for (WebElement popup : popups) {
            try {
                if (popup.isDisplayed()) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        return false;
    }

    private void xacNhanThoatThanhToan() {
        WebDriverWait shortWait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(8));
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;

        By btnThoatThanhToan = By.xpath(
                "//button[normalize-space()='Thoát thanh toán' " +
                        "or contains(normalize-space(), 'Thoát thanh toán')]"
        );

        WebElement button = shortWait.until(driver -> {
            List<WebElement> buttons = Constant.WEBDRIVER.findElements(btnThoatThanhToan);

            for (WebElement item : buttons) {
                try {
                    if (item.isDisplayed() && item.isEnabled()) {
                        return item;
                    }
                } catch (Exception ignored) {
                }
            }

            return null;
        });

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(button)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", button);
        }

        try {
            shortWait.until(driver -> {
                boolean cancelPopupClosed = !isCancelPaymentPopupDisplayed();

                List<WebElement> paymentPopups = Constant.WEBDRIVER.findElements(By.id("popup-payment"));
                boolean paymentPopupClosed = paymentPopups.isEmpty();

                if (!paymentPopups.isEmpty()) {
                    try {
                        paymentPopupClosed = !paymentPopups.get(0).isDisplayed();
                    } catch (Exception e) {
                        paymentPopupClosed = true;
                    }
                }

                return cancelPopupClosed || paymentPopupClosed;
            });
        } catch (Exception e) {
            forceClosePaymentPopups();
        }
    }

    private void closePaymentPopupSafely() {
        try {
            if (isCancelPaymentPopupDisplayed()) {
                xacNhanThoatThanhToan();
            }

            List<WebElement> paymentPopups = Constant.WEBDRIVER.findElements(By.id("popup-payment"));

            if (paymentPopups.isEmpty()) {
                return;
            }

            try {
                if (!paymentPopups.get(0).isDisplayed()) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            /*
             * UI hiện tại: bấm X hoặc Hủy đều có thể hiện popup xác nhận hủy.
             */
            List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
                    By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') " +
                            "or contains(normalize-space(), 'Đóng') " +
                            "or contains(normalize-space(), 'Hủy')]")
            );

            if (!closeButtons.isEmpty()) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(closeButtons.get(0))).click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", closeButtons.get(0));
                }

                if (isCancelPaymentPopupDisplayed()) {
                    xacNhanThoatThanhToan();
                }
            }

            waitPaymentPopupClosed();

        } catch (Exception ignored) {
            forceClosePaymentPopups();
        }
    }

    private void waitCancelPaymentPopupDisplayed() {
        try {
            wait.until(driver -> isCancelPaymentPopupDisplayed()
                    || traSachPage.getPopupXacNhanHuyThanhToan().isDisplayed());
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
                    status.equals(TRANG_THAI_CHUA_THANH_TOAN)
                            || status.equals(TRANG_THAI_DA_THANH_TOAN),
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
        wait.until(driver -> {
            String bodyText = normalizeText(
                    Constant.WEBDRIVER.findElement(By.tagName("body")).getText()
            );

            return bodyText.contains(MSG_CHUA_CHON_PTTT)
                    || bodyText.contains("chọn phương thức thanh toán")
                    || bodyText.contains("phương thức thanh toán");
        });

        String bodyText = normalizeText(
                Constant.WEBDRIVER.findElement(By.tagName("body")).getText()
        );

        Assert.assertTrue(
                bodyText.contains(MSG_CHUA_CHON_PTTT)
                        || bodyText.contains("chọn phương thức thanh toán")
                        || bodyText.contains("phương thức thanh toán"),
                "Hệ thống phải hiển thị lỗi “Vui lòng chọn phương thức thanh toán.” Nội dung thực tế: " + bodyText
        );
    }

    private void assertPaymentSuccessMessageDisplayed() {
        try {
            String message = traSachPage.getSuccessMessage();

            Assert.assertTrue(
                    message.contains(MSG_THANH_TOAN_THANH_CONG_1)
                            || message.contains(MSG_THANH_TOAN_THANH_CONG_2)
                            || message.contains("Thanh toán thành công")
                            || message.contains("thanh toán thành công"),
                    "Hệ thống phải hiển thị thông báo thanh toán thành công. Thực tế: " + message
            );
        } catch (Exception e) {
            String bodyText = normalizeText(Constant.WEBDRIVER.findElement(By.tagName("body")).getText());

            Assert.assertTrue(
                    bodyText.contains(MSG_THANH_TOAN_THANH_CONG_1)
                            || bodyText.contains(MSG_THANH_TOAN_THANH_CONG_2)
                            || bodyText.contains("Thanh toán thành công")
                            || bodyText.contains("thanh toán thành công"),
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
            String maNguoiDung = getMaNguoiDung(row);
            long tongTien = parseMoney(getTongTien(row));

            Assert.assertFalse(
                    tenNguoiDung.isEmpty(),
                    "Tên người dùng không được rỗng"
            );

            Assert.assertFalse(
                    maNguoiDung.isEmpty(),
                    "Mã người dùng không được rỗng"
            );

            Assert.assertTrue(
                    tongTien >= 0,
                    "Tổng tiền cần thanh toán phải >= 0"
            );
        }
    }

    @Test(priority = 2)
    public void TC_TP_02_moPopupChiTietPhiPhat() {
        WebElement row = openPopupCuaNguoiDungCoCaChuaThanhToanVaDaThanhToan();

        Assert.assertTrue(
                traSachPage.getPopupChiTietPhiPhat().isDisplayed(),
                "Hệ thống phải mở popup Thanh toán phí phạt/Chi tiết phí phạt"
        );

        assertPopupUserInfoMatchesList(row);
        assertFineDetailRowsHaveRequiredColumns();

        Assert.assertTrue(
                popupHasFineStatus(TRANG_THAI_CHUA_THANH_TOAN),
                "Popup phải hiển thị khoản phạt Chưa thanh toán"
        );

        Assert.assertTrue(
                popupHasFineStatus(TRANG_THAI_DA_THANH_TOAN),
                "Popup phải hiển thị khoản phạt Đã thanh toán"
        );

        assertPopupTotalOnlyIncludesUnpaidFines();

        /*
         * TC02 chỉ kiểm tra popup hiển thị đúng.
         * Không đóng popup để tránh phát sinh popup xác nhận hủy.
         * @AfterMethod sẽ đóng browser.
         */
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

        Assert.assertTrue(
                traSachPage.getPopupChiTietPhiPhat().isDisplayed(),
                "Khi chưa chọn phương thức thanh toán, popup thanh toán vẫn phải đang mở"
        );

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

        /*
         * TC03 pass tại đây.
         * Không đóng popup vì mục tiêu TC03 là kiểm lỗi khi chưa chọn phương thức.
         * @AfterMethod sẽ đóng browser.
         */
    }

    @Test(priority = 4)
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

        /*
         * Popup xác nhận hủy hiện ra.
         * Bấm 'Thoát thanh toán' để xác nhận hủy thao tác thanh toán.
         */
        xacNhanThoatThanhToan();

        waitPaymentPopupClosed();

        refreshThanhToanPhiPhatTab();

        WebElement rowSauKhiHuy = findFineRowByTenNguoiDung(tenNguoiDung);
        long tongTienSauKhiHuy = parseMoney(getTongTien(rowSauKhiHuy));

        Assert.assertEquals(
                tongTienSauKhiHuy,
                tongTienTruocKhiHuy,
                "Sau khi hủy, tổng tiền phạt phải giữ nguyên"
        );

        Assert.assertTrue(
                tongTienSauKhiHuy > 0,
                "Sau khi hủy, người dùng vẫn phải còn khoản phạt chưa thanh toán"
        );

        /*
         * Không mở lại popup chi tiết lần 2.
         * TC05 chỉ cần xác nhận hủy thao tác và kiểm tra dữ liệu danh sách không đổi.
         */
    }

    @Test(priority = 5)
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

        Assert.assertTrue(
                traSachPage.isPhuongThucTienMatSelected(),
                "Phương thức thanh toán Tiền mặt phải được chọn trước khi bấm Xác nhận"
        );

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
                    TRANG_THAI_DA_THANH_TOAN,
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
                    TRANG_THAI_DA_THANH_TOAN,
                    "Tất cả khoản phạt của người dùng đã thanh toán hết phải có trạng thái Đã thanh toán"
            );
        }

        Assert.assertTrue(
                isConfirmPaymentDisabledOrHidden(),
                "Người dùng đã thanh toán hết không được phép thanh toán lại. Nút Xác nhận phải bị ẩn hoặc vô hiệu hóa"
        );
    }
}