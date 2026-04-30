package pageobjects.auth;

import common.Constant;
import pageobjects.GeneralPage;

public class HomePage extends GeneralPage {

    public void open() {
        Constant.WEBDRIVER.get(Constant.THUVIEN_URL);
        Constant.WEBDRIVER.manage().window().maximize();
    }

    public LoginPage gotoLoginPage() {
        return new LoginPage();
    }
}
