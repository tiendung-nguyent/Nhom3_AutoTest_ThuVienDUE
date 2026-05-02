package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class quanLyMuonSachPage extends GeneralPage {

    private final By btnThemPhieuMuon = By.id("addBorrowBtn");
    private final By txtSearch = By.id("borrowSearchInput");
    private final By btnSearch = By.id("borrowSearchBtn");

    private final By txtMaNguoiDung = By.id("addUserId");
    private final By btnThemDongMoi = By.cssSelector("button.btn-add-dashed");
    private final By txtListMaSach = By.cssSelector("input.pm-input.book-code-input");

    private final By btnLuuPhieuMuon = By.cssSelector("button.btn-save");
    private final By lblSuccessMsg = By.cssSelector("div.toast-message");

    private final By suggestionItems = By.cssSelector(".suggestion-item");
    private final By btnEditGiaHan = By.cssSelector("button.btn-edit");

    public WebElement getBtnThemPhieuMuon() {
        return Constant.WEBDRIVER.findElement(btnThemPhieuMuon);
    }

    public WebElement getTxtMaNguoiDung() {
        return Constant.WEBDRIVER.findElement(txtMaNguoiDung);
    }

    public WebElement getBtnThemDongMoi() {
        return Constant.WEBDRIVER.findElement(btnThemDongMoi);
    }


    public void clickThemPhieuMuon() {
        this.scrollToElement(btnThemPhieuMuon); // Kế thừa từ GeneralPage
        this.getBtnThemPhieuMuon().click();
    }


    public void enterMaNguoiDung(String maND) {
        this.scrollToElement(txtMaNguoiDung); // Cuộn tới ô nhập
        this.getTxtMaNguoiDung().clear();
        this.getTxtMaNguoiDung().sendKeys(maND);
    }


    public void clickThemDongMoi() {
        this.scrollToElement(btnThemDongMoi); // Cuộn tới nút thêm dòng
        this.getBtnThemDongMoi().click();
    }

    public void enterNgayMuonTrongQuaKhu() {
        try {
            System.out.println("Đang tính toán và nhập ngày mượn trong quá khứ (trước 3 tháng)...");

            // 1. Tính toán ngày: Lấy ngày hiện tại trừ đi 3 tháng
            LocalDate ngayHienTai = LocalDate.now();
            LocalDate ngayTrongQuaKhu = ngayHienTai.minusMonths(3);

            // 2. Định dạng chuẩn theo kiểu gõ phím của Chrome (MMddyyyy)
            String strNgayMuon = ngayTrongQuaKhu.format(DateTimeFormatter.ofPattern("MMddyyyy"));

            // 3. Tìm ô nhập ngày mượn dựa vào ảnh Dung cung cấp
            By txtNgayMuon = By.id("addBorrowDate");
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtNgayMuon));

            // Cuộn màn hình tới ô nhập
            new org.openqa.selenium.interactions.Actions(Constant.WEBDRIVER)
                    .scrollToElement(input)
                    .perform();

            // 4. KỸ THUẬT XÓA SẠCH VÀ GÕ PHÍM
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
            js.executeScript("arguments[0].value = '';", input); // Quét sạch dữ liệu rác (nếu có)

            input.click();

            // Bấm mũi tên trái 3 lần để ép con trỏ nhảy về ô Tháng (mm) ngoài cùng
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);

            // Bây giờ mới gõ chuỗi MMddyyyy vào
            input.sendKeys(strNgayMuon);

            // Gửi phím TAB để web chốt dữ liệu
            input.sendKeys(org.openqa.selenium.Keys.TAB);

            System.out.println("Đã nhập ngày mượn: " + strNgayMuon + " (Thực tế là: " + ngayTrongQuaKhu.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi nhập ngày mượn trong quá khứ: " + e.getMessage());
        }
    }
    public void MaSach(String maSach) {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(txtListMaSach);

        if (!inputs.isEmpty()) {

            this.scrollToElement(txtListMaSach);

            WebElement input = inputs.get(0);

            input.clear();
            input.sendKeys(maSach);

        }
    }

    public void enterMaSachAtRow(int index, String maSach) {
        List<WebElement> inputs = Constant.WEBDRIVER.findElements(txtListMaSach);
        if (index < inputs.size()) {
            WebElement input = inputs.get(index);

            ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", input);

            input.clear();
            input.sendKeys(maSach);

            try {
                Thread.sleep(1000);

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

    public void clickLuuPhieuMuon() {
        this.scrollToElement(btnLuuPhieuMuon); // Cuộn tới nút Lưu ở cuối modal
        Constant.WEBDRIVER.findElement(btnLuuPhieuMuon).click();
    }


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

    public void clickGiaHanDauTien() {
        System.out.println("Đang tìm nút Gia hạn...");
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnEditGiaHan));

            this.scrollToElement(btnEditGiaHan);

            btn.click();
            System.out.println("Đã click nút Gia hạn thành công.");
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy hoặc không click được nút Gia hạn! " + e.getMessage());
        }
    }
    public void enterNgayGiaHanMoiTuDong() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(5)");
            String ngayMuonText = Constant.WEBDRIVER.findElement(ngayMuonLocator).getText().trim();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, inputFormatter);
            LocalDate ngayGiaHanMoi = ngayMuonDate.plusDays(30);

            String strNgayMoi = ngayGiaHanMoi.format(DateTimeFormatter.ofPattern("MMddyyyy"));
            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
            this.scrollToElement(txtNgayMoi);

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
            js.executeScript("arguments[0].value = '';", input);

            input.click();

            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);

            input.sendKeys(strNgayMoi);

            input.sendKeys(org.openqa.selenium.Keys.TAB);

            System.out.println("Đã ép con trỏ về ô mm và gõ: " + strNgayMoi);

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi nhập ngày: " + e.getMessage());
        }
    }

    public void enterNgayGiaHanMoi(String ngayMoi) {
        By txtNgayMoi = By.id("extLgNewDue");
        this.scrollToElement(txtNgayMoi);
        WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
        input.clear();
        input.sendKeys(ngayMoi);
    }


    public void clickLuuThayDoiGiaHan() {
        try {
            System.out.println("Đang tìm nút Lưu thay đổi trong modal popup-extend-large...");

            By btnLuuGiaHan = By.cssSelector("#popup-extend-large .btn-save");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnLuuGiaHan));

            this.scrollToElement(btnLuuGiaHan);
            btn.click();

            System.out.println("Đã click nút Lưu thay đổi thành công!");

            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("LỖI: Không nhấn được nút Lưu thay đổi: " + e.getMessage());
        }
    }

    public void clickHuyGiaHan() {
        try {
            System.out.println("Đang tìm nút Hủy trong modal popup-extend-large...");

            By btnHuy = By.cssSelector("#popup-extend-large .btn-cancel");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnHuy));

            this.scrollToElement(btnHuy);
            btn.click();

            System.out.println("Đã click nút Hủy thành công!");

            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("LỖI: Không nhấn được nút Hủy: " + e.getMessage());
        }
    }

    public String getNgayDenHanDauTien() {
        By ngayDenHanLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(5)");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(ngayDenHanLocator));
            String ngayDenHan = cell.getText().trim();
            System.out.println("Đã lấy được ngày đến hạn: " + ngayDenHan);
            return ngayDenHan;
        } catch (Exception e) {
            System.out.println("LỖI: Bảng không load được dữ liệu hoặc trống!");
            throw e;
        }
    }


    public boolean isModalGiaHanClosed() {
        try {
            WebElement modal = Constant.WEBDRIVER.findElement(By.id("popup-extend-large"));
            return !modal.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return true;
        }
    }
    public void clickGiaHanTheoTrangThai(String trangThai) {

        By btnGiaHanLocator = By.xpath("//tbody[@id='borrowTableBody']//tr[td[6]//span[contains(normalize-space(.), '" + trangThai + "')]]//button[contains(@class, 'btn-edit')]");

        try {
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
            WebElement btnGiaHan = wait.until(ExpectedConditions.elementToBeClickable(btnGiaHanLocator));

            this.scrollToElement(btnGiaHanLocator);
            btnGiaHan.click();
            System.out.println("Đã click nút Gia hạn của phiếu '" + trangThai + "' thành công.");
        } catch (Exception e) {
            throw new RuntimeException("LỖI: Không tìm thấy nút Gia hạn của phiếu mượn trạng thái '" + trangThai + "'!");
        }
    }
    public void enterNgayGiaHanNhoHonNgayMuon() {
        try {
            By ngayMuonLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(4)");
            String ngayMuonText = Constant.WEBDRIVER.findElement(ngayMuonLocator).getText().trim();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ngayMuonDate = LocalDate.parse(ngayMuonText, inputFormatter);

            LocalDate ngayGiaHanLoi = ngayMuonDate.minusDays(5);

            String strNgayMoi = ngayGiaHanLoi.format(DateTimeFormatter.ofPattern("MMddyyyy"));

            By txtNgayMoi = By.id("extLgNewDue");
            WebElement input = Constant.WEBDRIVER.findElement(txtNgayMoi);
            this.scrollToElement(txtNgayMoi);

            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
            js.executeScript("arguments[0].value = '';", input);

            input.click();

            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);
            input.sendKeys(org.openqa.selenium.Keys.ARROW_LEFT);

            input.sendKeys(strNgayMoi);
            input.sendKeys(org.openqa.selenium.Keys.TAB);

            System.out.println("Đã cố tình gõ ngày lỗi (Nhỏ hơn ngày mượn): " + strNgayMoi);

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi nhập ngày: " + e.getMessage());
        }
    }


    public void clickDongYPopupXoa() {
        try {
            System.out.println("Đang đợi popup xác nhận xóa xuất hiện...");
            By btnDongYLocator = By.cssSelector("#popup-delete-confirm .btn-danger");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btnDongY = wait.until(ExpectedConditions.elementToBeClickable(btnDongYLocator));

            btnDongY.click();
            System.out.println("Đã click nút Đồng ý xóa.");

            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Lỗi khi click Đồng ý xóa: " + e.getMessage());
        }
    }
    public boolean isPhieuMuonTonTai(String maPhieu) {
        List<WebElement> checkExist = Constant.WEBDRIVER.findElements(By.cssSelector("#borrowTableBody tr[data-id='" + maPhieu + "']"));


        return !checkExist.isEmpty();
    }
    public String clickXoaPhieuMuonDauTien() {
        System.out.println("Đang tìm nút icon Xóa ở dòng đầu tiên...");

        By firstRowLocator = By.cssSelector("#borrowTableBody tr:first-child");
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        WebElement firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowLocator));

        String maPhieu = firstRow.getAttribute("data-id");

        WebElement btnXoa = firstRow.findElement(By.cssSelector(".btn-del"));
        new org.openqa.selenium.interactions.Actions(Constant.WEBDRIVER)
                .scrollToElement(btnXoa)
                .perform();

        btnXoa.click();
        System.out.println("Đã click nút Xóa của phiếu: " + maPhieu);

        return maPhieu;
    }
    public String getNoiDungPopupXoa() {
        System.out.println("Đang đọc nội dung tiêu đề trên hộp thoại xác nhận xóa...");

        By titleLocator = By.cssSelector("#popup-delete-confirm .del-title");

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(titleLocator));

        return titleElement.getText().trim();
    }
    public void clickHuyPopupXoa() {
        try {
            System.out.println("Đang tìm nút Hủy trên modal popup-delete-confirm...");

            By btnHuyLocator = By.cssSelector("#popup-delete-confirm .btn-cancel");

            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement btnHuy = wait.until(ExpectedConditions.elementToBeClickable(btnHuyLocator));

            btnHuy.click();
            System.out.println("Đã click nút Hủy xóa.");

            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Lỗi khi click Hủy xóa: " + e.getMessage());
        }
    }
    public boolean isPopupXoaClosed() {
        try {
            WebElement popup = Constant.WEBDRIVER.findElement(By.id("popup-delete-confirm"));
            return !popup.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return true;
        }
    }

    public String clickXoaPhieuMuonTheoTrangThai(String trangThai) {
        System.out.println("Đang đợi dữ liệu ổn định và quét tìm phiếu mượn có trạng thái: '" + trangThai + "'...");

        try { Thread.sleep(1500); } catch (Exception e) {}

        By rowLocator = By.xpath("//tbody[@id='borrowTableBody']//tr[td[6]//span[contains(normalize-space(.), '" + trangThai + "')]]");

        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
                WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(rowLocator));

                String maPhieu = row.getAttribute("data-id");
                System.out.println("Đã tìm thấy phiếu mượn '" + trangThai + "': " + maPhieu);

                WebElement btnXoa = row.findElement(By.cssSelector(".btn-del"));

                new org.openqa.selenium.interactions.Actions(Constant.WEBDRIVER)
                        .scrollToElement(btnXoa)
                        .perform();

                btnXoa.click();
                System.out.println("Đã click nút icon Xóa thành công.");

                return maPhieu;

            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                // Nếu bị lỗi Stale, in ra thông báo và vòng lặp sẽ tự chạy lại
                System.out.println("Giao diện vừa bị load lại (StaleElement), đang thử bắt lại phần tử lần " + (i + 2) + "...");
                try { Thread.sleep(1000); } catch (Exception ex) {}
            } catch (org.openqa.selenium.TimeoutException e) {
                throw new RuntimeException("LỖI: Chờ 10 giây nhưng không thấy phiếu mượn nào mang trạng thái '" + trangThai + "'!");
            }
        }

        throw new RuntimeException("LỖI: Giao diện web tải lại liên tục hoặc mạng quá lag, không thể click được nút Xóa!");
    }
    public void nhapTuKhoaTimKiem(String tuKhoa) {

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(txtSearch));

        input.clear();
        input.sendKeys(tuKhoa);
    }
    public void clickNutTimKiem() {

        Constant.WEBDRIVER.findElement(btnSearch).click();

        try { Thread.sleep(1500); } catch (Exception e) {}
    }
    public String getMaPhieuMuonDauTien() {
        By maPhieuLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(1)");

        try {
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(maPhieuLocator));
            return cell.getText().trim();
        } catch (Exception e) {
            throw new RuntimeException("LỖI: Bảng kết quả tìm kiếm đang trống!");
        }
    }

    public String getMaNguoiDungDauTien() {
        By maNDLocator = By.cssSelector("#borrowTableBody tr:first-child td:nth-child(2)");

        try {
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5));
            WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(maNDLocator));
            return cell.getText().trim();
        } catch (Exception e) {
            throw new RuntimeException("LỖI: Không lấy được Mã người dùng để test!");
        }
    }


    public boolean checkTatCaDongKhopMaNguoiDung(String maMongMuon) {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector("#borrowTableBody tr"));

        if (rows.isEmpty()) return false;

        for (WebElement row : rows) {
            String maThucTe = row.findElement(By.xpath("./td[2]")).getText().trim();
            if (!maThucTe.equals(maMongMuon)) {
                System.out.println("LỖI: Tìm thấy mã " + maThucTe + " không khớp với " + maMongMuon);
                return false;
            }
        }
        return true;
    }
    public void clearOInputSearch() {
        System.out.println("Đang xóa trống ô tìm kiếm để tải lại danh sách...");
        By txtSearch = By.id("borrowSearchInput");
        WebElement input = Constant.WEBDRIVER.findElement(txtSearch);

        input.clear();

        input.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        input.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
    }
    public int getSoLuongDongHienTai() {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector("#borrowTableBody tr"));
        int count = rows.size();
        System.out.println("Số lượng dòng hiện có trong bảng: " + count);
        return count;
    }

}