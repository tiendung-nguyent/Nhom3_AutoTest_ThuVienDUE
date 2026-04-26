package testcases.quanLyMuonSach;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver; // Thêm import này
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.quanLyMuonSachPage;

public class themPhieuMuon {

    @BeforeMethod
    public void setupLogin() {
        System.out.println("== Setup Login ==");

        // ĐÂY LÀ ĐOẠN QUAN TRỌNG NHẤT ĐỂ SỬA LỖI NULL
        if (Constant.WEBDRIVER == null) {
            // Nếu bạn dùng Selenium 4.x đời mới, nó sẽ tự quản lý driver
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        // 1. Dùng HomePage để mở trang
        HomePage homePage = new HomePage();
        homePage.open();

        // 2. Từ HomePage đi đến LoginPage và đăng nhập
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        // 3. Sau khi login, dùng sidebar để vào trang mượn sách
        loginPage.gotoBorrow();
    }

    @Test()
    public void BR_F001() {
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung("1");
        muonSachPage.enterMaSachAtRow(0, "MS0");
        muonSachPage.clickLuuPhieuMuon();

        String expected = "Thêm thông tin mượn sách thành công";
        Assert.assertEquals(muonSachPage.getToastMessage(), expected);
    }
    @Test()
    public void BR_F002() {

        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        muonSachPage.clickThemPhieuMuon();
        // 2. Nhập mã người dùng không tồn tại (như trong ảnh bạn nhập là "3")
        muonSachPage.enterMaNguoiDung("3");
        muonSachPage.enterMaSachAtRow(0, "");
        // 5. Kiểm chứng thông báo lỗi
        String expectedError = "Không tìm thấy người dùng";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError, "ERROR: Thông báo lỗi không khớp hoặc không hiển thị!");
    }
    @Test()
    public void BR_F003() {

        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        muonSachPage.clickThemPhieuMuon();

        muonSachPage.enterMaNguoiDung("2");

        muonSachPage.enterMaSachAtRow(0, "MS001-001");

        muonSachPage.clickLuuPhieuMuon();

        String expectedError = "Người dùng này không thể mượn sách";
        String actualError = muonSachPage.getToastMessage();

        Assert.assertEquals(actualError, expectedError, "ERROR: Thông báo lỗi không khớp hoặc không hiển thị đúng!");

        System.out.println("Kết quả: Hệ thống đã chặn chính xác người dùng không phải Độc giả.");
    }

    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null; // Reset về null để session sau khởi tạo lại
        }
    }
}