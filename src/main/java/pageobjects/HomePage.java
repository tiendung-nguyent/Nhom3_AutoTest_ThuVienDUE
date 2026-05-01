package pageobjects;

import common.Constant;

public class HomePage extends GeneralPage {

    /**
     * Mở trình duyệt và truy cập vào địa chỉ URL của thư viện từ Constant
     */
    public void open() {
        Constant.WEBDRIVER.get(Constant.THUVIEN_URL);
        Constant.WEBDRIVER.manage().window().maximize();
    }

    /**
     * Phương thức này giúp luồng Test tự nhiên hơn: từ Home đi tới Login
     */
    public LoginPage gotoLoginPage() {
        // Nếu trang web của bạn cần click một nút "Đăng nhập" để hiện form, hãy thêm lệnh click ở đây.
        // Nếu URL trang chủ chính là trang login (như Django thường làm), chỉ cần return object.
        return new LoginPage();
    }
}