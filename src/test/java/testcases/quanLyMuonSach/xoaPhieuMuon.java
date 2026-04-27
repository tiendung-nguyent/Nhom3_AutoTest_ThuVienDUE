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
public class xoaPhieuMuon {
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

    @Test(description = "BR_X001 - Xóa phiếu mượn thành công")
    public void BR_X001() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();


        String maPhieuBiXoa = muonSachPage.clickXoaPhieuMuonDaTra();

        muonSachPage.clickDongYPopupXoa();


        String expectedMessage = "Xóa phiếu mượn thành công";
        String actualMessage = muonSachPage.getToastMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "LỖI: Thông báo xóa hiển thị không đúng!");

        try { Thread.sleep(1000); } catch (Exception e) {}

        boolean isTonTai = muonSachPage.isPhieuMuonTonTai(maPhieuBiXoa);
        Assert.assertFalse(isTonTai, "LỖI: Đã báo xóa thành công nhưng mã phiếu " + maPhieuBiXoa + " vẫn còn nằm trong bảng!");

        System.out.println("Test Xóa Phiếu Mượn PASS: Dữ liệu đã biến mất hoàn toàn!");
    }

    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
}
