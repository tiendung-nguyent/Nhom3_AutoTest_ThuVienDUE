package pageobjects.returnbook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.GeneralPage;

public class ReturnBookPage extends GeneralPage {
    
    private final By tabXacNhanTra = By.xpath("//button[contains(@onclick, 'tab-1')]");
    private final By tabThanhToanPhiPhat = By.xpath("//button[contains(@onclick, 'tab-2')]");

    public ReturnConfirmTab openReturnConfirmTab() {
        safeClick(tabXacNhanTra);
        return new ReturnConfirmTab();
    }

    public PaymentFineTab openPaymentFineTab() {
        safeClick(tabThanhToanPhiPhat);
        PaymentFineTab tab = new PaymentFineTab();
        tab.waitForTabLoaded();
        return tab;
    }
    
    // Maintain compatibility for now if needed, or just move everything
}
