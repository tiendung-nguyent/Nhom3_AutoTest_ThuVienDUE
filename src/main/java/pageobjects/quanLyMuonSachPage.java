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

            // 1. Cuộn tới ô nhập sách
            ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

            // 2. Nhập mã sách vào ô text
            input.clear();
            input.sendKeys(maSach);

            // 3. Đợi và chọn chính xác mã sách từ danh sách gợi ý hiện ra
            try {
                // Đợi 1 chút để JS render danh sách gợi ý (Toast/Suggestion thường chậm hơn code)
                Thread.sleep(1000);

                // Lấy danh sách các item gợi ý đang hiển thị trên màn hình
                List<WebElement> suggestions = Constant.WEBDRIVER.findElements(suggestionItems);

                for (WebElement item : suggestions) {
                    // Lấy thuộc tính data-code từ HTML bạn gửi để so sánh
                    String dataCode = item.getAttribute("data-code");

                    if (dataCode != null && dataCode.equals(maSach)) {
                        item.click(); // Click để chọn đúng cuốn sách
                        System.out.println("Đã chọn sách: " + maSach);
                        return; // Thoát hàm sau khi chọn xong
                    }
                }

                // Nếu chạy hết vòng lặp mà không thấy mã khớp, click đại cái đầu tiên (phương án dự phòng)
                if (!suggestions.isEmpty()) {
                    suggestions.get(0).click();
                }

            } catch (Exception e) {
                System.out.println("Lỗi khi chọn sách từ danh sách gợi ý: " + e.getMessage());
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
    public String getSuccessMessage() {
        // 1. Tạo bộ đợi trong tối đa 10 giây
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            // 2. Đợi cho đến khi phần tử xuất hiện trong DOM và hiển thị trên màn hình
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(lblSuccessMsg));

            // 3. Cuộn tới nó (nếu cần)
            this.scrollToElement(lblSuccessMsg);

            return toast.getText();
        } catch (Exception e) {
            return "Không tìm thấy thông báo thành công sau 10 giây!";
        }
    }
}