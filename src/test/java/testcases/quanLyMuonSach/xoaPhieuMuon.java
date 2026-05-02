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

    @Test()
    public void BR_11() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();


        String maPhieuBiXoa = muonSachPage.clickXoaPhieuMuonTheoTrangThai("Đã trả");

        muonSachPage.clickDongYPopupXoa();


        String expectedMessage = "Xóa phiếu mượn thành công";
        String actualMessage = muonSachPage.getToastMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "LỖI: Thông báo xóa hiển thị không đúng!");

        try { Thread.sleep(1000); } catch (Exception e) {}

        boolean isTonTai = muonSachPage.isPhieuMuonTonTai(maPhieuBiXoa);
        Assert.assertFalse(isTonTai, "LỖI: Đã báo xóa thành công nhưng mã phiếu " + maPhieuBiXoa + " vẫn còn nằm trong bảng!");

        System.out.println("Test Xóa Phiếu Mượn PASS: Dữ liệu đã biến mất hoàn toàn!");
    }
    @Test()
    public void BR_12() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        muonSachPage.clickXoaPhieuMuonDauTien();

        String expectedText = "Bạn có muốn xóa?";
        String actualText = muonSachPage.getNoiDungPopupXoa();

        boolean isChuaNoiDung = actualText.contains(expectedText);

        Assert.assertTrue(isChuaNoiDung,
                "LỖI: Hộp thoại không chứa câu hỏi xác nhận! \nNội dung thực tế trên web là: '" + actualText + "'");

        System.out.println("Test BR_X002 PASS: Hộp thoại xác nhận hiển thị đúng nội dung yêu cầu!");
    }
    @Test()
    public void BR_13() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        String maPhieuDangThaoTac = muonSachPage.clickXoaPhieuMuonDauTien();

        muonSachPage.clickHuyPopupXoa();


        Assert.assertTrue(muonSachPage.isPopupXoaClosed(), "LỖI: Hộp thoại xác nhận xóa vẫn chưa đóng!");

        boolean isTonTai = muonSachPage.isPhieuMuonTonTai(maPhieuDangThaoTac);
        Assert.assertTrue(isTonTai, "LỖI: Đã nhấn Hủy nhưng phiếu mượn " + maPhieuDangThaoTac + " lại bị biến mất khỏi danh sách!");

        System.out.println("Test BR_X003 PASS: Tính năng Hủy Xóa hoạt động hoàn hảo, dữ liệu được bảo toàn!");
    }
    @Test()
    public void BR_14() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();


        String maPhieuBiXoa = muonSachPage.clickXoaPhieuMuonTheoTrangThai("Đang mượn");

        muonSachPage.clickDongYPopupXoa();


        String expectedMessage = "Không thể xóa phiếu mượn đang mượn";
        String actualMessage = muonSachPage.getToastMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "LỖI: Thông báo xóa hiển thị không đúng!");
        try { Thread.sleep(1000); } catch (Exception e) {}

        boolean isTonTai = muonSachPage.isPhieuMuonTonTai(maPhieuBiXoa);
        Assert.assertTrue(isTonTai, "LỖI NGHIÊM TRỌNG: Đã báo lỗi không cho xóa nhưng phiếu " + maPhieuBiXoa + " lại biến mất khỏi bảng!");

    }
    @Test()
    public void BR_15() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();
        sidebarPage.gotoUsers();
        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maDocGiaHopLe = nguoiDung.getUnqualifiedReaderID();
        sidebarPage.gotoBorrow();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaHopLe);
        muonSachPage.enterNgayMuonTrongQuaKhu();
        muonSachPage.enterMaSachAtRow(0, "MS 0");
        muonSachPage.clickLuuPhieuMuon();


        String maPhieuBiXoa = muonSachPage.clickXoaPhieuMuonTheoTrangThai("Quá hạn");

        muonSachPage.clickDongYPopupXoa();


        String expectedMessage = "Không thể xóa phiếu mượn quá hạn";
        String actualMessage = muonSachPage.getToastMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "LỖI: Thông báo xóa hiển thị không đúng!");

        try { Thread.sleep(1000); } catch (Exception e) {} // Đợi UI ổn định

        boolean isTonTai = muonSachPage.isPhieuMuonTonTai(maPhieuBiXoa);
        Assert.assertTrue(isTonTai, "LỖI NGHIÊM TRỌNG: Hệ thống báo không thể xóa nhưng phiếu " + maPhieuBiXoa + " lại biến mất khỏi bảng!");
    }


    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
}