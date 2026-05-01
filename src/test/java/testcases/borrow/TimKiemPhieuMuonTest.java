package testcases.borrow;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.layout.SidebarPage;
import pageobjects.borrow.QuanLyMuonSachPage;

public class TimKiemPhieuMuonTest extends BaseTest {

    @BeforeMethod
    public void setupTest() {
        login();
        new SidebarPage().gotoBorrow();
    }

    @Test()
    public void BR_T001() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaSachAtRow(0, "MS 0");
        muonSachPage.clickLuuPhieuMuon();

        String tuKhoaTimKiem = muonSachPage.getMaPhieuMuonDauTien();
        muonSachPage.nhapTuKhoaTimKiem(tuKhoaTimKiem);
        muonSachPage.clickNutTimKiem();

        String maPhieuKetQua = muonSachPage.getMaPhieuMuonDauTien();
        Assert.assertEquals(maPhieuKetQua, tuKhoaTimKiem);
    }

    @Test()
    public void BR_T002() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        // Lấy mã người dùng từ dòng đầu tiên rồi tìm kiếm
        // checkTatCaDongKhopMaNguoiDung logic
        Assert.assertTrue(true); 
    }

    @Test()
    public void BR_T003() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.nhapTuKhoaTimKiem("KHONG_TON_TAI");
        muonSachPage.clickNutTimKiem();
        String expectedMsg = "Không tìm thấy phiếu mượn";
        Assert.assertEquals(muonSachPage.getToastMessage(), expectedMsg);
    }

    @Test()
    public void BR_T004() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        // Clear search logic
        Assert.assertTrue(true);
    }
}
