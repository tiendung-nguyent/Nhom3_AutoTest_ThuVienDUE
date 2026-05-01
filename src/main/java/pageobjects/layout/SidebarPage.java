package pageobjects.layout;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import core.BasePage;

public class SidebarPage extends BasePage {

    private final By menuTongQuan = By.xpath("//aside[@class='sidebar']//a[@href='/dashboard/']");
    private final By menuBorrow = By.xpath("//aside[@class='sidebar']//a[@href='/borrow-books/']");
    private final By menuReturn = By.xpath("//aside[@class='sidebar']//a[@href='/return-books/']");
    private final By menuBooks = By.xpath("//aside[@class='sidebar']//a[@href='/books/']");
    private final By menuUsers = By.xpath("//aside[@class='sidebar']//a[@href='/users/']");
    private final By menuReports = By.xpath("//aside[@class='sidebar']//a[@href='/reports/']");

    public WebElement getMenuTongQuan() {
        return driver.findElement(menuTongQuan);
    }

    public WebElement getMenuBorrow() {
        return driver.findElement(menuBorrow);
    }

    public WebElement getMenuReturn() {
        return driver.findElement(menuReturn);
    }

    public WebElement getMenuBooks() {
        return driver.findElement(menuBooks);
    }

    public WebElement getMenuUsers() {
        return driver.findElement(menuUsers);
    }

    public WebElement getMenuReports() {
        return driver.findElement(menuReports);
    }

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

    public boolean isMenuItemActive(String href) {
        String xpath = String.format("//aside[@class='sidebar']//li[a[@href='%s']]", href);
        return driver.findElement(By.xpath(xpath)).getAttribute("class").contains("active");
    }
}
