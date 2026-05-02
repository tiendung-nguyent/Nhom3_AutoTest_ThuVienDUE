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
    public void BR_07() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickGiaHanTheoTrangThai("Đang mượn");

        muonSachPage.enterNgayGiaHanMoiTuDong();

        muonSachPage.clickLuuThayDoiGiaHan();

        String expectedError = "Gia hạn thành công";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError);
    }
    @Test
    public void BR_08() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickGiaHanTheoTrangThai("Quá hạn");


        muonSachPage.enterNgayGiaHanMoiTuDong();

        muonSachPage.clickLuuThayDoiGiaHan();

        String expectedError = "Sách đã quá hạn trả, không thể gia hạn. Vui lòng thực hiện trả sách và thanh toán phí phạt nếu có.";
        String actualError = muonSachPage.getToastMessage();
        Assert.assertEquals(actualError, expectedError);
    }
    @Test()
    public void BR_09() {
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
    public void BR_10() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickGiaHanDauTien();

        muonSachPage.enterNgayGiaHanNhoHonNgayMuon();

        muonSachPage.clickLuuThayDoiGiaHan();


        String expectedMsg = "Ngày gia hạn mới phải lớn hơn hạn trả hiện tại.";
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

