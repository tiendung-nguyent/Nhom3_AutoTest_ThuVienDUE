package testcases.quanlytrasach;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.List;

public class xacNhanTraSachTest {
	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeClass
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) driver.quit();
	}

	private void loginAsThuThu() {
		driver.get("http://localhost:8000/login");
		driver.findElement(By.id("username")).sendKeys("thuthu");
		driver.findElement(By.id("password")).sendKeys("password");
		driver.findElement(By.id("login-btn")).click();
		wait.until(ExpectedConditions.urlContains("dashboard"));
	}

	private void goToXacNhanTraSachTab() {
		driver.get("http://localhost:8000/quanlytrasach");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tab-xacnhantra"))).click();
	}

	/**
	 * TC_TS_01: Hiển thị đúng danh sách bản ghi được phép xác nhận trả
	 */
	@Test
	public void TC_TS_01_hienThiDanhSachXacNhanTra() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		List<WebElement> rows = driver.findElements(By.cssSelector(".row-muon"));
		for (WebElement row : rows) {
			String trangThai = row.findElement(By.cssSelector(".trang-thai")).getText();
			boolean hasXacNhanBtn = row.findElements(By.cssSelector(".btn-xacnhantra")).size() > 0;
			if (trangThai.equals("Đang mượn") || trangThai.equals("Quá hạn")) {
				Assert.assertTrue(hasXacNhanBtn, "Bản ghi hợp lệ phải có nút Xác nhận trả");
			} else {
				Assert.assertFalse(hasXacNhanBtn, "Bản ghi không hợp lệ không được phép thao tác");
			}
		}
	}

	/**
	 * TC_TS_02: Mở popup xác nhận trả và hiển thị đúng thông tin
	 */
	@Test
	public void TC_TS_02_moPopupVaHienThiThongTin() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Đang mượn'], .row-muon[data-trangthai='Quá hạn']"));
		String maSach = row.findElement(By.cssSelector(".ma-sach")).getText();
		String tenSach = row.findElement(By.cssSelector(".ten-sach")).getText();
		String nguoiMuon = row.findElement(By.cssSelector(".nguoi-muon")).getText();
		String hanTra = row.findElement(By.cssSelector(".han-tra")).getText();
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		Assert.assertEquals(driver.findElement(By.id("popup-ma-sach")).getText(), maSach);
		Assert.assertEquals(driver.findElement(By.id("popup-ten-sach")).getText(), tenSach);
		Assert.assertEquals(driver.findElement(By.id("popup-nguoi-muon")).getText(), nguoiMuon);
		Assert.assertEquals(driver.findElement(By.id("popup-han-tra")).getText(), hanTra);
		List<WebElement> tinhTrangOptions = driver.findElements(By.name("tinhtrang"));
		Assert.assertEquals(tinhTrangOptions.size(), 2, "Có 2 lựa chọn: Tốt và Hư hỏng");
	}

	/**
	 * TC_TS_03: Báo lỗi khi chưa chọn tình trạng sách khi trả
	 */
	@Test
	public void TC_TS_03_loiChuaChonTinhTrang() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Đang mượn']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("btn-xacnhan")) .click();
		WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-tinhtrang")));
		Assert.assertEquals(error.getText(), "Vui lòng chọn tình trạng sách khi trả");
	}

	/**
	 * TC_TS_04: Xác nhận trả thành công với sách đúng hạn, tình trạng tốt
	 */
	@Test
	public void TC_TS_04_traSachDungHanTinhTrangTot() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Đang mượn'][data-dunghan='true']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("radio-tot")).click();
		driver.findElement(By.id("btn-xacnhan")).click();
		WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success")));
		Assert.assertTrue(toast.getText().contains("Xác nhận trả sách thành công"));
		List<WebElement> rows = driver.findElements(By.cssSelector(".row-muon[data-id='" + row.getAttribute("data-id") + "']"));
		Assert.assertEquals(rows.size(), 0, "Bản ghi đã trả không còn hiển thị");
	}

	/**
	 * TC_TS_05: Xác nhận trả thành công với sách quá hạn
	 */
	@Test
	public void TC_TS_05_traSachQuaHan() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Quá hạn']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("radio-tot")).click();
		driver.findElement(By.id("btn-xacnhan")).click();
		WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success")));
		Assert.assertTrue(toast.getText().contains("Xác nhận trả sách thành công"));
		driver.get("http://localhost:8000/quanlyphat");
		WebElement phat = driver.findElement(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
		Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán");
	}

	/**
	 * TC_TS_06: Xác nhận trả thành công với sách hư hỏng
	 */
	@Test
	public void TC_TS_06_traSachHuHong() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Đang mượn']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("radio-huhong")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mucdo-huhong")));
		driver.findElement(By.id("mucdo-huhong")).sendKeys("Nặng");
		driver.findElement(By.id("mota-huhong")).sendKeys("Rách bìa");
		driver.findElement(By.id("btn-xacnhan")).click();
		WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success")));
		Assert.assertTrue(toast.getText().contains("Xác nhận trả sách thành công"));
		driver.get("http://localhost:8000/quanlyphat");
		WebElement phat = driver.findElement(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
		Assert.assertEquals(phat.findElement(By.cssSelector(".loai-phat")).getText(), "Hư hỏng");
		Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán");
	}

	/**
	 * TC_TS_07: Xác nhận trả thành công với sách vừa quá hạn vừa hư hỏng
	 */
	@Test
	public void TC_TS_07_traSachQuaHanVaHuHong() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Quá hạn']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("radio-huhong")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mucdo-huhong")));
		driver.findElement(By.id("mucdo-huhong")).sendKeys("Nặng");
		driver.findElement(By.id("mota-huhong")).sendKeys("Rách bìa, mất trang");
		driver.findElement(By.id("btn-xacnhan")).click();
		WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-success")));
		Assert.assertTrue(toast.getText().contains("Xác nhận trả sách thành công"));
		driver.get("http://localhost:8000/quanlyphat");
		List<WebElement> phats = driver.findElements(By.cssSelector(".phat[data-idmuon='" + row.getAttribute("data-id") + "']"));
		boolean hasTreHan = false, hasHuHong = false;
		for (WebElement phat : phats) {
			String loai = phat.findElement(By.cssSelector(".loai-phat")).getText();
			if (loai.equals("Trễ hạn")) hasTreHan = true;
			if (loai.equals("Hư hỏng")) hasHuHong = true;
			Assert.assertEquals(phat.findElement(By.cssSelector(".trangthai-phat")).getText(), "Chưa thanh toán");
		}
		Assert.assertTrue(hasTreHan && hasHuHong, "Phải có cả phạt trễ hạn và hư hỏng");
	}

	/**
	 * TC_TS_08: Hủy thao tác xác nhận trả sách
	 */
	@Test
	public void TC_TS_08_huyXacNhanTraSach() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		WebElement row = driver.findElement(By.cssSelector(".row-muon[data-trangthai='Đang mượn']"));
		row.findElement(By.cssSelector(".btn-xacnhantra")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra")));
		driver.findElement(By.id("btn-huy")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-xacnhantra-huy")));
		driver.findElement(By.id("btn-xacnhan-huy")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("popup-xacnhantra")));
		List<WebElement> rows = driver.findElements(By.cssSelector(".row-muon[data-id='" + row.getAttribute("data-id") + "']"));
		Assert.assertEquals(rows.size(), 1, "Bản ghi vẫn còn trong danh sách");
	}

	/**
	 * TC_TS_09: Không cho phép xác nhận trả với bản ghi không hợp lệ hoặc đã được xử lý
	 */
	@Test
	public void TC_TS_09_khongChoXacNhanTraKhongHopLe() {
		loginAsThuThu();
		goToXacNhanTraSachTab();
		List<WebElement> rows = driver.findElements(By.cssSelector(".row-muon[data-trangthai='Đã trả']"));
		if (!rows.isEmpty()) {
			WebElement row = rows.get(0);
			boolean hasBtn = row.findElements(By.cssSelector(".btn-xacnhantra")).size() > 0;
			Assert.assertFalse(hasBtn, "Không được phép xác nhận trả với bản ghi đã trả");
		}
		driver.get("http://localhost:8000/quanlytrasach/xacnhan?id=999999");
		WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger, .alert-warning")));
		Assert.assertTrue(alert.getText().contains("Không tìm thấy bản ghi mượn") || alert.getText().contains("không còn hợp lệ"));
	}
}

