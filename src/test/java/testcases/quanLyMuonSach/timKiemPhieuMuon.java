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

public class timKiemPhieuMuon {
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
    public void BR_16() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();
        sidebarPage.gotoUsers();
        nguoiDungPage nguoiDung = new nguoiDungPage();
        String maDocGiaHopLe = nguoiDung.getUnqualifiedReaderID();        
        sidebarPage.gotoBorrow();

        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaHopLe);

        muonSachPage.enterMaSachAtRow(0, "MS0");
        muonSachPage.clickLuuPhieuMuon();

        String tuKhoaTimKiem = muonSachPage.getMaPhieuMuonDauTien();
        System.out.println("Đã lấy được từ khóa: " + tuKhoaTimKiem);

        muonSachPage.nhapTuKhoaTimKiem(tuKhoaTimKiem);

        muonSachPage.clickNutTimKiem();

        String maPhieuKetQua = muonSachPage.getMaPhieuMuonDauTien();

        Assert.assertEquals(maPhieuKetQua, tuKhoaTimKiem,
                "LỖI: Tìm mã " + tuKhoaTimKiem + " nhưng kết quả lại ra mã " + maPhieuKetQua);

    }
    @Test()
    public void BR_17() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();

        String maNguoiDungSearch = muonSachPage.getMaNguoiDungDauTien();

        muonSachPage.nhapTuKhoaTimKiem(maNguoiDungSearch);

        muonSachPage.clickNutTimKiem();


        boolean isChinhXac = muonSachPage.checkTatCaDongKhopMaNguoiDung(maNguoiDungSearch);

        Assert.assertTrue(isChinhXac, "LỖI: Kết quả tìm kiếm hiển thị sai người dùng!");

        System.out.println("Test BR_T002 PASS: Tìm kiếm theo Mã người dùng '" + maNguoiDungSearch + "' thành công!");
    }
    @Test()
    public void BR_18() {
        SidebarPage sidebarPage = new SidebarPage();

        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();
        String maGia = "KHONG_TON_TAI";
        muonSachPage.nhapTuKhoaTimKiem(maGia);
        muonSachPage.clickNutTimKiem();

        String expectedMsg = "Không tìm thấy phiếu mượn";
        String actualMsg = muonSachPage.getToastMessage();

        Assert.assertEquals(actualMsg, expectedMsg, "LỖI: Thông báo tìm kiếm không khớp!");

        System.out.println("Test BR_T003 PASS: Hệ thống đã báo lỗi đúng khi tìm mã không tồn tại.");
    }
    @Test()
    public void BR_19() {
        SidebarPage sidebarPage = new SidebarPage();
        quanLyMuonSachPage muonSachPage = new quanLyMuonSachPage();

        sidebarPage.gotoBorrow();


        try { Thread.sleep(2000); } catch (Exception e) {}

        int soLuongBanDau = muonSachPage.getSoLuongDongHienTai();
        System.out.println("Số lượng thực tế ban đầu: " + soLuongBanDau);

        muonSachPage.clearOInputSearch();

        muonSachPage.clickNutTimKiem();

        int soLuongSauKhiSearch = muonSachPage.getSoLuongDongHienTai();

        Assert.assertEquals(soLuongSauKhiSearch, soLuongBanDau, "LỖI: Danh sách hiển thị lại không đầy đủ!");
        Assert.assertTrue(soLuongSauKhiSearch > 0, "LỖI: Bảng không có dữ liệu!");

        System.out.println("Test BR_T004 PASS: Reset tìm kiếm thành công!");
    }
    @AfterMethod
    public void quitBrowser() {
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
}
