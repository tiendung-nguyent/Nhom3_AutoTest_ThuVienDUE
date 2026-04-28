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

		traSachPage.getTabXacNhanTra().click();
		waitForReturnRowsOrEmptyPage();
	}

	private void openTabThanhToanPhiPhat() {
		SidebarPage sidebarPage = new SidebarPage();

		/*
		 * Không dùng /quanlyphat vì route này không tồn tại.
		 * Đi đúng luồng UI: Sidebar Trả sách -> tab Thanh toán phí phạt.
		 */
		sidebarPage.gotoReturn();

		WebElement tabThanhToanPhiPhat = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//*[contains(normalize-space(), 'Thanh toán phí phạt')]")
		));

		((JavascriptExecutor) Constant.WEBDRIVER)
				.executeScript("arguments[0].scrollIntoView({block: 'center'});", tabThanhToanPhiPhat);

		tabThanhToanPhiPhat.click();

		wait.until(driver ->
				Constant.WEBDRIVER.getPageSource().contains("Thanh toán phí phạt")
						|| Constant.WEBDRIVER.getPageSource().contains("phí phạt")
						|| Constant.WEBDRIVER.getPageSource().contains("Chưa thanh toán")
						|| Constant.WEBDRIVER.getPageSource().contains("Đã thanh toán")
		);
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
		((JavascriptExecutor) Constant.WEBDRIVER)
				.executeScript("arguments[0].scrollIntoView({block: 'center'});", row);

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
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//label[contains(normalize-space(), '" + mucDo + "')]")
		));

		((JavascriptExecutor) Constant.WEBDRIVER)
				.executeScript("arguments[0].scrollIntoView({block: 'center'});", option);

		try {
			option.click();
		} catch (Exception e) {
			((JavascriptExecutor) Constant.WEBDRIVER)
					.executeScript("arguments[0].click();", option);
		}
	}

	private void nhapMoTaHuHong(String moTa) {
		WebElement textArea = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//textarea[contains(@placeholder, 'Nhập mô tả tình trạng sách khi trả')]")
		));

		((JavascriptExecutor) Constant.WEBDRIVER)
				.executeScript("arguments[0].scrollIntoView({block: 'center'});", textArea);

		textArea.clear();
		textArea.sendKeys(moTa);
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

	/*
	 * Kiểm tra khoản phạt trong tab Thanh toán phí phạt.
	 * Cách này không phụ thuộc selector .phat[data-idmuon].
	 * Nó kiểm tra theo nội dung đang hiển thị trong tab hoặc popup chi tiết.
	 */
	private void assertFineCreatedInPaymentTab(String loanId, String loaiPhatExpected) {
		openTabThanhToanPhiPhat();

		String bodyText = Constant.WEBDRIVER.findElement(By.tagName("body")).getText();

		if (bodyText.contains(loanId)
				&& bodyText.contains(loaiPhatExpected)
				&& bodyText.contains(TRANG_THAI_PHAT_CHUA_THANH_TOAN)) {
			return;
		}

		List<WebElement> detailButtons = Constant.WEBDRIVER.findElements(
				By.xpath("//button[contains(normalize-space(), 'Xem') " +
						"or contains(normalize-space(), 'Chi tiết') " +
						"or contains(normalize-space(), 'Thanh toán')]")
		);

		for (int i = 0; i < detailButtons.size(); i++) {
			detailButtons = Constant.WEBDRIVER.findElements(
					By.xpath("//button[contains(normalize-space(), 'Xem') " +
							"or contains(normalize-space(), 'Chi tiết') " +
							"or contains(normalize-space(), 'Thanh toán')]")
			);

			if (i >= detailButtons.size()) {
				break;
			}

			WebElement button = detailButtons.get(i);

			((JavascriptExecutor) Constant.WEBDRIVER)
					.executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

			try {
				button.click();
			} catch (Exception e) {
				((JavascriptExecutor) Constant.WEBDRIVER)
						.executeScript("arguments[0].click();", button);
			}

			wait.until(driver -> Constant.WEBDRIVER.findElement(By.tagName("body")).getText().contains("phạt")
					|| Constant.WEBDRIVER.findElement(By.tagName("body")).getText().contains("Phạt")
					|| Constant.WEBDRIVER.findElement(By.tagName("body")).getText().contains("Mã phiếu"));

			String detailText = Constant.WEBDRIVER.findElement(By.tagName("body")).getText();

			if (detailText.contains(loanId)
					&& detailText.contains(loaiPhatExpected)
					&& detailText.contains(TRANG_THAI_PHAT_CHUA_THANH_TOAN)) {
				closeAnyPopupIfVisible();
				return;
			}

			closeAnyPopupIfVisible();
		}

		throw new AssertionError(
				"Không tìm thấy khoản phạt đã tạo trong tab Thanh toán phí phạt. " +
						"Loan ID: " + loanId +
						", Loại phạt cần tìm: " + loaiPhatExpected +
						", Trạng thái cần có: " + TRANG_THAI_PHAT_CHUA_THANH_TOAN +
						". Có thể tab Thanh toán phí phạt không hiển thị mã phiếu mượn hoặc selector nút chi tiết chưa đúng."
		);
	}

	private void assertBothFinesCreatedInPaymentTab(String loanId) {
		assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_TRE_HAN);
		assertFineCreatedInPaymentTab(loanId, LOAI_PHAT_HU_HONG);
	}

	private void closeAnyPopupIfVisible() {
		List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
				By.xpath("//button[contains(normalize-space(), 'Đóng') " +
						"or contains(normalize-space(), 'Hủy') " +
						"or contains(normalize-space(), '×') " +
						"or contains(@class, 'close')]")
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

		assertBothFinesCreatedInPaymentTab(loanId);
	}

	@Test
	public void TC_TS_08_huyXacNhanTraSach() {
		WebElement row = taoPhieuMuonVaLayDongXacNhanTra();

		String loanId = row.getAttribute("data-loan-id");
		String bookCode = row.getAttribute("data-book-code");
		String trangThaiTruoc = getTrangThai(row);

		Assert.assertEquals(
				trangThaiTruoc,
				TRANG_THAI_DANG_MUON,
				"Bản ghi mới tạo phải ở trạng thái Đang mượn trước khi test hủy"
		);

		openPopupXacNhanTra(row);

		/*
		 * Chọn tình trạng để hệ thống ghi nhận có thay đổi,
		 * khi bấm Hủy mới hiển thị popup xác nhận hủy thao tác.
		 */
		traSachPage.chonTinhTrangTot();

		traSachPage.clickHuy();

		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.id("popup-return-cancel-confirm")
		));

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
				TRANG_THAI_DANG_MUON,
				"Sau khi hủy, bản ghi phải giữ nguyên trạng thái Đang mượn"
		);

		Assert.assertTrue(
				isActiveReturnRecordStillExists(loanId, bookCode),
				"Sau khi hủy, bản ghi vẫn phải còn trong danh sách chờ trả"
		);

		/*
		 * Dọn dữ liệu để phiếu mượn mới tạo không bị treo.
		 * Nếu dọn lỗi thì không làm fail TC08 vì TC08 đã kiểm tra xong mục tiêu chính.
		 */
		try {
			WebElement rowCanDon = findReturnRowByLoanId(loanId);
			traSachTinhTrangTot(rowCanDon);
		} catch (Throwable ignored) {
			System.out.println("Không dọn được dữ liệu sau TC_TS_08. Loan ID: " + loanId);
		}
	}
}