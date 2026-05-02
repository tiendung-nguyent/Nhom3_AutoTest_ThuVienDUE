package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class quanLySachPage extends GeneralPage {

    // Tại bảng danh mục sách (Hình 1)
    private final By firstBookRow = By.cssSelector("#bookTableBody tr:first-child");

    // Tại bảng chi tiết kho sách (Hình 2)
    private final By stockRows = By.cssSelector("#stockTable tr");

    public void clickXemCuonSachDauTien() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(firstBookRow)).click();
    }

    public String getMaSachDangMuon() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(stockRows));

        By rowDangMuonLocator = By.xpath("//tbody[@id='stockTable']//tr[td//span[contains(text(), 'Đang mượn')]]");

        try {
            WebElement row = Constant.WEBDRIVER.findElement(rowDangMuonLocator);
            String maSach = row.findElement(By.xpath("./td[3]")).getText().trim();
            System.out.println("Tìm thấy mã sách đang mượn: " + maSach);
            return maSach;
        } catch (Exception e) {
            throw new RuntimeException("LỖI: Không tìm thấy cuốn sách nào có trạng thái 'Đang mượn'!");
        }
    }
}