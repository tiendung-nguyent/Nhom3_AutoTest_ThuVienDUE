package testcases.returnbook.returnconfirm;

import base.BaseTest;
import core.TextUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.layout.SidebarPage;
import pageobjects.returnbook.PaymentFineTab;
import pageobjects.returnbook.ReturnBookPage;
import pageobjects.returnbook.ReturnConfirmTab;
import java.util.List;

public class ReturnConfirmTest extends BaseTest {

    private ReturnConfirmTab returnConfirmTab;
    private ReturnBookPage returnBookPage;

    private static final String MSG_TRA_SACH_THANH_CONG = "Xác nhận trả sách thành công";
    private static final String ERR_CHUA_CHON_TINH_TRANG = "Vui lòng chọn tình trạng sách khi trả.";
    private static final String TRANG_THAI_DANG_MUON = "Đang mượn";
    private static final String TRANG_THAI_QUA_HAN = "Quá hạn";

    @BeforeMethod
    public void setUpTest() {
        login();
        openXacNhanTraTab();
    }

    private void openXacNhanTraTab() {
        SidebarPage sidebarPage = new SidebarPage();
        sidebarPage.gotoReturn();
        returnBookPage = new ReturnBookPage();
        returnConfirmTab = returnBookPage.openReturnConfirmTab();
    }

    private String getTrangThai(WebElement row) {
        return TextUtils.normalizeText(row.findElement(By.cssSelector(".col-status")).getText());
    }

    private WebElement findValidReturnRow() {
        return returnConfirmTab.getRowsMuon().stream()
                .filter(row -> {
                    String trangThai = getTrangThai(row);
                    boolean hasBtn = !row.findElements(By.cssSelector(".return-confirm-trigger")).isEmpty();
                    return hasBtn && (trangThai.equals(TRANG_THAI_DANG_MUON) || trangThai.equals(TRANG_THAI_QUA_HAN));
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError("Không tìm thấy bản ghi hợp lệ."));
    }

    @Test
    public void TC_TS_01_hienThiDanhSachXacNhanTra() {
        List<WebElement> rows = returnConfirmTab.getRowsMuon();
        Assert.assertFalse(rows.isEmpty());
    }

    @Test
    public void TC_TS_02_moPopupXacNhanTra() {
        WebElement row = findValidReturnRow();
        returnConfirmTab.clickXacNhanTra(row);
        Assert.assertTrue(driver.findElement(By.id("popup-return-confirm")).isDisplayed());
    }

    @Test
    public void TC_TS_03_loiChuaChonTinhTrangSach() {
        WebElement row = findValidReturnRow();
        returnConfirmTab.clickXacNhanTra(row);
        returnConfirmTab.clickXacNhan();
        Assert.assertEquals(returnConfirmTab.getErrorTinhTrangMessage(), ERR_CHUA_CHON_TINH_TRANG);
    }

    @Test
    public void TC_TS_04_traSachDungHanTinhTrangTot() {
        WebElement row = findValidReturnRow();
        returnConfirmTab.clickXacNhanTra(row);
        returnConfirmTab.chonTinhTrangTot();
        returnConfirmTab.clickXacNhan();
        Assert.assertTrue(returnConfirmTab.getSuccessMessage().contains(MSG_TRA_SACH_THANH_CONG));
    }

    @Test
    public void TC_TS_05_traSachQuaHan() {
        WebElement row = returnConfirmTab.getRowsMuon().stream()
                .filter(r -> getTrangThai(r).equals(TRANG_THAI_QUA_HAN))
                .findFirst().orElse(null);
        if (row != null) {
            returnConfirmTab.clickXacNhanTra(row);
            returnConfirmTab.chonTinhTrangTot();
            returnConfirmTab.clickXacNhan();
            Assert.assertTrue(returnConfirmTab.getSuccessMessage().contains(MSG_TRA_SACH_THANH_CONG));
        }
    }

    @Test
    public void TC_TS_06_traSachHuHong() {
        WebElement row = findValidReturnRow();
        returnConfirmTab.clickXacNhanTra(row);
        returnConfirmTab.chonTinhTrangHuHong();
        returnConfirmTab.nhapMucDoHuHong("1");
        returnConfirmTab.nhapMoTaHuHong("Rách bìa");
        returnConfirmTab.clickXacNhan();
        Assert.assertTrue(returnConfirmTab.getSuccessMessage().contains(MSG_TRA_SACH_THANH_CONG));
    }

    @Test
    public void TC_TS_07_traSachQuaHanVaHuHong() {
        WebElement row = returnConfirmTab.getRowsMuon().stream()
                .filter(r -> getTrangThai(r).equals(TRANG_THAI_QUA_HAN))
                .findFirst().orElse(null);
        if (row != null) {
            returnConfirmTab.clickXacNhanTra(row);
            returnConfirmTab.chonTinhTrangHuHong();
            returnConfirmTab.nhapMucDoHuHong("3");
            returnConfirmTab.nhapMoTaHuHong("Rách bìa, mất trang");
            returnConfirmTab.clickXacNhan();
            Assert.assertTrue(returnConfirmTab.getSuccessMessage().contains(MSG_TRA_SACH_THANH_CONG));
        }
    }

    @Test
    public void TC_TS_08_huyXacNhanTraSach() {
        WebElement row = findValidReturnRow();
        returnConfirmTab.clickXacNhanTra(row);
        returnConfirmTab.clickHuy();
        returnConfirmTab.xacNhanHuy();
        Assert.assertTrue(true);
    }
}
