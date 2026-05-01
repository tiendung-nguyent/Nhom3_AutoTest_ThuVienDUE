package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;

public class SidebarPage {

    // --- Locators ---
    private final By menuTongQuan = By.xpath("//aside[@class='sidebar']//a[@href='/dashboard/']");
    private final By menuBorrow = By.xpath("//aside[@class='sidebar']//a[@href='/borrow-books/']");
    private final By menuReturn = By.xpath("//aside[@class='sidebar']//a[@href='/return-books/']");
    private final By menuBooks = By.xpath("//aside[@class='sidebar']//a[@href='/books/']");
    private final By menuUsers = By.xpath("//aside[@class='sidebar']//a[@href='/users/']");
    private final By menuReports = By.xpath("//aside[@class='sidebar']//a[@href='/reports/']");

    // --- Elements ---
    public WebElement getMenuTongQuan() {
        return Constant.WEBDRIVER.findElement(menuTongQuan);
    }

    public WebElement getMenuBorrow() {
        return Constant.WEBDRIVER.findElement(menuBorrow);
    }

    public WebElement getMenuReturn() {
        return Constant.WEBDRIVER.findElement(menuReturn);
    }

    public WebElement getMenuBooks() {
        return Constant.WEBDRIVER.findElement(menuBooks);
    }

    public WebElement getMenuUsers() {
        return Constant.WEBDRIVER.findElement(menuUsers);
    }

    public WebElement getMenuReports() {
        return Constant.WEBDRIVER.findElement(menuReports);
    }

    // --- Actions ---

    public void gotoTongQuan() {
        this.getMenuTongQuan().click();
    }

    public void gotoBorrow() {
        this.getMenuBorrow().click();
    }

    public void gotoReturn() {
        this.getMenuReturn().click();
    }

    public void gotoBooks() {
        this.getMenuBooks().click();
    }

    public void gotoUsers() {
        this.getMenuUsers().click();
    }

    public void gotoReports() {
        this.getMenuReports().click();
    }

    /**
     * Kiểm tra xem một menu item có đang được chọn (active) hay không.
     * @param href link của menu cần kiểm tra (ví dụ: "/borrow/")
     * @return true nếu li chứa a có class 'active'
     */
    public boolean isMenuItemActive(String href) {
        String xpath = String.format("//aside[@class='sidebar']//li[a[@href='%s']]", href);
        return Constant.WEBDRIVER.findElement(By.xpath(xpath)).getAttribute("class").contains("active");
    }
}