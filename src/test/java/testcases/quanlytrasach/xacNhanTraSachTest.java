package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.By;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class XacNhanTraSachTest {
	private static final Logger LOGGER = Logger.getLogger(XacNhanTraSachTest.class.getName());
				   private WebDriverWait wait;
				   private quanLyTraSachPage traSachPage;

	@BeforeMethod
	public void setup() {
		if (Constant.WEBDRIVER == null) {
			Constant.WEBDRIVER = new ChromeDriver();
			Constant.WEBDRIVER.manage().window().maximize();
		}
		wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
		traSachPage = new quanLyTraSachPage();
		HomePage homePage = new HomePage();
		homePage.open();
		LoginPage loginPage = homePage.gotoLoginPage();
		loginPage.login(Constant.USERNAME, Constant.PASSWORD);
		wait.until(ExpectedConditions.urlContains("dashboard"));
		SidebarPage sidebar = new SidebarPage();
		sidebar.gotoReturn();
		traSachPage.getTabXacNhanTra().click();
	}

	@AfterMethod
	public void tearDown() {
		if (Constant.WEBDRIVER != null) {
			Constant.WEBDRIVER.quit();
			Constant.WEBDRIVER = null;
		}
	}


	/**
	 * TC_TS_01: Hiển thị đúng danh sách bản ghi được phép xác nhận trả
	 */
	@Test
	public void hienThiDanhSachXacNhanTra() {
		try {
			List<WebElement> rows = traSachPage.getRowsMuon();
			Assert.assertTrue(!rows.isEmpty(), "Không có bản ghi nào để xác nhận trả! Số lượng: " + rows.size());
			for (WebElement row : rows) {
				String trangThai = row.findElement(By.cssSelector(".trang-thai")).getText();
				boolean hasXacNhanBtn = !row.findElements(By.cssSelector(".btn-xacnhantra")).isEmpty();
				if (trangThai.equals("Đang mượn") || trangThai.equals("Quá hạn")) {
					Assert.assertTrue(hasXacNhanBtn, "Bản ghi hợp lệ phải có nút Xác nhận trả. Trạng thái: " + trangThai + ", Có nút: " + hasXacNhanBtn);
				} else {
					Assert.assertFalse(hasXacNhanBtn, "Bản ghi không hợp lệ không được phép thao tác. Trạng thái: " + trangThai + ", Có nút: " + hasXacNhanBtn);
				}
			}
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra danh sách xác nhận trả", t);
			Assert.fail("Lỗi khi kiểm tra danh sách xác nhận trả: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_02: Mở popup xác nhận trả và hiển thị đúng thông tin
	 */
	@Test
	public void moPopupVaHienThiThongTin() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> {
					String tt = r.findElement(By.cssSelector(".trang-thai")).getText();
					return tt.equals("Đang mượn") || tt.equals("Quá hạn");
				})
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi hợp lệ!"));
			String maSach = row.findElement(By.cssSelector(".ma-sach")).getText();
			String tenSach = row.findElement(By.cssSelector(".ten-sach")).getText();
			String nguoiMuon = row.findElement(By.cssSelector(".nguoi-muon")).getText();
			String hanTra = row.findElement(By.cssSelector(".han-tra")).getText();
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			Assert.assertEquals(Constant.WEBDRIVER.findElement(By.id("popup-ma-sach")).getText(), maSach, "Mã sách không khớp");
			Assert.assertEquals(Constant.WEBDRIVER.findElement(By.id("popup-ten-sach")).getText(), tenSach, "Tên sách không khớp");
			Assert.assertEquals(Constant.WEBDRIVER.findElement(By.id("popup-nguoi-muon")).getText(), nguoiMuon, "Người mượn không khớp");
			Assert.assertEquals(Constant.WEBDRIVER.findElement(By.id("popup-han-tra")).getText(), hanTra, "Hạn trả không khớp");
			List<WebElement> tinhTrangOptions = Constant.WEBDRIVER.findElements(By.name("tinhtrang"));
			Assert.assertEquals(tinhTrangOptions.size(), 2, "Có 2 lựa chọn: Tốt và Hư hỏng, thực tế: " + tinhTrangOptions.size());
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra popup xác nhận trả", t);
			Assert.fail("Lỗi khi kiểm tra popup xác nhận trả: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_03: Báo lỗi khi chưa chọn tình trạng sách khi trả
	 */
	@Test
	public void TC_TS_03_loiChuaChonTinhTrang() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> r.findElement(By.cssSelector(".trang-thai")).getText().equals("Đang mượn"))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi Đang mượn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.getBtnXacNhan().click();
			String error = traSachPage.getErrorTinhTrangMessage();
			Assert.assertEquals(error, "Vui lòng chọn tình trạng sách khi trả", "Thông báo lỗi không đúng: " + error);
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra bắt buộc chọn tình trạng sách", t);
			Assert.fail("Lỗi khi kiểm tra bắt buộc chọn tình trạng sách: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_04: Xác nhận trả thành công với sách đúng hạn, tình trạng tốt
	 */
	@Test
	public void TC_TS_04_traSachDungHanTinhTrangTot() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> "Đang mượn".equals(r.findElement(By.cssSelector(".trang-thai")).getText()) &&
						"true".equals(r.getAttribute("data-dunghan")))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi đúng hạn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.chonTinhTrangTot();
			traSachPage.clickXacNhan();
			String msg = traSachPage.getSuccessMessage();
			Assert.assertTrue(msg.contains("Xác nhận trả sách thành công"), "Thông báo thành công không đúng: " + msg);
			List<WebElement> rows = traSachPage.getRowsMuon();
			boolean stillExist = rows.stream().anyMatch(r -> {
				String id1 = r.getAttribute("data-id");
				String id2 = row.getAttribute("data-id");
				return id1 != null && id1.equals(id2);
			});
			Assert.assertFalse(stillExist, "Bản ghi đã trả không còn hiển thị");
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi xác nhận trả sách đúng hạn, tình trạng tốt", t);
			Assert.fail("Lỗi khi xác nhận trả sách đúng hạn, tình trạng tốt: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_05: Xác nhận trả thành công với sách quá hạn
	 */
	@Test
	public void TC_TS_05_traSachQuaHan() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> r.findElement(By.cssSelector(".trang-thai")).getText().equals("Quá hạn"))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi quá hạn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.chonTinhTrangTot();
			traSachPage.clickXacNhan();
			String msg = traSachPage.getSuccessMessage();
			Assert.assertTrue(msg.contains("Xác nhận trả sách thành công"), "Thông báo thành công không đúng: " + msg);
			// Kiểm tra phạt
			Constant.WEBDRIVER.get("http://localhost:8000/quanlyphat");
			WebElement phat = Constant.WEBDRIVER.findElement(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
			Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán", "Trạng thái phạt không đúng");
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi xác nhận trả sách quá hạn", t);
			Assert.fail("Lỗi khi xác nhận trả sách quá hạn: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_06: Xác nhận trả thành công với sách hư hỏng
	 */
	@Test
	public void TC_TS_06_traSachHuHong() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> r.findElement(By.cssSelector(".trang-thai")).getText().equals("Đang mượn"))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi Đang mượn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.chonTinhTrangHuHong();
			traSachPage.nhapMucDoHuHong("Nặng");
			traSachPage.nhapMoTaHuHong("Rách bìa");
			traSachPage.clickXacNhan();
			String msg = traSachPage.getSuccessMessage();
			Assert.assertTrue(msg.contains("Xác nhận trả sách thành công"), "Thông báo thành công không đúng: " + msg);
			Constant.WEBDRIVER.get("http://localhost:8000/quanlyphat");
			WebElement phat = Constant.WEBDRIVER.findElement(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
			Assert.assertEquals(phat.findElement(By.cssSelector(".loai-phat")).getText(), "Hư hỏng", "Loại phạt không đúng");
			Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán", "Trạng thái phạt không đúng");
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi xác nhận trả sách hư hỏng", t);
			Assert.fail("Lỗi khi xác nhận trả sách hư hỏng: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_07: Xác nhận trả thành công với sách vừa quá hạn vừa hư hỏng
	 */
	@Test
	public void TC_TS_07_traSachQuaHanVaHuHong() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> r.findElement(By.cssSelector(".trang-thai")).getText().equals("Quá hạn"))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi quá hạn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.chonTinhTrangHuHong();
			traSachPage.nhapMucDoHuHong("Nặng");
			traSachPage.nhapMoTaHuHong("Rách bìa, mất trang");
			traSachPage.clickXacNhan();
			String msg = traSachPage.getSuccessMessage();
			Assert.assertTrue(msg.contains("Xác nhận trả sách thành công"), "Thông báo thành công không đúng: " + msg);
			Constant.WEBDRIVER.get("http://localhost:8000/quanlyphat");
			List<WebElement> phats = Constant.WEBDRIVER.findElements(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
			boolean hasTreHan = false, hasHuHong = false;
			for (WebElement phat : phats) {
				String loai = phat.findElement(By.cssSelector(".loai-phat")).getText();
				if (loai.equals("Trễ hạn")) hasTreHan = true;
				if (loai.equals("Hư hỏng")) hasHuHong = true;
				Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán", "Trạng thái phạt không đúng");
			}
			Assert.assertTrue(hasTreHan && hasHuHong, "Phải có cả phạt trễ hạn và hư hỏng");
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi xác nhận trả sách quá hạn và hư hỏng", t);
			Assert.fail("Lỗi khi xác nhận trả sách quá hạn và hư hỏng: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_08: Hủy thao tác xác nhận trả sách
	 */
	@Test
	public void TC_TS_08_huyXacNhanTraSach() {
		try {
			WebElement row = traSachPage.getRowsMuon().stream()
				.filter(r -> r.findElement(By.cssSelector(".trang-thai")).getText().equals("Đang mượn"))
				.findFirst().orElseThrow(() -> new AssertionError("Không có bản ghi Đang mượn!"));
			traSachPage.clickXacNhanTra(row);
			wait.until(ExpectedConditions.visibilityOf(traSachPage.getPopupXacNhanTra()));
			traSachPage.clickHuy();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra-huy")));
			traSachPage.xacNhanHuy();
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-xacnhantra")));
			List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector(".row-muon[data-id='" + row.getAttribute("data-id") + "']"));
			Assert.assertEquals(rows.size(), 1, "Bản ghi vẫn còn trong danh sách");
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi hủy xác nhận trả sách", t);
			Assert.fail("Lỗi khi hủy xác nhận trả sách: " + t.getMessage());
		}
	}

	/**
	 * TC_TS_09: Không cho phép xác nhận trả với bản ghi không hợp lệ hoặc đã được xử lý
	 */
	@Test
	public void TC_TS_09_khongChoXacNhanTraKhongHopLe() {
		try {
			List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector(".row-muon[data-trangthai='Đã trả']"));
			if (!rows.isEmpty()) {
				WebElement row = rows.get(0);
				boolean hasBtn = !row.findElements(By.cssSelector(".btn-xacnhantra")).isEmpty();
				Assert.assertFalse(hasBtn, "Không được phép xác nhận trả với bản ghi đã trả");
			}
			Constant.WEBDRIVER.get("http://localhost:8000/quanlytrasach/xacnhan?id=999999");
			WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger, .alert-warning")));
			Assert.assertTrue(alert.getText().contains("Không tìm thấy bản ghi mượn") || alert.getText().contains("không còn hợp lệ"), "Thông báo lỗi không đúng: " + alert.getText());
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra xác nhận trả với bản ghi không hợp lệ", t);
			Assert.fail("Lỗi khi kiểm tra xác nhận trả với bản ghi không hợp lệ: " + t.getMessage());
		}
	}
}
