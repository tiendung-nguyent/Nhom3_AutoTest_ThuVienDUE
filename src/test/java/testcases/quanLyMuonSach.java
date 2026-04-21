package testcases;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver; // Thêm import này
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.quanLyMuonSachPage;

public class quanLyMuonSach {

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
        Assert.assertEquals(muonSachPage.getSuccessMessage(), expected);
    }

    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null; // Reset về null để session sau khởi tạo lại
        }
    }
}