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
import pageobjects.quanLyMuonSachPage;
import pageobjects.quanLyTraSachPage;
import pageobjects.nguoiDungPage;

import java.time.Duration;
import java.util.List;

public class XacNhanTraSachTest {

    private WebDriverWait wait;
    private quanLyTraSachPage traSachPage;

    private static final String MA_SACH_TEST = "MS 0";

    private static final String MSG_THEM_PHIEU_MUON_THANH_CONG = "Thêm thông tin mượn sách thành công";
    private static final String MSG_TRA_SACH_THANH_CONG = "Xác nhận trả sách thành công";
    private static final String ERR_CHUA_CHON_TINH_TRANG = "Vui lòng chọn tình trạng sách khi trả.";

    private static final String TRANG_THAI_DANG_MUON = "Đang mượn";
    private static final String TRANG_THAI_QUA_HAN = "Quá hạn";
    private static final String TRANG_THAI_DA_TRA = "Đã trả";

    private static final String LOAI_PHAT_TRE_HAN = "Trễ hạn";
    private static final String LOAI_PHAT_HU_HONG = "Hư hỏng";
    private static final String TRANG_THAI_PHAT_CHUA_THANH_TOAN = "Chưa thanh toán";

    private static final String MUC_HU_HONG_NHE = "Hư hỏng nhẹ";
    private static final String MUC_HU_HONG_VUA = "Hư hỏng vừa";
    private static final String MUC_HU_HONG_NANG = "Hư hỏng nặng";

    @BeforeMethod
    public void setUp() {
        initDriver();
        login();
        openXacNhanTraTab();
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

    private void openXacNhanTraTab() {
        SidebarPage sidebarPage = new SidebarPage();

        sidebarPage.gotoReturn();

        wait.until(ExpectedConditions.elementToBeClickable(traSachPage.getTabXacNhanTra()));
        traSachPage.getTabXacNhanTra().click();

        waitForReturnRowsOrEmptyPage();
    }

    private void openTabThanhToanPhiPhat() {
        SidebarPage sidebarPage = new SidebarPage();

        sidebarPage.gotoReturn();

        traSachPage.openTabThanhToanPhiPhat();
    }

    private void refreshXacNhanTraTab() {
        Constant.WEBDRIVER.navigate().refresh();
        waitForReturnRowsOrEmptyPage();
    }

    // =========================
    // TEST DATA
    // =========================

    private WebElement taoPhieuMuonVaLayDongXacNhanTra() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        String maDocGia = getMaDocGiaHopLe(sidebarPage);

        sidebarPage.gotoBorrow();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGia);

        /*
         * Đúng nghiệp vụ:
         * Nhập mã sách rồi chọn sách từ danh sách gợi ý.
         */
        muonSachPage.enterMaSachAtRow(0, MA_SACH_TEST);

        String actualBookCode = getSelectedBookCode();

        muonSachPage.clickLuuPhieuMuon();

        Assert.assertEquals(
                muonSachPage.getToastMessage(),
                MSG_THEM_PHIEU_MUON_THANH_CONG,
                "Tạo phiếu mượn tiền điều kiện thất bại"
        );

        sidebarPage.gotoReturn();
        traSachPage.getTabXacNhanTra().click();

        waitForReturnRows();

