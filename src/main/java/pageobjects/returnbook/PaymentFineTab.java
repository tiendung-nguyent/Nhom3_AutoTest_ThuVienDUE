package pageobjects.returnbook;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.GeneralPage;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentFineTab extends GeneralPage {

    private final By rowPhiPhat = By.xpath(
            "//div[@id='tab-2']//table//tbody/tr[" +
                    "td and count(td) >= 4 and " +
                    "not(contains(normalize-space(.), 'Không có dữ liệu')) and " +
                    "not(contains(normalize-space(.), 'Không có người dùng'))" +
                    "]"
    );

    private final By tenNguoiDung = By.cssSelector("td:nth-child(2)");
    private final By tongTien = By.cssSelector("td:nth-child(4)");
    private final By popupChiTietPhiPhat = By.id("popup-payment");
    private final By popupTenNguoiDung = By.id("payUserName");
    private final By popupTongTien = By.id("payTotalAmount");
    private final By popupChiTietKhoanPhat = By.xpath(
            "//tbody[@id='paymentTableBody']/tr[" +
                    "td and count(td) >= 8 and " +
                    "not(contains(normalize-space(.), 'Không có dữ liệu'))" +
                    "]"
    );
    private final By btnXacNhanThanhToan = By.xpath("//div[@id='popup-payment']//button[contains(normalize-space(.),'Xác nhận')]");
    private final By btnClosePopup = By.xpath("//div[@id='popup-payment']//button[contains(text(), '×') or contains(text(), 'Đóng')]");
    private final By btnHuyThanhToan = By.xpath("//div[@id='popup-payment']//button[normalize-space()='Hủy']");
    private final By popupXacNhanHuy = By.id("popup-payment-cancel-confirm");
    private final By btnXacNhanHuy = By.xpath("//div[@id='popup-payment-cancel-confirm']//button[contains(normalize-space(.), 'Thoát thanh')]");
    private final By toastSuccess = By.cssSelector(".toast-success");

    public void waitForTabLoaded() {
        wait.until(driver -> {
            String bodyText = normalizeText(driver.findElement(By.tagName("body")).getText());
            return bodyText.contains("Thanh toán phí phạt") || !getRowsPhiPhat().isEmpty();
        });
    }

    public List<WebElement> getRowsPhiPhat() {
        return driver.findElements(rowPhiPhat).stream()
                .filter(row -> isValidDataRow(row, 4))
                .collect(Collectors.toList());
    }

    private boolean isValidDataRow(WebElement row, int minCellCount) {
        try {
            if (!row.isDisplayed()) return false;
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < minCellCount) return false;
            String text = normalizeText(row.getText());
            return !text.isEmpty() && !text.toLowerCase().contains("không có dữ liệu");
        } catch (Exception e) {
            return false;
        }
    }

    public By getSelectorTenNguoiDung() { return tenNguoiDung; }
    public By getSelectorTongTien() { return tongTien; }

    public WebElement getPopupChiTietPhiPhat() { return driver.findElement(popupChiTietPhiPhat); }
    public WebElement getPopupTenNguoiDung() { return driver.findElement(popupTenNguoiDung); }
    public WebElement getPopupTongTien() { return driver.findElement(popupTongTien); }
    public WebElement getPopupXacNhanHuyThanhToan() { return driver.findElement(popupXacNhanHuy); }

    public List<WebElement> getPopupChiTietKhoanPhat() {
        return driver.findElements(popupChiTietKhoanPhat).stream()
                .filter(row -> isValidDataRow(row, 8))
                .collect(Collectors.toList());
    }

    public void clickXemChiTietPhiPhatWithWait(WebElement row) {
        scrollToElement(row);
        List<WebElement> buttons = row.findElements(By.tagName("button"));
        if (!buttons.isEmpty()) {
            safeClick(buttons.get(buttons.size() - 1));
        } else {
            safeClick(row);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(popupChiTietPhiPhat));
    }

    public void closePopupChiTietPhiPhat() {
        safeClick(btnClosePopup);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(popupChiTietPhiPhat));
    }

    public void clickXacNhanThanhToan() {
        By[] candidates = new By[]{
                By.id("btnConfirmPayment"),
                By.xpath("//div[@id='popup-payment']//button[contains(@onclick,'submitPayment')]"),
                By.xpath("//div[@id='popup-payment']//button[normalize-space()='Xác nhận']"),
                By.xpath("//button[contains(@onclick,'submitPayment')]")
        };

        for (By candidate : candidates) {
            try {
                List<WebElement> buttons = driver.findElements(candidate);
                if (buttons.isEmpty()) continue;
                WebElement button = buttons.stream().filter(WebElement::isDisplayed).findFirst().orElse(buttons.get(0));
                safeClick(button);
                return;
            } catch (Exception ignored) {}
        }
    }

    public void chonPhuongThucThanhToanTienMat() {
        By radioInputBy = By.cssSelector("input[type='radio'][name='paymentMethodNew'][value='cash']");
        safeClick(radioInputBy);
    }

    public void clickHuyThanhToan() {
        safeClick(btnHuyThanhToan);
    }

    public void xacNhanHuyThanhToan() {
        safeClick(btnXacNhanHuy);
    }

    public String getSuccessMessage() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
        return toast.getText().trim();
    }

    public boolean isXacNhanThanhToanEnabled() {
        try {
            List<WebElement> buttons = driver.findElements(btnXacNhanThanhToan);
            if (buttons.isEmpty()) return false;
            WebElement button = buttons.stream().filter(WebElement::isDisplayed).findFirst().orElse(buttons.get(0));
            return button.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}
