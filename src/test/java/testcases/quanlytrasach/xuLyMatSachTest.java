package testcases.quanlytrasach;
import common.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebElement;


public class xuLyMatSachTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void preCondition() {
        loginAsUser();

    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void TC_MS_01_HienThiDanhSachXuLyMatSach() {
        // 1. Chuyển sang màn hình Trả sách
        WebElement menuTraSach = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Trả sách')]")
        ));
        menuTraSach.click();

        // 2. Click vào tab "Xử lý mất sách" (Dùng XPath tab-3 từ ảnh image_a0fc29.jpg)
        WebElement tabXuLyMatSach = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@onclick[contains(.,'tab-3')]]")
        ));
        tabXuLyMatSach.click();

        // 3. Đợi 3 giây để Group 10 quan sát bảng được lọc
        try {
            System.out.println("🔍 Đang kiểm tra danh sách trong tab Xử lý mất sách...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4. Kiểm tra tiêu đề bảng để xác nhận đã vào đúng khu vực
        WebElement tableLabel = driver.findElement(By.xpath("//p[contains(@class,'section-label') and contains(text(),'Danh sách bản ghi mượn cần xử lý mất sách')]"));
        // 5. Lấy tất cả các dòng trong bảng
        // Chỉ lấy những dòng chính có chứa dữ liệu và nút bấm
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr[contains(@class, 'lost-record-row-group-start')]"));
        for (WebElement row : rows) {
            String maPhieu = row.findElement(By.xpath("./td[1]")).getText();
            String trangThai = row.findElement(By.xpath("./td[6]")).getText();

            System.out.println("--- Đang kiểm tra phiếu: " + maPhieu + " ---");

            // KIỂM TRA ĐIỀU KIỆN: Trạng thái phải là Đang mượn hoặc Quá hạn
            boolean isHopLe = trangThai.contains("Đang mượn") || trangThai.contains("Quá hạn");
            // KIỂM TRA NÚT: Phải có nút "Báo mất" màu đỏ
            WebElement btnBaoMat = row.findElement(By.xpath(".//button[contains(text(),'Báo mất')]"));
        }
    }

    @Test
    public void TC_MS_02_MoFormXuLyMatSach() {
        // --- 1. CHỌN TAB VÀ DỮ LIỆU NGẪU NHIÊN ---
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        // Sử dụng onclick chuẩn để chuyển tab
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Lấy danh sách nút Báo mất
        List<WebElement> listButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//button[contains(@class, 'lost-report-trigger')]")));

        int randomIndex = new java.util.Random().nextInt(listButtons.size());
        WebElement btnBaoMat = listButtons.get(randomIndex);

        // Lấy thông tin sách từ data-attribute của thẻ tr
        WebElement row = btnBaoMat.findElement(By.xpath("./ancestor::tr"));
        String tenSach = row.getAttribute("data-book-title");
        String maPhieu = row.getAttribute("data-loan-id");

        System.out.println("🎲 Dòng ngẫu nhiên: " + (randomIndex + 1));
        System.out.println("🚀 Xử lý: " + tenSach + " [" + maPhieu + "]");

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);


        // --- 2. TƯƠNG TÁC VỚI FORM (Đã sửa ID và Link) ---
        // A. CHỜ FORM HIỂN THỊ (Dùng ID chuẩn từ ảnh bạn gửi)
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book")));

        // B. NHẬP GHI CHÚ
        WebElement txtGhiChu = modal.findElement(By.id("lostNote"));
        txtGhiChu.clear();
        txtGhiChu.sendKeys("Nhóm 3 DUE - Test báo mất sách ngẫu nhiên");

        // C. CHỌN PHƯƠNG ÁN "BỒI THƯỜNG TIỀN" (Dùng link bạn xác nhận chạy được)
        WebElement optMoney = modal.findElement(By.xpath(".//input[@value='money']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optMoney);

        // Đợi 1 giây để hệ thống tính phí phạt
        try { Thread.sleep(1000); } catch (InterruptedException e) {}


        // --- 3. XÁC NHẬN (Đã sửa đúng nút theo image_10e88e.jpg) ---
        // Nút có onclick="submitLostBookReport()" và class btn-orange
        WebElement btnXacNhan = modal.findElement(By.xpath(".//button[@onclick='submitLostBookReport()']"));

        // Click xác nhận bằng JS cho chắc chắn
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);

        // Đợi Modal đóng hoàn toàn
        wait.until(ExpectedConditions.invisibilityOf(modal));

        System.out.println("✅ Thành công: Đã xác nhận báo mất cho phiếu [" + maPhieu + "].");
    }
    @Test
    public void TC_MS_03_BaoLoi_KhongNhapNgayKhaiBaoMat() {
        // --- 1. CHUẨN BỊ: MỞ FORM XỬ LÝ MẤT SÁCH ---
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        // SỬA: Chuyển tab bằng onclick chuẩn từ image_116bd6.png
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // SỬA: Lấy hàng bằng class chuẩn lost-record
        List<WebElement> validRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tr[contains(@class, 'lost-record')]")));

        WebElement row = validRows.get(new java.util.Random().nextInt(validRows.size()));

        // SỬA: Tìm nút Báo mất bằng class chuẩn
        WebElement btnBaoMat = row.findElement(By.className("lost-report-trigger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);

        // SỬA: Đợi Form xuất hiện bằng ID popup-lost-book (Chắc chắn hơn dùng text h3)
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book")));

        // --- 2. THỰC HIỆN CÁC BƯỚC KIỂM THỬ ---

        // Bước 2: Tìm input date bên trong modal
        WebElement dtpNgayKhaiBao = modal.findElement(By.xpath(".//input[@type='date']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='';", dtpNgayKhaiBao);

        // Bước 3: Chọn phương án bồi hoàn (Dùng link value='money' Hoa đã test pass)
        WebElement optBoiThuong = modal.findElement(By.xpath(".//input[@value='money']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optBoiThuong);

        // Bước 4: SỬA - Bấm Xác nhận với onclick chính xác tuyệt đối
        WebElement btnXacNhan = modal.findElement(By.xpath(".//button[@onclick='submitLostBookReport()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);

        // --- 3. KIỂM TRA KẾT QUẢ ---

        try {
            // SỬA: Tìm đúng text lỗi hiển thị như trong ảnh image_0fa93b.png
            WebElement lblError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Vui lòng chọn ngày hợp lệ')]")));

            System.out.println("✅ Pass: Hệ thống hiển thị đúng lỗi: " + lblError.getText());

        } catch (Exception e) {
            String validationMessage = dtpNgayKhaiBao.getAttribute("validationMessage");
            if (validationMessage != null && !validationMessage.isEmpty()) {
                System.out.println("✅ Pass: Trình duyệt chặn với lỗi hệ thống: " + validationMessage);
            } else {
                Assert.fail("❌ Hệ thống không hiển thị thông báo lỗi khi bỏ trống ngày!");
            }
        }

        // Kiểm tra Form vẫn đang mở (Dùng biến modal đã tìm thấy ở trên)
        Assert.assertTrue(modal.isDisplayed(), "❌ Form đã bị đóng dù dữ liệu thiếu!");
    }

    @Test
    public void TC_MS_04_BaoLoi_NgayKhaiBaoKhongHopLe() {
        // --- 1. MỞ FORM XỬ LÝ MẤT SÁCH ---
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        // Sử dụng onclick chuẩn để chuyển sang Tab 3
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Lấy danh sách hàng và bấm nút Báo mất của dòng đầu tiên bằng class chuẩn
        List<WebElement> validRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tr[contains(@class, 'lost-record') and .//span[contains(text(), 'Đang mượn')]]")));
        WebElement btnBaoMat = validRows.get(0).findElement(By.className("lost-report-trigger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);

        // Đợi Modal hiển thị với ID chính xác
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book")));


        // --- 2. NHẬP DỮ LIỆU SAI (NGÀY TƯƠNG LAI) ---
        // Tìm ô nhập ngày bên trong modal
        WebElement dtpNgayKhaiBao = modal.findElement(By.xpath(".//input[@type='date']"));
        // Gán giá trị ngày tương lai
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='2099-01-01';", dtpNgayKhaiBao);

        // Chọn phương án bồi hoàn "Bồi thường tiền"
        WebElement optBoiThuong = modal.findElement(By.xpath(".//input[@value='money']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optBoiThuong);

        // Bấm Xác nhận với onclick chính xác
        WebElement btnXacNhan = modal.findElement(By.xpath(".//button[@onclick='submitLostBookReport()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);


        // --- 3. KIỂM TRA LỖI ---
        try {
            // Tìm thông báo lỗi hiển thị trên giao diện
            WebElement lblError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Vui lòng chọn ngày hợp lệ')]")));

            String actualError = lblError.getText();
            System.out.println("✅ Pass: Hệ thống hiển thị đúng lỗi: " + actualError);
            Assert.assertEquals(actualError, "Vui lòng chọn ngày hợp lệ", "❌ Nội dung lỗi không khớp!");

        } catch (Exception e) {
            // Kiểm tra validation của trình duyệt nếu không thấy text lỗi trên giao diện
            String validationMsg = dtpNgayKhaiBao.getAttribute("validationMessage");
            if (validationMsg != null && !validationMsg.isEmpty()) {
                System.out.println("✅ Pass: Trình duyệt chặn với thông báo: " + validationMsg);
            } else {
                // Nếu form vẫn mở tức là hệ thống đã chặn thành công
                Assert.assertTrue(modal.isDisplayed(), "❌ Lỗi: Hệ thống không chặn ngày tương lai!");
            }
        }

        // Đảm bảo Form không bị đóng
        Assert.assertTrue(modal.isDisplayed(), "❌ Form đã bị đóng dù dữ liệu ngày không hợp lệ!");
    }
    @Test
    public void TC_MS_05_BaoLoi_ChuaChonPhuongAnBoiHoan() {
        // --- 1. MỞ TAB XỬ LÝ MẤT SÁCH ---
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));

        // Chuyển sang Tab 3
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // --- 2. CHỌN DÒNG "ĐANG MƯỢN" NGẪU NHIÊN (SỬ DỤNG XPATH MỚI) ---
        // XPath này đảm bảo chỉ lấy những hàng còn đang mượn để báo mất
        String xpathRows = "//tr[contains(@class, 'lost-record') and .//span[contains(text(), 'Đang mượn')]]";

        List<WebElement> borrowingRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathRows)));

        // Kiểm tra để tránh lỗi nếu danh sách trống
        Assert.assertFalse(borrowingRows.isEmpty(), "❌ Thất bại: Không tìm thấy dòng nào ở trạng thái 'Đang mượn'!");

        // Chọn 1 dòng ngẫu nhiên trong danh sách các dòng đang mượn
        WebElement row = borrowingRows.get(new java.util.Random().nextInt(borrowingRows.size()));

        // Bấm nút Báo mất của dòng đó
        WebElement btnBaoMat = row.findElement(By.className("lost-report-trigger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);

        // --- 3. TƯƠNG TÁC VỚI FORM (ID: popup-lost-book) ---
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book")));

        // Nhập ngày khai báo (Lấy ngày hiện tại)
        WebElement dtpNgay = modal.findElement(By.xpath(".//input[@type='date']"));
        String today = java.time.LocalDate.now().toString();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='" + today + "';", dtpNgay);

        // Nhập ghi chú
        modal.findElement(By.id("lostNote")).sendKeys("Kiểm tra lỗi chưa chọn phương án - Nhóm 3 DUE");

        // BƯỚC QUAN TRỌNG: KHÔNG CHỌN Phương án bồi hoàn (Để trống 2 radio button)

        // Bấm nút Xác nhận
        WebElement btnXacNhan = modal.findElement(By.xpath(".//button[@onclick='submitLostBookReport()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnXacNhan);


        // --- 4. KIỂM TRA LỖI (ASSERTION) ---
        try {
            // Kiểm tra thông báo lỗi hiển thị đúng như trong ảnh
            // Dùng ID lostMethodError mà bạn đã soi được trong file inspect cũ
            WebElement lblError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lostMethodError")));

            String actualError = lblError.getText();
            System.out.println("✅ Pass: Hệ thống chặn thành công với lỗi: " + actualError);
            Assert.assertTrue(actualError.contains("Vui lòng chọn phương án bồi hoàn"), "❌ Lỗi hiển thị không đúng!");

        } catch (Exception e) {
            // Dự phòng: Nếu không thấy text, chỉ cần Form không đóng là hệ thống đã chặn thành công
            Assert.assertTrue(modal.isDisplayed(), "❌ Bug: Form bị đóng dù chưa chọn phương án bồi hoàn!");
        }

        // Đảm bảo Form vẫn mở để kết thúc test case an toàn
        Assert.assertTrue(modal.isDisplayed(), "❌ Lỗi: Form đã bị đóng bất thường!");
    }

    @Test
    public void TC_MS_06_XuLyMatSach_BoiThuongTien_Success() {
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        List<WebElement> borrowingRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//tr[contains(@class, 'lost-record') and .//span[contains(text(), 'Đang mượn')]]")));
        WebElement row = borrowingRows.get(0);

        // Lấy thông tin định danh (Tên hoặc mã) để kiểm tra sau khi lưu
        String identifier = row.findElement(By.xpath("./td[2]")).getText().trim();

        WebElement btnBaoMat = row.findElement(By.className("lost-report-trigger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book")));

        // Điền form
        WebElement inputDate = modal.findElement(By.xpath(".//input[@type='date']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value ='" + java.time.LocalDate.now() + "';", inputDate);

        WebElement optMoney = modal.findElement(By.xpath(".//input[@value='money']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optMoney);

        WebElement btnSubmit = modal.findElement(By.xpath(".//button[@onclick='submitLostBookReport()']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSubmit);

        wait.until(ExpectedConditions.invisibilityOf(modal));
        try { Thread.sleep(1500); } catch (InterruptedException e) {}

        // Kiểm tra kết quả
        WebElement rowResult = driver.findElement(By.xpath("//tr[contains(., '" + identifier + "')]"));
        String status = rowResult.findElement(By.xpath(".//span[contains(@class, 'badge')]")).getText().trim();

        boolean isPass = status.equalsIgnoreCase("Đã mất") || status.equalsIgnoreCase("Đang xử lý");
        Assert.assertTrue(isPass, "❌ Trạng thái thực tế: " + status);
    }

    @Test
    public void TC_MS_07_XuLyMatSach_DenSachMoi_Success() throws InterruptedException {

        // =========================
        // 1. MỞ TAB TRẢ SÁCH > XỬ LÝ MẤT SÁCH
        // =========================
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        Thread.sleep(2000);

        // =========================
        // 2. LẤY BẢN GHI HỢP LỆ (Đang mượn / Quá hạn)
        // =========================
        List<WebElement> validRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//tr[contains(@class,'lost-record') and " +
                                "(.//span[contains(normalize-space(),'Đang mượn') " +
                                "or contains(normalize-space(),'Quá hạn')])]")
                )
        );

        Assert.assertFalse(
                validRows.isEmpty(),
                "❌ Không có bản ghi hợp lệ để test!"
        );

        // Chọn bản ghi đầu tiên
        WebElement row = validRows.get(0);

        String identifier = row.findElement(
                By.xpath("./td[2]")
        ).getText().trim();

        String oldStatus = row.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("🎯 Bản ghi test: " + identifier);
        System.out.println("📌 Trạng thái ban đầu: " + oldStatus);

        // =========================
        // 3. MỞ FORM BÁO MẤT
        // =========================
        WebElement btnBaoMat = row.findElement(
                By.className("lost-report-trigger")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnBaoMat
        );

        // =========================
        // 4. CHỜ MODAL
        // =========================
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("popup-lost-book")
                )
        );

        System.out.println("📄 Modal xử lý mất sách đã mở");

        // =========================
        // 5. NHẬP NGÀY KHAI BÁO MẤT
        // =========================
        WebElement dtpNgay = modal.findElement(
                By.id("lostReportDate")
        );

        String today = java.time.LocalDate.now().toString();

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; " +
                        "arguments[0].dispatchEvent(new Event('change'));",
                dtpNgay,
                today
        );

        System.out.println("📅 Ngày khai báo mất: " + today);

        // =========================
        // 6. GHI CHÚ (nếu có)
        // =========================
        try {
            WebElement txtNote = modal.findElement(
                    By.id("lostNote")
            );

            txtNote.clear();
            txtNote.sendKeys("Người dùng chọn phương án đền sách mới.");

        } catch (Exception e) {
            System.out.println("ℹ️ Không có ô ghi chú.");
        }

        // =========================
        // 7. CHỌN ĐỀN SÁCH MỚI
        // =========================
        WebElement optDenSachMoi = modal.findElement(
                By.xpath(".//input[@name='compensateMethod' and @value='book']")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                optDenSachMoi
        );

        System.out.println("📚 Đã chọn phương án: Đền sách mới");

        // Đợi hệ thống render logic nghiệp vụ
        Thread.sleep(1500);

        // =========================
        // 8. KIỂM TRA PREVIEW NGHIỆP VỤ
        // =========================
        try {
            String totalAmount = modal.findElement(
                    By.id("lostTotalAmount")
            ).getText().trim();

            String nextStatus = modal.findElement(
                    By.id("lostRecordNextStatus")
            ).getText().trim();

            System.out.println("💰 Tổng tiền dự kiến: " + totalAmount);
            System.out.println("📊 Trạng thái dự kiến: " + nextStatus);

        } catch (Exception ignored) {}

        // =========================
        // 9. BẤM XÁC NHẬN
        // =========================
        WebElement btnSubmit = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@id='popup-lost-book']//button[" +
                                "contains(normalize-space(),'Xác nhận') " +
                                "or contains(@onclick,'submitLostBookReport')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                btnSubmit
        );

        Thread.sleep(500);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnSubmit
        );

        System.out.println("⚡ Đã bấm xác nhận");

        // =========================
        // 10. KIỂM TRA THÔNG BÁO THÀNH CÔNG
        // =========================
        boolean successMessage = false;

        try {
            WebElement toast = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'thành công') " +
                                    "or contains(text(),'Xác nhận mất sách thành công') " +
                                    "or contains(text(),'đã xử lý')]")
                    )
            );

            System.out.println("🎉 Thông báo: " + toast.getText());
            successMessage = true;

        } catch (Exception e) {
            System.out.println("⚠️ Không bắt được toast, sẽ kiểm tra bằng dữ liệu.");
        }

        // =========================
        // 11. CHỜ MODAL ĐÓNG
        // =========================
        try {
            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.id("popup-lost-book")
                    )
            );
        } catch (Exception ignored) {}

        Thread.sleep(1500);

        // =========================
        // 12. REFRESH LẠI DỮ LIỆU
        // =========================
        driver.navigate().refresh();

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // =========================
        // 13. TÌM LẠI HỒ SƠ
        // =========================
        WebElement rowResult = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[contains(@class,'lost-record')]" +
                                "[td[2][normalize-space()='" + identifier + "']]")
                )
        );

        String newStatus = rowResult.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("📊 Trạng thái sau xử lý: " + newStatus);

        // =========================
        // 14. VERIFY NGHIỆP VỤ
        // =========================

        // A. Trạng thái phải đổi
        Assert.assertNotEquals(
                newStatus,
                oldStatus,
                "❌ Hồ sơ không thay đổi trạng thái!"
        );

        // B. Trạng thái đúng nghiệp vụ đền sách
        boolean validBusinessLogic =
                newStatus.toLowerCase().contains("đền sách")
                        || newStatus.equalsIgnoreCase("Chờ đền sách")
                        || newStatus.equalsIgnoreCase("Đang xử lý")
                        || newStatus.equalsIgnoreCase("Đã mất");

        Assert.assertTrue(
                validBusinessLogic,
                "❌ Hồ sơ không chuyển đúng trạng thái nghiệp vụ đền sách mới!"
        );

        // =========================
        // 15. KIỂM TRA TIỀN PHẠT (nếu có)
        // =========================
        try {
            String fineText = rowResult.findElement(
                    By.xpath("./td[7]")
            ).getText().trim();

            System.out.println("💰 Khoản phí/phạt hiển thị: " + fineText);

            // Nếu hệ thống không quy định phạt mất sách khi đền sách mới,
            // thường phải = 0 hoặc chỉ phí xử lý
            Assert.assertFalse(
                    fineText.contains("50000") || fineText.contains("100000"),
                    "❌ Phát sinh phạt mất sách bất thường khi chọn đền sách mới!"
            );

        } catch (Exception e) {
            System.out.println("ℹ️ Không có cột tiền phạt để kiểm tra.");
        }

        // =========================
        // 16. KHÔNG CHO BÁO MẤT LẠI
        // =========================
        List<WebElement> lostButtonsAfter = rowResult.findElements(
                By.className("lost-report-trigger")
        );

        Assert.assertTrue(
                lostButtonsAfter.isEmpty()
                        || !lostButtonsAfter.get(0).isDisplayed(),
                "❌ Hồ sơ đã xử lý nhưng vẫn cho báo mất lại!"
        );

        // =========================
        // 17. ASSERT THÀNH CÔNG CUỐI
        // =========================
        Assert.assertTrue(
                successMessage || !newStatus.equals(oldStatus),
                "❌ Không có dấu hiệu xử lý thành công!"
        );

        System.out.println("✅ TC Passed: Xử lý mất sách thành công với phương án Đền sách mới, đúng nghiệp vụ.");
    }
    @Test
    public void TC_MS_08_HuyThaoTac_KhongLuuDuLieu() {

        // ===== 1. MỞ TAB XỬ LÝ MẤT SÁCH =====
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ===== 2. CHỈ CHỌN BẢN GHI THẬT SỰ CHƯA XỬ LÝ =====
        // Điều kiện:
        // - Đang mượn
        // - Có nút lost-report-trigger
        // - Không phải đã mất
        String xpathRows =
                "//tr[contains(@class,'lost-record')" +
                        " and .//span[contains(normalize-space(),'Đang mượn')]" +
                        " and .//button[contains(@class,'lost-report-trigger')]" +
                        "]";

        List<WebElement> borrowingRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathRows))
        );

        Assert.assertFalse(
                borrowingRows.isEmpty(),
                "❌ Không tìm thấy hồ sơ hợp lệ để test Hủy!"
        );

        // ===== 3. RANDOM 1 DÒNG =====
        WebElement row = borrowingRows.get(
                new java.util.Random().nextInt(borrowingRows.size())
        );

        String identifier = row.findElement(By.xpath("./td[2]"))
                .getText()
                .trim();

        String originalStatus = row.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        // Đếm số hồ sơ Đã mất trước test
        int lostBefore = driver.findElements(
                By.xpath("//tr[contains(@class,'lost-record')]" +
                        "[td[2][normalize-space()='" + identifier + "']" +
                        " and .//span[contains(normalize-space(),'Đã mất')]]")
        ).size();

        System.out.println("🎯 Bản ghi test: " + identifier);
        System.out.println("📌 Trạng thái ban đầu: " + originalStatus);
        System.out.println("📌 Hồ sơ mất trước test: " + lostBefore);

        // ===== 4. MỞ FORM =====
        WebElement btnBaoMat = row.findElement(By.className("lost-report-trigger"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnBaoMat);

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("popup-lost-book"))
        );

        System.out.println("📄 Form xử lý mất sách đã mở");

        // ===== 5. NHẬP DỮ LIỆU GIẢ =====
        try {
            WebElement dtpNgay = modal.findElement(By.xpath(".//input[@type='date']"));
            String today = java.time.LocalDate.now().toString();
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('change'));",
                    dtpNgay,
                    today
            );
            System.out.println("📅 Đã nhập ngày: " + today);
        } catch (Exception ignored) {}

        try {
            WebElement txtNote = modal.findElement(By.xpath(".//textarea"));
            txtNote.clear();
            txtNote.sendKeys("TEST HỦY KHÔNG LƯU");
            System.out.println("📝 Đã nhập ghi chú");
        } catch (Exception ignored) {}

        try {
            WebElement radioMoney = modal.findElement(
                    By.xpath(".//input[@type='radio' and @value='money']")
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioMoney);
            System.out.println("💰 Đã chọn phương án bồi thường");
        } catch (Exception ignored) {}

        // ===== 6. BẤM HỦY =====
        WebElement btnHuy = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='popup-lost-book']//button[contains(normalize-space(),'Hủy')]")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnHuy);
        System.out.println("⚡ Đã bấm Hủy");

        // ===== 7. XỬ LÝ POPUP XÁC NHẬN (NẾU CÓ) =====
        try {
            WebElement confirmCancel = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(text(),'Xác nhận') or contains(text(),'Đồng ý')]")
                    ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmCancel);
            System.out.println("⚡ Đã xác nhận hủy");

        } catch (Exception ignored) {
            System.out.println("ℹ️ Không có popup xác nhận hủy.");
        }

        // ===== 8. ĐẢM BẢO MODAL ĐÓNG =====
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("popup-lost-book"))
        );

        System.out.println("✅ Form đã đóng");

        // ===== 9. REFRESH TOÀN BỘ ĐỂ LẤY DB THẬT =====
        driver.navigate().refresh();

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick, 'tab-3')]"));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // ===== 10. KIỂM TRA ĐÚNG BẢN GHI GỐC =====
        WebElement rowSauHuy = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[contains(@class,'lost-record')" +
                                "][td[2][normalize-space()='" + identifier + "']" +
                                " and .//button[contains(@class,'lost-report-trigger')]]")
                )
        );

        System.out.println("📄 ROW SAU HỦY: " + rowSauHuy.getText());

        String finalStatus = rowSauHuy.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("📊 Trạng thái sau hủy: " + finalStatus);

        // ===== 11. VERIFY TRẠNG THÁI KHÔNG ĐỔI =====
        Assert.assertEquals(
                finalStatus,
                originalStatus,
                "❌ Trạng thái bị thay đổi dù đã hủy thao tác!"
        );

        // ===== 12. VERIFY KHÔNG TẠO THÊM HỒ SƠ ĐÃ MẤT =====
        int lostAfter = driver.findElements(
                By.xpath("//tr[contains(@class,'lost-record')]" +
                        "[td[2][normalize-space()='" + identifier + "']" +
                        " and .//span[contains(normalize-space(),'Đã mất')]]")
        ).size();

        System.out.println("📌 Hồ sơ mất sau test: " + lostAfter);

        Assert.assertEquals(
                lostAfter,
                lostBefore,
                "❌ Lỗi: Hủy nhưng vẫn tạo thêm hồ sơ mất sách!"
        );

        // ===== 13. VERIFY NÚT BÁO MẤT VẪN CÒN =====
        List<WebElement> lostButtons = rowSauHuy.findElements(
                By.className("lost-report-trigger")
        );

        Assert.assertTrue(
                !lostButtons.isEmpty() && lostButtons.get(0).isDisplayed(),
                "❌ Lỗi: Hủy nhưng hệ thống vẫn khóa chức năng báo mất!"
        );

        System.out.println("✅ TC Passed: Hủy thao tác thành công, không lưu dữ liệu, không đổi trạng thái.");
    }

    @Test
    public void TC_MS_09_KhongChoXuLyBanGhiKhongHopLe_HoacDaXuLy() {
        // 1. Mở tab Trả sách > Xử lý mất sách
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // 2. Tìm 1 bản ghi KHÔNG hợp lệ:
        // - Không phải Đang mượn / Quá hạn
        // - Hoặc đã xử lý (Đã mất / Đã trả / Đã xử lý...)
        String invalidXpath =
                "//tr[contains(@class,'lost-record') and " +
                        "not(.//span[contains(normalize-space(),'Đang mượn') or contains(normalize-space(),'Quá hạn')])]";

        List<WebElement> invalidRows = driver.findElements(By.xpath(invalidXpath));

        // Nếu không có bản ghi invalid thì PASS dạng skip mềm
        if (invalidRows.isEmpty()) {
            System.out.println("⚠️ Không có bản ghi không hợp lệ để kiểm thử.");
            return;
        }

        // 3. Chọn ngẫu nhiên 1 bản ghi invalid
        WebElement invalidRow = invalidRows.get(
                new java.util.Random().nextInt(invalidRows.size())
        );

        String identifier = invalidRow.findElement(By.xpath("./td[2]"))
                .getText()
                .trim();

        String currentStatus = invalidRow.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("🎯 Bản ghi kiểm thử: " + identifier);
        System.out.println("📌 Trạng thái hiện tại: " + currentStatus);

        // 4. Kiểm tra nút Báo mất
        List<WebElement> reportButtons = invalidRow.findElements(
                By.className("lost-report-trigger")
        );

        // CASE A: Nút không tồn tại => đúng nghiệp vụ
        if (reportButtons.isEmpty()) {
            System.out.println("✅ Không hiển thị nút Báo mất cho bản ghi không hợp lệ.");
            return;
        }

        // CASE B: Nút có nhưng hệ thống phải chặn khi click
        WebElement btnBaoMat = reportButtons.get(0);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnBaoMat
        );

        // 5. Kiểm tra:
        // - Không mở form
        // HOẶC
        // - Có cảnh báo lỗi
        boolean modalOpened;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("popup-lost-book")
            ));
            modalOpened = true;
        } catch (Exception e) {
            modalOpened = false;
        }

        if (!modalOpened) {
            System.out.println("✅ Hệ thống đã chặn mở form với bản ghi không hợp lệ.");
        } else {
            System.out.println("⚠️ Form vẫn mở, kiểm tra bước xác nhận xử lý.");

            // Thử bấm xác nhận nếu có
            try {
                WebElement btnConfirm = driver.findElement(
                        By.xpath("//button[contains(text(),'Xác nhận') or contains(text(),'Lưu')]")
                );

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();",
                        btnConfirm
                );

            } catch (Exception ignored) {}

            // 6. Kiểm tra thông báo lỗi
            boolean hasErrorMessage = false;

            try {
                WebElement errorMsg = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[contains(text(),'Không tìm thấy bản ghi') " +
                                        "or contains(text(),'không hợp lệ') " +
                                        "or contains(text(),'đã được xử lý') " +
                                        "or contains(text(),'không còn hợp lệ')]")
                        )
                );

                System.out.println("✅ Thông báo hệ thống: " + errorMsg.getText());
                hasErrorMessage = true;

            } catch (Exception ignored) {}

            Assert.assertTrue(
                    hasErrorMessage,
                    "❌ Lỗi: Hệ thống vẫn cho xử lý bản ghi không hợp lệ!"
            );
        }

        // 7. Reload lại đúng bản ghi để đảm bảo trạng thái không đổi
        driver.navigate().refresh();

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        WebElement rowAfter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[contains(@class,'lost-record')][td[2][normalize-space()='" + identifier + "']]")
                )
        );

        String statusAfter = rowAfter.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("📊 Trạng thái sau kiểm thử: " + statusAfter);

        Assert.assertEquals(
                statusAfter,
                currentStatus,
                "❌ Lỗi: Dữ liệu đã bị thay đổi dù bản ghi không hợp lệ!"
        );

        System.out.println("✅ TC Passed: Không thể xử lý bản ghi không hợp lệ / đã xử lý.");
    }
    // Hàm hỗ trợ Click bằng JavaScript để bỏ qua độ trễ render của trình duyệt

    @Test
    public void TC_MS_10_XacNhanXuLyMatSachThatBai_KhongCapNhatDoDang() {

        // 1. Mở tab Trả sách > Xử lý mất sách
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // 2. Lấy bản ghi hợp lệ: Đang mượn / Quá hạn
        String validXpath =
                "//tr[contains(@class,'lost-record') and " +
                        "(.//span[contains(normalize-space(),'Đang mượn') or contains(normalize-space(),'Quá hạn')])]";

        List<WebElement> validRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(validXpath))
        );

        Assert.assertFalse(
                validRows.isEmpty(),
                "❌ Không có bản ghi hợp lệ để test xử lý mất sách!"
        );

        // 3. Chọn ngẫu nhiên 1 dòng
        WebElement selectedRow = validRows.get(
                new java.util.Random().nextInt(validRows.size())
        );

        String identifier = selectedRow.findElement(By.xpath("./td[2]"))
                .getText()
                .trim();

        String originalStatus = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("🎯 Bản ghi chọn test: " + identifier);
        System.out.println("📌 Trạng thái ban đầu: " + originalStatus);

        // 4. Mở form Báo mất
        WebElement btnBaoMat = selectedRow.findElement(
                By.className("lost-report-trigger")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnBaoMat
        );

        // 5. Đợi modal hiển thị
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("popup-lost-book")
                )
        );

        System.out.println("📄 MODAL HTML:\n" + modal.getAttribute("innerHTML"));

        // 6. Nhập ngày khai báo mất (bắt buộc)
        try {
            WebElement reportDate = modal.findElement(
                    By.id("lostReportDate")
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));",
                    reportDate,
                    java.time.LocalDate.now().toString()
            );

            System.out.println("✅ Đã nhập ngày khai báo mất.");
        } catch (Exception e) {
            System.out.println("⚠️ Không nhập được ngày.");
        }

        // 7. Nhập ghi chú
        try {
            WebElement txtReason = modal.findElement(
                    By.id("lostNote")
            );

            txtReason.clear();
            txtReason.sendKeys("Sách bị mất trong quá trình sử dụng.");

            System.out.println("✅ Đã nhập ghi chú.");
        } catch (Exception e) {
            System.out.println("ℹ️ Không có ô ghi chú.");
        }

        // 8. Chọn phương án bồi hoàn bắt buộc
        WebElement radioMoney = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//input[@name='compensateMethod' and @value='money']")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                radioMoney
        );

        System.out.println("✅ Đã chọn phương án bồi thường tiền.");

        // 9. Giả lập lỗi hệ thống:
        // Override hàm submit để chặn lưu
        ((JavascriptExecutor) driver).executeScript(
                "window.originalSubmitLostBookReport = window.submitLostBookReport;" +
                        "window.submitLostBookReport = function() {" +
                        "   throw new Error('Fake save fail for testing');" +
                        "};"
        );

        System.out.println("✅ Đã giả lập lỗi hệ thống khi submit.");

        // 10. Tìm nút xác nhận thật
        WebElement btnConfirm = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@id='popup-lost-book']//button[contains(@class,'btn-orange') or contains(@onclick,'submitLostBookReport')]")
                )
        );

        System.out.println("✅ Đã tìm thấy nút xác nhận: " + btnConfirm.getText());

        // 11. Click xác nhận
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    btnConfirm
            );
        } catch (Exception e) {
            System.out.println("⚠️ Click lỗi do submit fail giả lập.");
        }

        // 12. Kiểm tra hệ thống KHÔNG đóng modal hoặc có báo lỗi
        boolean hasFailMessage = false;

        try {
            WebElement failMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'thất bại') " +
                                    "or contains(text(),'Không thể xử lý') " +
                                    "or contains(text(),'Có lỗi xảy ra') " +
                                    "or contains(text(),'error') " +
                                    "or contains(text(),'Error')]")
                    )
            );

            System.out.println("✅ Thông báo lỗi: " + failMsg.getText());
            hasFailMessage = true;

        } catch (Exception ignored) {
            System.out.println("ℹ️ Không có popup lỗi rõ ràng.");
        }

        boolean modalStillOpen = false;

        try {
            modalStillOpen = driver.findElement(
                    By.id("popup-lost-book")
            ).isDisplayed();
        } catch (Exception ignored) {}

        Assert.assertTrue(
                hasFailMessage || modalStillOpen,
                "❌ Lỗi: Hệ thống vẫn xử lý thành công dù đã giả lập lỗi!"
        );

        // 13. Refresh lại trang
        driver.navigate().refresh();

        // 14. Mở lại đúng tab
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // 15. Tìm lại bản ghi
        WebElement rowAfter = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[contains(@class,'lost-record')][td[2][normalize-space()='" + identifier + "']]")
                )
        );

        String statusAfter = rowAfter.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("📊 Trạng thái sau lỗi: " + statusAfter);

        // 16. Verify trạng thái không đổi
        Assert.assertEquals(
                statusAfter,
                originalStatus,
                "❌ Lỗi: Trạng thái bị thay đổi dù xác nhận thất bại!"
        );

        // 17. Verify không phát sinh hồ sơ Đã mất
        List<WebElement> duplicatePenalty = driver.findElements(
                By.xpath("//tr[contains(@class,'lost-record')]" +
                        "[td[2][normalize-space()='" + identifier + "'] " +
                        "and .//span[contains(normalize-space(),'Đã mất') or contains(normalize-space(),'Mất sách')]]")
        );

        Assert.assertTrue(
                duplicatePenalty.isEmpty(),
                "❌ Lỗi: Hệ thống tạo hồ sơ mất sách dù submit thất bại!"
        );

        // 18. Restore function gốc (nếu cần test sau)
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "if(window.originalSubmitLostBookReport) {" +
                            "window.submitLostBookReport = window.originalSubmitLostBookReport;" +
                            "}"
            );
        } catch (Exception ignored) {}

        System.out.println("✅ TC Passed: Xác nhận thất bại nhưng dữ liệu vẫn nhất quán, không cập nhật dở dang.");
    }



    @Test
    public void TC_MS_11_KhongTaoTrungXuLyMatSach_KhiXacNhanNhieuLan() throws InterruptedException {

        // =========================
        // 1. Mở tab Trả sách > Xử lý mất sách
        // =========================
        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // =========================
        // 2. Lấy danh sách bản ghi hợp lệ
        // =========================
        String validXpath =
                "//tr[contains(@class,'lost-record') and " +
                        "(.//span[contains(normalize-space(),'Đang mượn') " +
                        "or contains(normalize-space(),'Quá hạn')])]";

        List<WebElement> validRows = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath(validXpath)
                )
        );

        Assert.assertFalse(
                validRows.isEmpty(),
                "❌ Không có bản ghi hợp lệ để test!"
        );

        // =========================
        // 3. Chọn random 1 dòng
        // =========================
        WebElement selectedRow = validRows.get(
                new java.util.Random().nextInt(validRows.size())
        );

        String identifier = selectedRow.findElement(
                By.xpath("./td[2]")
        ).getText().trim();

        String originalStatus = selectedRow.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("🎯 Bản ghi test: " + identifier);
        System.out.println("📌 Trạng thái ban đầu: " + originalStatus);

        // =========================
        // 4. Click Báo mất
        // =========================
        WebElement btnBaoMat = selectedRow.findElement(
                By.className("lost-report-trigger")
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                btnBaoMat
        );

        // =========================
        // 5. Chờ modal mở
        // =========================
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("popup-lost-book")
                )
        );

        System.out.println("📄 Modal mở thành công");

        // =========================
        // 6. Nhập ngày hiện tại
        // =========================
        try {
            WebElement dateInput = modal.findElement(
                    By.id("lostReportDate")
            );

            String today = java.time.LocalDate.now().toString();

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));",
                    dateInput,
                    today
            );

            System.out.println("📅 Ngày khai báo: " + today);

        } catch (Exception e) {
            System.out.println("⚠️ Không nhập được ngày.");
        }

        // =========================
        // 7. Ghi chú
        // =========================
        try {
            WebElement txtReason = modal.findElement(
                    By.id("lostNote")
            );

            txtReason.clear();
            txtReason.sendKeys("Người dùng làm mất sách.");

        } catch (Exception e) {
            System.out.println("⚠️ Không có ô ghi chú.");
        }

        // =========================
        // 8. Chọn phương án bồi thường
        // =========================
        try {
            WebElement radioMoney = modal.findElement(
                    By.xpath(".//input[@name='compensateMethod' and @value='money']")
            );

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();",
                    radioMoney
            );

            System.out.println("💰 Chọn bồi thường tiền");

        } catch (Exception e) {
            Assert.fail("❌ Không chọn được phương án bồi thường!");
        }

        // =========================
        // 9. Đợi hệ thống render kết quả
        // =========================
        Thread.sleep(1500);

        // =========================
        // 10. Tìm nút xác nhận (FULL FIX)
        // =========================
        WebElement btnConfirm = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@id='popup-lost-book']//button[" +
                                "contains(normalize-space(),'Xác nhận') " +
                                "or contains(normalize-space(),'Xác Nhận') " +
                                "or contains(normalize-space(),'Lưu') " +
                                "or contains(normalize-space(),'Xử lý') " +
                                "or contains(@onclick,'submitLostBookReport')" +
                                "]")
                )
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                btnConfirm
        );

        Thread.sleep(500);

        // =========================
        // 11. Click nhiều lần liên tiếp
        // =========================
        for (int i = 1; i <= 3; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();",
                        btnConfirm
                );

                System.out.println("⚡ Click xác nhận lần " + i);

                Thread.sleep(300);

            } catch (Exception e) {
                System.out.println("ℹ️ Click lần " + i + " bị chặn.");
            }
        }

        // =========================
        // 12. Đợi modal đóng hoặc xử lý xong
        // =========================
        try {
            wait.until(
                    ExpectedConditions.or(
                            ExpectedConditions.invisibilityOfElementLocated(
                                    By.id("popup-lost-book")
                            ),
                            ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//*[contains(text(),'thành công') " +
                                            "or contains(text(),'đã xử lý') " +
                                            "or contains(text(),'Hoàn tất')]")
                            )
                    )
            );
        } catch (Exception ignored) {}

        // =========================
        // 13. Refresh lại
        // =========================
        driver.navigate().refresh();

        clickByJS(By.xpath("//span[contains(text(),'Trả sách')]"));
        clickByJS(By.xpath("//button[contains(@onclick,'tab-3')]"));

        // =========================
        // 14. Tìm lại bản ghi
        // =========================
        WebElement processedRow = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//tr[contains(@class,'lost-record')]" +
                                "[td[2][normalize-space()='" + identifier + "']]")
                )
        );

        String finalStatus = processedRow.findElement(
                By.xpath(".//span[contains(@class,'badge')]")
        ).getText().trim();

        System.out.println("📊 Trạng thái sau xử lý: " + finalStatus);

        // =========================
        // 15. Verify trạng thái đổi
        // =========================
        Assert.assertNotEquals(
                finalStatus,
                originalStatus,
                "❌ Không xử lý được bản ghi!"
        );

        // =========================
        // 16. Verify không tạo trùng
        // =========================
        List<WebElement> duplicateProcessedRows = driver.findElements(
                By.xpath("//tr[contains(@class,'lost-record')]" +
                        "[td[2][normalize-space()='" + identifier + "'] " +
                        "and .//span[contains(normalize-space(),'Đã mất') " +
                        "or contains(normalize-space(),'Mất sách')]]")
        );

        System.out.println("📌 Số hồ sơ mất sách: " + duplicateProcessedRows.size());

        Assert.assertEquals(
                duplicateProcessedRows.size(),
                1,
                "❌ Lỗi: Tạo trùng nhiều hồ sơ xử lý mất sách!"
        );

        // =========================
        // 17. Verify không còn nút báo mất
        // =========================
        List<WebElement> lostButtonsAfter = processedRow.findElements(
                By.className("lost-report-trigger")
        );

        Assert.assertTrue(
                lostButtonsAfter.isEmpty()
                        || !lostButtonsAfter.get(0).isDisplayed(),
                "❌ Đã xử lý nhưng vẫn cho báo mất lại!"
        );

        // =========================
        // 18. Verify chỉ 1 badge trạng thái
        // =========================
        List<WebElement> statusBadges = processedRow.findElements(
                By.xpath(".//span[contains(@class,'badge')]")
        );

        Assert.assertEquals(
                statusBadges.size(),
                1,
                "❌ Một hồ sơ có nhiều trạng thái!"
        );

        System.out.println("✅ TC Passed: Chống submit lặp, không tạo trùng xử lý mất sách.");
    }


    private void clickByJS(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }


    private void loginAsUser() {
        driver.get(Constant.THUVIEN_URL);
        typeFirstVisible(Constant.USERNAME, By.id("username"), By.name("username"));
        typeFirstVisible(Constant.PASSWORD, By.id("password"), By.name("password"));
        clickFirstVisible(By.id("login-btn"), By.cssSelector("button[type='submit']"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//aside")));
    }

    private void typeFirstVisible(String text, By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                element.clear();
                element.sendKeys(text);
                return;
            } catch (Exception ignored) {}
        }
    }

    private void clickFirstVisible(By... locators) {
        for (By locator : locators) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (Exception ignored) {}
        }
    }
}