        return findReturnRowByBookCode(actualBookCode, TRANG_THAI_DANG_MUON);
    }

    private String getMaDocGiaHopLe(SidebarPage sidebarPage) {
        sidebarPage.gotoUsers();

        nguoiDungPage nguoiDungPage = new nguoiDungPage();
        String maDocGia = nguoiDungPage.getUnqualifiedReaderID();

        Assert.assertNotNull(maDocGia, "Không lấy được mã độc giả hợp lệ");
        Assert.assertFalse(maDocGia.trim().isEmpty(), "Mã độc giả hợp lệ không được rỗng");

        return maDocGia;
    }

    private String getSelectedBookCode() {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(
                By.cssSelector("input.pm-input.book-code-input")
        );

        if (inputs.isEmpty()) {
            return MA_SACH_TEST;
        }

        String value = inputs.get(0).getAttribute("value");

        if (value == null || value.trim().isEmpty()) {
            return MA_SACH_TEST;
        }

        return value.trim();
    }

    // =========================
    // ACTIONS
    // =========================

    private void openPopupXacNhanTra(WebElement row) {
        traSachPage.clickXacNhanTra(row);

        wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
    }

    private void traSachTinhTrangTot(WebElement row) {
        openPopupXacNhanTra(row);

        traSachPage.chonTinhTrangTot();
        traSachPage.clickXacNhan();

        assertReturnSuccessMessage();
    }

    private void traSachHuHong(WebElement row, String mucDoHuHong, String moTaHuHong) {
        openPopupXacNhanTra(row);

        traSachPage.chonTinhTrangHuHong();

        assertPopupContains(
                "Thông tin hư hỏng",
                "Popup phải hiển thị khu vực thông tin hư hỏng sau khi chọn Hư hỏng"
        );

        assertPopupContains(
                "Mức độ hư hỏng",
                "Popup phải hiển thị trường mức độ hư hỏng"
        );

        assertPopupContains(
                "Mô tả tình trạng hư hỏng",
                "Popup phải hiển thị trường mô tả tình trạng hư hỏng"
        );

        chonMucDoHuHong(mucDoHuHong);
        nhapMoTaHuHong(moTaHuHong);

        traSachPage.clickXacNhan();

        assertReturnSuccessMessage();
    }

    private void chonMucDoHuHong(String mucDo) {
        wait.until(driver -> !Constant.WEBDRIVER.findElements(By.name("damageLevel")).isEmpty());

        List<WebElement> radios = Constant.WEBDRIVER.findElements(By.name("damageLevel"));

        for (WebElement radio : radios) {
            WebElement label = radio.findElement(By.xpath("./ancestor::label[1]"));

            if (label.getText().contains(mucDo)) {
                traSachPage.nhapMucDoHuHong(radio.getAttribute("value"));
                return;
            }
        }

        throw new AssertionError("Không tìm thấy mức độ hư hỏng: " + mucDo);
    }


    private void nhapMoTaHuHong(String moTa) {
        traSachPage.nhapMoTaHuHong(moTa);
    }

    private void waitPopupXacNhanTraDong() {
        wait.until(driver -> {
            List<WebElement> popups = Constant.WEBDRIVER.findElements(By.id("popup-return-confirm"));

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

    // =========================
    // FINDERS
    // =========================

    private WebElement findReturnRowByBookCode(String bookCode, String trangThai) {
        return traSachPage.getRowsMuon().stream()
                .filter(row -> isSameBook(row, bookCode))
                .filter(row -> getTrangThai(row).equals(trangThai))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không tìm thấy phiếu mượn với mã sách: " + bookCode + " và trạng thái: " + trangThai
                ));
    }

    private WebElement findReturnRowByLoanId(String loanId) {
        return traSachPage.getRowsMuon().stream()
                .filter(row -> loanId.equals(row.getAttribute("data-loan-id")))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không tìm thấy phiếu mượn với Loan ID: " + loanId
                ));
    }

    private WebElement findValidReturnRow() {
        return traSachPage.getRowsMuon().stream()
                .filter(row -> {
                    String trangThai = getTrangThai(row);
                    boolean hasXacNhanBtn = !row.findElements(By.cssSelector(".return-confirm-trigger")).isEmpty();

                    return hasXacNhanBtn
                            && (trangThai.equals(TRANG_THAI_DANG_MUON)
                            || trangThai.equals(TRANG_THAI_QUA_HAN));
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Không tìm thấy bản ghi hợp lệ để xác nhận trả. " +
                                "Cần có ít nhất 1 bản ghi trạng thái Đang mượn hoặc Quá hạn và có nút Xác nhận trả."
                ));
    }

    private WebElement findOverdueRow() {
        List<WebElement> rows = traSachPage.getRowsMuon();

        return rows.stream()
                .filter(row -> getTrangThai(row).equals(TRANG_THAI_QUA_HAN))
                .filter(row -> !row.findElements(By.cssSelector(".return-confirm-trigger")).isEmpty())
                .findFirst()
                .orElseThrow(() -> new AssertionError(buildNoOverdueDataMessage(rows)));
    }

    private boolean isSameBook(WebElement row, String bookCode) {
        String rowCode = row.getAttribute("data-book-code");

        if (rowCode == null || bookCode == null) {
            return false;
        }

        return bookCode.equals(rowCode)
                || bookCode.contains(rowCode)
                || rowCode.contains(bookCode);
    }

    // =========================
    // ASSERT HELPERS / UTILS
    // =========================

    private void assertReturnSuccessMessage() {
        Assert.assertTrue(
                traSachPage.getSuccessMessage().contains(MSG_TRA_SACH_THANH_CONG),
                "Xác nhận trả sách không thành công"
        );
    }

    private void assertPopupContains(String text, String errorMessage) {
        Assert.assertTrue(
                traSachPage.getPopupXacNhanTra().getText().contains(text),
                errorMessage
        );
    }

    private void assertPopupForOverdue() {
        assertPopupContains(
                "Số ngày quá hạn",
                "Popup phải hiển thị số ngày quá hạn"
        );

        assertPopupContains(
                "Mức phạt mỗi ngày",
                "Popup phải hiển thị mức phạt mỗi ngày"
        );

        assertPopupContains(
                "Phí phạt trễ hạn tạm tính",
                "Popup phải hiển thị phí phạt trễ hạn tạm tính"
        );

        assertPopupContains(
                "Tổng phí phạt",
                "Popup phải hiển thị tổng phí phạt"
        );
    }

    private void assertPopupThongTinCoBan(WebElement row) {
        String maSach = row.getAttribute("data-book-code");
        String tenSach = row.getAttribute("data-book-title");
        String nguoiMuon = row.getAttribute("data-borrower");
        String hanTra = row.getAttribute("data-due-date");

        Assert.assertEquals(getText(By.id("returnBookCode")), maSach, "Mã sách không khớp");
        Assert.assertEquals(getText(By.id("returnBookTitle")), tenSach, "Tên sách không khớp");
        Assert.assertEquals(getText(By.id("returnBorrower")), nguoiMuon, "Người mượn không khớp");
        Assert.assertEquals(getText(By.id("returnDueDate")), hanTra, "Hạn trả không khớp");

        List<WebElement> tinhTrangOptions = Constant.WEBDRIVER.findElements(By.name("returnCondition"));

        Assert.assertEquals(
                tinhTrangOptions.size(),
                2,
                "Popup phải có 2 lựa chọn tình trạng: Tốt và Hư hỏng"
        );
    }

    private void assertFineCreatedInPaymentTab(String loanId, String loaiPhatExpected) {
        openTabThanhToanPhiPhat();

        List<WebElement> rows = traSachPage.getRowsPhiPhat();

        Assert.assertFalse(
                rows.isEmpty(),
                "Tab Thanh toán phí phạt phải có dữ liệu sau khi phát sinh khoản phạt"
        );

        for (WebElement row : rows) {
            try {
                traSachPage.clickXemChiTietPhiPhatWithWait(row);

                List<WebElement> fineRows = traSachPage.getPopupChiTietKhoanPhatWithWait();

                for (WebElement fineRow : fineRows) {
                    String fineText = fineRow.getText();

                    boolean isCorrectLoan = fineText.contains(loanId);
                    boolean isCorrectType = fineText.contains(loaiPhatExpected);
                    boolean isUnpaid = fineText.contains(TRANG_THAI_PHAT_CHUA_THANH_TOAN);

                    if (isCorrectLoan && isCorrectType && isUnpaid) {
                        closePaymentPopupSafely();
                        return;
                    }
                }

                closePaymentPopupSafely();
            } catch (Exception e) {
                ;
            }
        }

        throw new AssertionError(
                "Không tìm thấy khoản phạt đã tạo trong tab Thanh toán phí phạt. " +
                        "Loan ID: " + loanId +
                        ", Loại phạt: " + loaiPhatExpected +
                        ", Trạng thái cần có: " + TRANG_THAI_PHAT_CHUA_THANH_TOAN
        );
    }

    private void assertBothFinesCreatedInPaymentTab(String loanId) {
        assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_TRE_HAN);
        assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_HU_HONG);
    }
    private void assertBothFinesExistInOnePopup(String loanId, String nguoiMuon) {
        openTabThanhToanPhiPhat();

        wait.until(driver -> !traSachPage.getRowsPhiPhat().isEmpty());
        List<WebElement> rows = traSachPage.getRowsPhiPhat();

        List<WebElement> targetRows = rows.stream()
                .filter(r -> r.getText().contains(nguoiMuon))
                .toList();

        Assert.assertFalse(
                targetRows.isEmpty(),
                "Không tìm thấy người mượn '" + nguoiMuon + "' trong danh sách phí phạt tổng quát."
        );

        boolean isFoundAndValid = false;

        for (WebElement targetRow : targetRows) {
            try {
                ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", targetRow);
                traSachPage.clickXemChiTietPhiPhatWithWait(targetRow);

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-payment")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("paymentTableBody")));

                List<WebElement> fineRows = traSachPage.getPopupChiTietKhoanPhatWithWait();

                boolean containsTargetLoan = fineRows.stream().anyMatch(r -> r.getText().contains(loanId));

                if (containsTargetLoan) {
                    int countFines = 0;
                    boolean hasTreHan = false;
                    boolean hasHuHong = false;

                    for (WebElement fineRow : fineRows) {
                        String text = fineRow.getText();

                        if (text.contains(loanId) && text.contains(TRANG_THAI_PHAT_CHUA_THANH_TOAN)) {
                            if (text.contains(LOAI_PHAT_TRE_HAN) && !hasTreHan) {
                                hasTreHan = true;
                                countFines++;
                            } else if (text.contains(LOAI_PHAT_HU_HONG) && !hasHuHong) {
                                hasHuHong = true;
                                countFines++;
                            }
                        }
                    }

                    if (countFines == 2) {
                        isFoundAndValid = true;
                        break;
                    }
                }
            } finally {
                closePaymentPopupSafely();

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }

        Assert.assertTrue(
                isFoundAndValid,
                "Đã tìm kiếm các khoản phạt của '" + nguoiMuon + "' nhưng không thấy đủ 2 loại phạt (Trễ hạn & Hư hỏng) cho mã phiếu: " + loanId
        );
    }
    private void closePaymentPopupSafely() {
        try {
            traSachPage.closePopupChiTietPhiPhat();

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
        } catch (Exception ignored) {
            List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
                    By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') or contains(text(), 'Đóng') or contains(text(), 'Hủy')]")
            );

            if (!closeButtons.isEmpty()) {
                try {
                    closeButtons.get(0).click();
                } catch (Exception e) {
                    ((JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("arguments[0].click();", closeButtons.get(0));
                }
            }
        }
    }

    private boolean isActiveReturnRecordStillExists(String loanId, String bookCode) {
        return Constant.WEBDRIVER.findElements(
                        By.cssSelector(".return-record[data-loan-id='" + loanId + "'][data-book-code='" + bookCode + "']")
                )
                .stream()
                .anyMatch(row -> !getTrangThai(row).equals(TRANG_THAI_DA_TRA));
    }

    private String getTrangThai(WebElement row) {
        return row.findElement(By.cssSelector(".col-status"))
                .getText()
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    private String getText(By locator) {
        return Constant.WEBDRIVER.findElement(locator).getText().trim();
    }

    private String buildNoOverdueDataMessage(List<WebElement> rows) {
        StringBuilder message = new StringBuilder();

        message.append("Không tìm thấy dữ liệu quá hạn để chạy test.\n");
        message.append("Điều kiện bắt buộc: cần ít nhất 1 phiếu mượn có trạng thái Quá hạn và có nút Xác nhận trả.\n");
        message.append("Số dòng Selenium đọc được: ").append(rows.size()).append("\n");
        message.append("Danh sách trạng thái hiện có:\n");

        for (int i = 0; i < rows.size(); i++) {
            WebElement row = rows.get(i);

            String loanId = row.getAttribute("data-loan-id");
            String bookCode = row.getAttribute("data-book-code");
            String status;

            try {
                status = getTrangThai(row);
            } catch (Exception e) {
                status = "Không đọc được trạng thái";
            }

            boolean hasConfirmButton = !row.findElements(By.cssSelector(".return-confirm-trigger")).isEmpty();

            message.append("Dòng ")
                    .append(i + 1)
                    .append(" | loanId: ")
                    .append(loanId)
                    .append(" | bookCode: ")
                    .append(bookCode)
                    .append(" | status: ")
                    .append(status)
                    .append(" | hasConfirmButton: ")
                    .append(hasConfirmButton)
                    .append("\n");
        }

        return message.toString();
    }

    private void waitForReturnRows() {
        wait.until(driver -> !traSachPage.getRowsMuon().isEmpty());
    }

    private void waitForReturnRowsOrEmptyPage() {
        wait.until(driver ->
                (Constant.WEBDRIVER.getPageSource() != null
                        && Constant.WEBDRIVER.getPageSource().contains("Xác nhận trả"))
                        || !traSachPage.getRowsMuon().isEmpty()
        );
    }

    // =========================
    // TEST CASES
    // =========================

    @Test
    public void TC_TS_01_hienThiDanhSachXacNhanTra() {
        List<WebElement> rows = traSachPage.getRowsMuon();

        Assert.assertFalse(rows.isEmpty(), "Danh sách xác nhận trả không được rỗng");

        for (WebElement row : rows) {
            String trangThai = getTrangThai(row);
            boolean hasXacNhanBtn = !row.findElements(By.cssSelector(".return-confirm-trigger")).isEmpty();

            if (trangThai.equals(TRANG_THAI_DANG_MUON) || trangThai.equals(TRANG_THAI_QUA_HAN)) {
                Assert.assertTrue(
                        hasXacNhanBtn,
                        "Bản ghi hợp lệ phải có nút Xác nhận trả. Trạng thái: " + trangThai
                );
            } else {
                Assert.assertFalse(
                        hasXacNhanBtn,
                        "Bản ghi không hợp lệ không được có nút Xác nhận trả. Trạng thái: " + trangThai
                );
            }
        }
    }

    @Test
    public void TC_TS_02_moPopupXacNhanTra() {
        WebElement row = findValidReturnRow();

        openPopupXacNhanTra(row);
        assertPopupThongTinCoBan(row);
    }

    @Test
    public void TC_TS_03_loiChuaChonTinhTrangSach() {
        WebElement row = findValidReturnRow();

        openPopupXacNhanTra(row);
        traSachPage.clickXacNhan();

        Assert.assertEquals(
                traSachPage.getErrorTinhTrangMessage(),
                ERR_CHUA_CHON_TINH_TRANG,
                "Thông báo lỗi chưa chọn tình trạng không đúng"
        );
    }

    @Test
    public void TC_TS_04_traSachDungHanTinhTrangTot() {
        WebElement row = taoPhieuMuonVaLayDongXacNhanTra();

        String loanId = row.getAttribute("data-loan-id");
        String bookCode = row.getAttribute("data-book-code");

        traSachTinhTrangTot(row);

        Assert.assertFalse(
                isActiveReturnRecordStillExists(loanId, bookCode),
                "Bản ghi sau khi trả không được còn ở trạng thái đang xử lý"
        );
    }

    @Test
    public void TC_TS_05_traSachQuaHan() {
        WebElement row = findOverdueRow();
        String loanId = row.getAttribute("data-loan-id");

        Assert.assertEquals(
                getTrangThai(row),
                TRANG_THAI_QUA_HAN,
                "Bản ghi dùng để test phải có trạng thái Quá hạn"
        );

        openPopupXacNhanTra(row);
        assertPopupForOverdue();

        traSachPage.chonTinhTrangTot();
        traSachPage.clickXacNhan();

        assertReturnSuccessMessage();

        assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_TRE_HAN);
    }

    @Test
    public void TC_TS_06_traSachHuHong() {
        WebElement row = taoPhieuMuonVaLayDongXacNhanTra();
        String loanId = row.getAttribute("data-loan-id");

        traSachHuHong(
                row,
                MUC_HU_HONG_NHE,
                "Rách bìa"
        );

        assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_HU_HONG);
    }

    @Test
    public void TC_TS_07_traSachQuaHanVaHuHong() {
        WebElement row = findOverdueRow();
        String loanId = row.getAttribute("data-loan-id");
        String nguoiMuon = row.getAttribute("data-borrower");

        Assert.assertEquals(
                getTrangThai(row),
                TRANG_THAI_QUA_HAN,
                "Bản ghi dùng để test phải có trạng thái Quá hạn"
        );

        traSachHuHong(
                row,
                MUC_HU_HONG_NANG,
                "Rách bìa, mất trang"
        );

        assertBothFinesExistInOnePopup(loanId, nguoiMuon);
    }

    @Test
    public void TC_TS_08_huyXacNhanTraSach() {
        WebElement row = findValidReturnRow();

        String loanId = row.getAttribute("data-loan-id");
        String bookCode = row.getAttribute("data-book-code");
        String trangThaiTruoc = getTrangThai(row);

        // 1. Mở popup xác nhận trả
        openPopupXacNhanTra(row);

        // 2. Chọn tình trạng sách khi trả
        traSachPage.chonTinhTrangTot();

        // 3. Bấm Hủy
        traSachPage.clickHuy();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("popup-return-cancel-confirm")
        ));

        // 4. Xác nhận hủy thao tác
        traSachPage.xacNhanHuy();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("popup-return-cancel-confirm")
        ));

        waitPopupXacNhanTraDong();

        refreshXacNhanTraTab();

        WebElement rowSauHuy = findReturnRowByLoanId(loanId);
        String trangThaiSau = getTrangThai(rowSauHuy);

        Assert.assertEquals(
                trangThaiSau,
                trangThaiTruoc,
                "Sau khi hủy xác nhận trả, trạng thái phiếu mượn phải giữ nguyên"
        );

        Assert.assertTrue(
                isActiveReturnRecordStillExists(loanId, bookCode),
                "Sau khi hủy, bản ghi vẫn phải còn trong danh sách chờ xác nhận trả"
        );
    }
}