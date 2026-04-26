package testcases.quanLyMuonSach;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.SidebarPage;
import pageobjects.nguoiDungPage;
import pageobjects.quanLyMuonSachPage;

public class giaHanPhieuMuonTest {

    @BeforeMethod
    public void setupLogin() {
        System.out.println("== Setup Login ==");

        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        HomePage homePage = new HomePage();
        homePage.open();

        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);


    }

    @Test
    public void BR_G001() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickGiaHanDauTien();

        muonSachPage.enterNgayGiaHanMoiTuDong();

        muonSachPage.clickLuuThayDoiGiaHan();

        String expectedError = "Gia hạn thành công";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError);
    }
    @Test()
    public void BR_G002() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        String ngayDenHanCu = muonSachPage.getNgayDenHanDauTien();
        System.out.println("Ngày đến hạn ban đầu: " + ngayDenHanCu);

        muonSachPage.clickGiaHanDauTien();

        muonSachPage.enterNgayGiaHanMoiTuDong();

        muonSachPage.clickHuyGiaHan();


        Assert.assertTrue(muonSachPage.isModalGiaHanClosed(), "LỖI: Biểu mẫu Gia hạn chưa được đóng!");

        String ngayDenHanHienTai = muonSachPage.getNgayDenHanDauTien();
        Assert.assertEquals(ngayDenHanHienTai, ngayDenHanCu, "LỖI: Ngày đến hạn bị thay đổi dù đã nhấn Hủy!");

    }
    @Test
    public void BR_G003() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickGiaHanDauTien();

        muonSachPage.enterNgayGiaHanNhoHonNgayMuon();

        muonSachPage.clickLuuThayDoiGiaHan();


        String expectedMsg = "Ngày gia hạn mới không thể nhỏ hơn ngày mượn";
        String actualMsg = muonSachPage.getToastMessage();

        Assert.assertEquals(actualMsg, expectedMsg, "LỖI: Thông báo bắt lỗi ngày không đúng!");
    }
    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
}

