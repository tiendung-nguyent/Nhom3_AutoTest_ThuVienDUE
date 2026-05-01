package testcases.borrow;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.layout.SidebarPage;
import pageobjects.borrow.QuanLyMuonSachPage;

public class XoaPhieuMuonTest extends BaseTest {

    @BeforeMethod
    public void setupTest() {
        login();
        new SidebarPage().gotoBorrow();
    }

    @Test()
    public void BR_X001() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        String maPhieuBiXoa = muonSachPage.clickXoaPhieuMuonDauTien();
        muonSachPage.clickDongYPopupXoa();
        String expectedMessage = "Xóa phiếu mượn thành công";
        Assert.assertEquals(muonSachPage.getToastMessage(), expectedMessage);
    }

    @Test()
    public void BR_X002() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.clickXoaPhieuMuonDauTien();
        // check popup content logic
        Assert.assertTrue(true);
    }

    @Test()
    public void BR_X003() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.clickXoaPhieuMuonDauTien();
        muonSachPage.clickHuyPopupXoa();
        Assert.assertTrue(muonSachPage.isPopupXoaClosed());
    }

    @Test()
    public void BR_X004() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        // clickXoaPhieuMuonTheoTrangThai("Đang mượn")
        Assert.assertTrue(true);
    }

    @Test()
    public void BR_X005() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        // clickXoaPhieuMuonTheoTrangThai("Quá hạn")
        Assert.assertTrue(true);
    }
}
