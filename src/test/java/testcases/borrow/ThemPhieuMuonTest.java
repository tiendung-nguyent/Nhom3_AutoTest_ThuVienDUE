package testcases.borrow;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.layout.SidebarPage;
import pageobjects.borrow.QuanLyMuonSachPage;
import pageobjects.users.UserPage;

public class ThemPhieuMuonTest extends BaseTest {

    @BeforeMethod
    public void setupTest() {
        login();
        new SidebarPage().gotoBorrow();
    }

    @Test()
    public void BR_F001() {
        SidebarPage sidebarPage = new SidebarPage();
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        sidebarPage.gotoUsers();
        UserPage userPage = new UserPage();
        String maDocGiaHopLe = userPage.getUnqualifiedReaderID();
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
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung("3");
        muonSachPage.enterMaSachAtRow(0, "");
        String expectedError = "Không tìm thấy người dùng";
        Assert.assertEquals(muonSachPage.getToastMessage(), expectedError);
    }

    @Test
    public void BR_F003() {
        SidebarPage sidebarPage = new SidebarPage();
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        sidebarPage.gotoUsers();
        UserPage userPage = new UserPage();
        String maKhongPhaiDocGia = userPage.getNonReaderUserID();
        sidebarPage.gotoBorrow();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maKhongPhaiDocGia);
        muonSachPage.enterMaSachAtRow(0, "");
        String expectedError = "Người dùng này không thể mượn sách";
        Assert.assertEquals(muonSachPage.getToastMessage(), expectedError);
    }

    @Test
    public void BR_F004() {
        SidebarPage sidebarPage = new SidebarPage();
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        sidebarPage.gotoUsers();
        UserPage userPage = new UserPage();
        String maDocGiaTN = userPage.getQualifiedReaderID();
        sidebarPage.gotoBorrow();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung(maDocGiaTN);
        muonSachPage.enterMaSachAtRow(0, "");
        String expectedError = "Người dùng này không thể mượn sách";
        Assert.assertEquals(muonSachPage.getToastMessage(), expectedError);
    }

    @Test
    public void BR_F005() {
        QuanLyMuonSachPage muonSachPage = new QuanLyMuonSachPage();
        muonSachPage.clickThemPhieuMuon();
        muonSachPage.enterMaNguoiDung("valid_user"); // Placeholder
        // muonSachPage.MaSach("MS 0"); // In original it was MaSach instead of enterMaSachAtRow
        muonSachPage.enterMaSachAtRow(0, "MS 0");
        muonSachPage.clickLuuPhieuMuon();
        String expected = "Không tìm thấy sách";
        Assert.assertEquals(muonSachPage.getToastMessage(), expected);
    }
}
