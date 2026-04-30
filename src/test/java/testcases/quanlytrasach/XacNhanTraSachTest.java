package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
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

		wait.until(ExpectedConditions.elementToBeClickable(traSachPage.getTabXacNhanTra()));
		traSachPage.getTabXacNhanTra().click();

		waitForReturnRowsOrEmptyPage();
	}

	private void openTabThanhToanPhiPhat() {
		waitGlobalPopupOverlayClosed();

		if (!Constant.WEBDRIVER.getCurrentUrl().contains("/return-books")) {
			SidebarPage sidebarPage = new SidebarPage();
			sidebarPage.gotoReturn();
		}

		waitGlobalPopupOverlayClosed();
		traSachPage.openTabThanhToanPhiPhat();
	}

	private void refreshXacNhanTraTab() {
		waitGlobalPopupOverlayClosed();
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

	private void waitReturnPopupAndOverlayClosed() {
		waitPopupXacNhanTraDong();
		waitGlobalPopupOverlayClosed();
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

		waitReturnPopupAndOverlayClosed();
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
				closePaymentPopupSafely();
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
		openTabThanhToanPhiPhat();

		List<WebElement> rows = traSachPage.getRowsPhiPhat();

		Assert.assertFalse(
				rows.isEmpty(),
				"Tab Thanh toán phí phạt phải có dữ liệu sau khi phát sinh khoản phạt"
		);

		boolean foundTreHan = false;
		boolean foundHuHong = false;

		for (WebElement row : rows) {
			try {
				traSachPage.clickXemChiTietPhiPhatWithWait(row);

				List<WebElement> fineRows = traSachPage.getPopupChiTietKhoanPhatWithWait();

				for (WebElement fineRow : fineRows) {
					String fineText = fineRow.getText();

					boolean isCorrectLoan = fineText.contains(loanId);
					boolean isUnpaid = fineText.contains(TRANG_THAI_PHAT_CHUA_THANH_TOAN);

					if (isCorrectLoan && isUnpaid && fineText.contains(LOAI_PHAT_TRE_HAN)) {
						foundTreHan = true;
					}

					if (isCorrectLoan && isUnpaid && fineText.contains(LOAI_PHAT_HU_HONG)) {
						foundHuHong = true;
					}
				}

				closePaymentPopupSafely();

				if (foundTreHan && foundHuHong) {
					return;
				}

			} catch (Exception e) {
				closePaymentPopupSafely();
			}
		}

		Assert.assertTrue(
				foundTreHan,
				"Không tìm thấy khoản phạt Trễ hạn với Loan ID: " + loanId
		);

		Assert.assertTrue(
				foundHuHong,
				"Không tìm thấy khoản phạt Hư hỏng với Loan ID: " + loanId
		);
	}

	// =========================
	// POPUP / OVERLAY HELPERS
	// =========================

	private boolean isGlobalPopupOverlayDisplayed() {
		List<WebElement> overlays = Constant.WEBDRIVER.findElements(By.id("popupOverlay"));

		if (overlays.isEmpty()) {
			return false;
		}

		try {
			WebElement overlay = overlays.get(0);
			String display = overlay.getCssValue("display");
			String style = overlay.getAttribute("style");

			return overlay.isDisplayed()
					&& !"none".equalsIgnoreCase(display)
					&& (style == null || !style.contains("display: none"));
		} catch (Exception e) {
			return false;
		}
	}

	private void waitGlobalPopupOverlayClosed() {
		try {
			wait.until(driver -> !isGlobalPopupOverlayDisplayed());
		} catch (TimeoutException e) {
			forceCloseBlockingPopups();
			wait.until(driver -> !isGlobalPopupOverlayDisplayed());
		}
	}

	private void forceCloseBlockingPopups() {
		try {
			((JavascriptExecutor) Constant.WEBDRIVER).executeScript(
					"var ids = [" +
							"'popupOverlay'," +
							"'popup-return-confirm'," +
							"'popup-return-cancel-confirm'," +
							"'popup-payment'," +
							"'popup-xacnhan-huy-thanhtoan'" +
							"];" +
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

	private boolean isCancelPaymentConfirmPopupDisplayed() {
		List<WebElement> popups = Constant.WEBDRIVER.findElements(
				By.xpath(
						"//*[contains(normalize-space(), 'Chưa hoàn tất thanh toán') " +
								"or contains(normalize-space(), 'Xác nhận hủy')]"
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

	private void confirmCancelPaymentPopupIfDisplayed() {
		if (!isCancelPaymentConfirmPopupDisplayed()) {
			return;
		}

		List<WebElement> exitButtons = Constant.WEBDRIVER.findElements(
				By.xpath(
						"//button[contains(normalize-space(), 'Thoát thanh toán') " +
								"or contains(normalize-space(), 'Thoát') " +
								"or contains(normalize-space(), 'Hủy thanh toán')]"
				)
		);

		if (exitButtons.isEmpty()) {
			throw new AssertionError(
					"Popup xác nhận hủy thanh toán đang hiển thị nhưng không tìm thấy nút 'Thoát thanh toán'."
			);
		}

		WebElement exitButton = exitButtons.get(0);

		try {
			wait.until(ExpectedConditions.elementToBeClickable(exitButton)).click();
		} catch (Exception e) {
			((JavascriptExecutor) Constant.WEBDRIVER)
					.executeScript("arguments[0].click();", exitButton);
		}

		try {
			wait.until(driver -> !isCancelPaymentConfirmPopupDisplayed());
		} catch (Exception e) {
			forceCloseBlockingPopups();
		}

		waitGlobalPopupOverlayClosed();
	}

	private void closePaymentPopupSafely() {
		try {
			confirmCancelPaymentPopupIfDisplayed();

			List<WebElement> paymentPopups = Constant.WEBDRIVER.findElements(By.id("popup-payment"));

			if (paymentPopups.isEmpty()) {
				waitGlobalPopupOverlayClosed();
				return;
			}

			try {
				if (!paymentPopups.get(0).isDisplayed()) {
					waitGlobalPopupOverlayClosed();
					return;
				}
			} catch (Exception e) {
				waitGlobalPopupOverlayClosed();
				return;
			}

			List<WebElement> closeButtons = Constant.WEBDRIVER.findElements(
					By.xpath("//div[@id='popup-payment']//button[contains(text(), '×')]")
			);

			if (!closeButtons.isEmpty()) {
				try {
					wait.until(ExpectedConditions.elementToBeClickable(closeButtons.get(0))).click();
				} catch (Exception e) {
					((JavascriptExecutor) Constant.WEBDRIVER)
							.executeScript("arguments[0].click();", closeButtons.get(0));
				}

				/*
				 * Quan trọng:
				 * UI của bạn bấm X cũng hiện popup xác nhận hủy thanh toán.
				 */
				confirmCancelPaymentPopupIfDisplayed();

			} else {
				List<WebElement> cancelButtons = Constant.WEBDRIVER.findElements(
						By.xpath("//div[@id='popup-payment']//button[normalize-space()='Hủy']")
				);

				if (!cancelButtons.isEmpty()) {
					try {
						wait.until(ExpectedConditions.elementToBeClickable(cancelButtons.get(0))).click();
					} catch (Exception e) {
						((JavascriptExecutor) Constant.WEBDRIVER)
								.executeScript("arguments[0].click();", cancelButtons.get(0));
					}

					confirmCancelPaymentPopupIfDisplayed();
				}
			}

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

			waitGlobalPopupOverlayClosed();

		} catch (Exception e) {
			try {
				confirmCancelPaymentPopupIfDisplayed();
				forceCloseBlockingPopups();
			} catch (Exception ignored) {
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
		waitGlobalPopupOverlayClosed();

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

		try {
			WebElement rowCanDon = findReturnRowByLoanId(loanId);
			traSachTinhTrangTot(rowCanDon);
		} catch (Throwable ignored) {
			System.out.println("Không dọn được dữ liệu sau TC_TS_08. Loan ID: " + loanId);
		}
	}
}