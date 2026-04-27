package testcases.quanLyMuonSach;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver; // Thêm import này
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.SidebarPage;
import pageobjects.nguoiDungPage;
import pageobjects.quanLyMuonSachPage;

public class themPhieuMuonTest {

    @BeforeMethod
    public void setupLogin() {
        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }
        HomePage homePage = new HomePage();
        homePage.open();
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        org.openqa.selenium.support.ui.WebDriverWait wait = new org.openqa.selenium.support.ui.WebDriverWait(Constant.WEBDRIVER, java.time.Duration.ofSeconds(10));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("dashboard"));
        SidebarPage sidebarPage = new SidebarPage();
        sidebarPage.gotoBorrow();
    }

    @Test()
    public void BR_F001() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();
        sidebarPage.gotoUsers();
        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maDocGiaHopLe = nguoiDung.getUnqualifiedReaderID();
        sidebarPage.gotoBorrow();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaHopLe);
        muonSachPage.enterMaSachAtRow(0, "MS 0");
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
    @Test
    public void BR_F003() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoUsers();

        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maKhongPhaiDocGia = nguoiDung.getNonReaderUserID();
        sidebarPage.gotoBorrow();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maKhongPhaiDocGia);
        muonSachPage.enterMaSachAtRow(0, "");
        String expectedError = "Người dùng này không thể mượn sách";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError, "ERROR: Thông báo lỗi không khớp hoặc không hiển thị!");

    }
    @Test
    public void BR_F004(){
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();
        sidebarPage.gotoUsers();
        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maDocGiaTN = nguoiDung.getQualifiedReaderID();
        sidebarPage.gotoBorrow();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaTN);
        muonSachPage.enterMaSachAtRow(0, "");
        String expectedError = "Người dùng này không thể mượn sách";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError, "ERROR: Thông báo lỗi không khớp hoặc không hiển thị!");

    }
    @Test
    public void BR_F005(){
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();
        sidebarPage.gotoUsers();
        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maDocGiaHopLe = nguoiDung.getUnqualifiedReaderID();
        sidebarPage.gotoBorrow();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaHopLe);
        muonSachPage.MaSach("MS 0");
        muonSachPage.clickLuuPhieuMuon();

        String expected = "Không tìm thấy sách";
        Assert.assertEquals(muonSachPage.getToastMessage(), expected);
    }

    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null; // Reset về null để session sau khởi tạo lại
        }
    }
}