package testcases.quanlytrasach;

import common.Constant;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import pageobjects.LoginPage;
import pageobjects.quanLyTraSachPage;

import java.time.Duration;

public class timKiemTraSachTest {

    private quanLyTraSachPage traSachPage;
    private LoginPage loginPage;

    private static final String MA_PHIEU_MUON = "PM0000001";
    private static final String TEN_NGUOI_MUON = "Nguyễn Văn A";
    private static final String TU_KHOA_KHONG_TON_TAI = "Trần Văn Z";

    // =========================
    // SETUP
    // =========================
    @BeforeClass
    public void setupOnce() {

        if (Constant.WEBDRIVER == null) {
            Constant.WEBDRIVER = new ChromeDriver();
            Constant.WEBDRIVER.manage().window().maximize();
        }

        loginPage = new LoginPage();
        traSachPage = new quanLyTraSachPage();

        Constant.WEBDRIVER.get(Constant.THUVIEN_URL);

        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
    }

    // =========================
    // BEFORE EACH TEST
    // =========================
    @BeforeMethod
    public void goToPage() {

        traSachPage.getTabXacNhanTra().click();

        new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10))
                .until(driver -> true);
    }

    // =========================
    // TEST 1 - SEARCH BY MA PHIEU
    // =========================
    @Test
    public void TS_F001_timKiemTheoMaPhieuMuon() {

        traSachPage.timKiemTraSach(MA_PHIEU_MUON);

        Assert.assertTrue(
                traSachPage.isCoKetQuaTraSach(),
                "Không có kết quả tìm kiếm"
        );

        Assert.assertFalse(
                traSachPage.isThongBaoKhongTimThay(),
                "Hiển thị sai thông báo không tìm thấy"
        );

        Assert.assertTrue(
                traSachPage.getDongTheoKeyword(MA_PHIEU_MUON)
                        .getText()
                        .contains(MA_PHIEU_MUON),
                "Không tìm thấy mã phiếu mượn"
        );
    }

    // =========================
    // TEST 2 - SEARCH BY NAME
    // =========================
    @Test
    public void TS_F002_timKiemTheoTenNguoiMuon() {

        traSachPage.timKiemTraSach(TEN_NGUOI_MUON);

        Assert.assertTrue(
                traSachPage.isCoKetQuaTraSach(),
                "Không có kết quả tìm kiếm"
        );

        Assert.assertTrue(
                traSachPage.getDongTheoKeyword(TEN_NGUOI_MUON)
                        .getText()
                        .contains(TEN_NGUOI_MUON),
                "Không tìm thấy tên người mượn"
        );
    }

    // =========================
    // TEST 3 - KHÔNG TỒN TẠI
    // =========================
    @Test
    public void TS_F003_timKiemKhongTonTai() {

        traSachPage.timKiemTraSach(TU_KHOA_KHONG_TON_TAI);

        Assert.assertTrue(
                traSachPage.isThongBaoKhongTimThay(),
                "Không hiển thị thông báo không tìm thấy"
        );
    }

    // =========================
    // TEARDOWN
    // =========================
    @AfterClass
    public void tearDown() {

        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
            Constant.WEBDRIVER = null;
        }
    }
}