package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class quanLyMuonSachPage extends GeneralPage {

    // --- Locators ---
    private final By btnThemPhieuMuon = By.id("addBorrowBtn");
    private final By txtTimKiem = By.id("borrowSearchInput");
    private final By btnTimKiem = By.id("borrowSearchBtn");

    private final By txtMaNguoiDung = By.id("addUserId");
    private final By btnThemDongMoi = By.cssSelector("button.btn-add-dashed");
    private final By txtListMaSach = By.cssSelector("input.pm-input.book-code-input");

    // Bổ sung nút Lưu và Thông báo thành công để hoàn tất nghiệp vụ
    private final By btnLuuPhieuMuon = By.cssSelector("button.btn-save");
    private final By lblSuccessMsg = By.cssSelector("div.toast-message");

    private final By suggestionItems = By.cssSelector(".suggestion-item");
    // --- Elements ---

    public WebElement getBtnThemPhieuMuon() {
        return Constant.WEBDRIVER.findElement(btnThemPhieuMuon);
    }

    public WebElement getTxtMaNguoiDung() {
        return Constant.WEBDRIVER.findElement(txtMaNguoiDung);
    }

    public WebElement getBtnThemDongMoi() {
        return Constant.WEBDRIVER.findElement(btnThemDongMoi);
    }

    // --- Actions ---

    /**
     * Click nút để mở modal thêm phiếu mượn
     */
    public void clickThemPhieuMuon() {
        this.scrollToElement(btnThemPhieuMuon); // Kế thừa từ GeneralPage
        this.getBtnThemPhieuMuon().click();
    }

    /**
     * Nhập Mã người dùng
     */
    public void enterMaNguoiDung(String maND) {
        this.scrollToElement(txtMaNguoiDung); // Cuộn tới ô nhập
        this.getTxtMaNguoiDung().clear();
        this.getTxtMaNguoiDung().sendKeys(maND);
    }

    /**
     * Click nút để thêm một dòng nhập mã sách mới
     */
    public void clickThemDongMoi() {
        this.scrollToElement(btnThemDongMoi); // Cuộn tới nút thêm dòng
        this.getBtnThemDongMoi().click();
    }

    /**
     * Nhập Mã sách vào một dòng cụ thể (index 0 là dòng đầu tiên)
     */
    public void enterMaSachAtRow(int index, String maSach) {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(txtListMaSach);
        if (index < inputs.size()) {
            WebElement input = inputs.get(index);

            // 1. Cuộn tới ô nhập
            ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

            // 2. Nhập mã sách
            input.clear();
            input.sendKeys(maSach);

            // 3. ĐỢI danh sách gợi ý hiện ra và CLICK chọn
            try {
                // Đợi 1 giây để JavaScript load danh sách gợi ý
                Thread.sleep(1000);

                // Tìm tất cả các item gợi ý (locator .suggestion-item bạn đã inspect thấy)
                List<WebElement> suggestions = Constant.WEBDRIVER.findElements(By.cssSelector(".suggestion-item"));

                if (!suggestions.isEmpty()) {
                    // Click vào cái đầu tiên hiện ra (thường là cái đúng nhất)
                    suggestions.get(0).click();
                    System.out.println("Đã click chọn sách từ gợi ý.");
                } else {
                    System.out.println("Cảnh báo: Không thấy danh sách gợi ý hiện ra!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Nhấn nút Lưu phiếu mượn
     */
    public void clickLuuPhieuMuon() {
        this.scrollToElement(btnLuuPhieuMuon); // Cuộn tới nút Lưu ở cuối modal
        Constant.WEBDRIVER.findElement(btnLuuPhieuMuon).click();
    }

    /**
     * Lấy thông báo thành công
     */
    public String getToastMessage() {
        // Đợi tối đa 5 giây để thông báo (bất kể xanh hay đỏ) xuất hiện
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast-message")));
            return toast.getText();
        } catch (Exception e) {
            return "Không có thông báo nào xuất hiện!";
        }
    }
}