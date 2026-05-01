package pageobjects.returnbook;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.GeneralPage;
import java.util.List;

public class ReturnConfirmTab extends GeneralPage {

    private final By rowMuon = By.cssSelector(".return-record");
    private final By btnXacNhanTra = By.cssSelector(".return-confirm-trigger");
    private final By popupXacNhanTra = By.id("popup-return-confirm");
    private final By radioTot = By.cssSelector("input[name='returnCondition'][value='good']");
    private final By radioHuHong = By.cssSelector("input[name='returnCondition'][value='damaged']");
    private final By inputMoTaHuHong = By.id("damageDescription");
    private final By btnXacNhan = By.xpath("//button[@onclick='submitReturnConfirm()']");
    private final By btnHuy = By.xpath("//button[@onclick='requestCloseReturnPopup()']");
    private final By btnXacNhanHuy = By.xpath("//button[@onclick='confirmCancelReturnPopup()']");
    private final By toastSuccess = By.cssSelector(".toast-success");
    private final By errorTinhTrang = By.id("returnConditionError");

    public List<WebElement> getRowsMuon() {
        return driver.findElements(rowMuon);
    }

    public void clickXacNhanTra(WebElement row) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(row.findElement(btnXacNhanTra)));
        safeClick(button);
    }

    public void chonTinhTrangTot() {
        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioTot));
        safeClick(radio);
        wait.until(driver -> radio.isSelected());
    }

    public void chonTinhTrangHuHong() {
        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(radioHuHong));
        safeClick(radio);
        wait.until(driver -> radio.isSelected());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='damageLevel']")));
    }

    public void nhapMucDoHuHong(String mucDoId) {
        By radioInput = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']");
        By radioLabel = By.xpath("//input[@name='damageLevel' and @value='" + mucDoId + "']/ancestor::label[1]");
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(radioInput));
        try {
            WebElement label = wait.until(ExpectedConditions.elementToBeClickable(radioLabel));
            label.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
        }
        wait.until(driver -> input.isSelected());
    }

    public void nhapMoTaHuHong(String moTa) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(inputMoTaHuHong));
        input.clear();
        input.sendKeys(moTa);
    }

    public void clickXacNhan() {
        safeClick(btnXacNhan);
    }

    public void clickHuy() {
        safeClick(btnHuy);
    }

    public void xacNhanHuy() {
        safeClick(btnXacNhanHuy);
    }

    public String getSuccessMessage() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
        return toast.getText().trim();
    }

    public String getErrorTinhTrangMessage() {
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorTinhTrang));
        return error.getText().trim();
    }
}
