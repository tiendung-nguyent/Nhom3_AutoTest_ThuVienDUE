package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import common.Constant;
import java.util.List;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
public class nguoiDungPage {
    public String getNonReaderUserID() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".user-data-row")));

            List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector(".user-data-row"));

            for (WebElement row : rows) {
                ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", row);
                WebElement tagElement = row.findElement(By.cssSelector("td:nth-child(4) span.tag"));
                String userRole = tagElement.getText().trim();

                if (!userRole.equals("Độc giả")) {
                    String foundID = row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                    System.out.println("Tìm thấy mã: " + foundID + " với vai trò: " + userRole);
                    return foundID;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy bảng hoặc thẻ loại người dùng! " + e.getMessage());
        }
        return null;
    }
    public String getUnqualifiedReaderID() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".user-data-row")));
            List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector(".user-data-row"));

            for (WebElement row : rows) {
                ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", row);
                String userRole = row.findElement(By.cssSelector("td:nth-child(4) span.tag")).getText().trim();

                String graduationStatus = row.findElement(By.cssSelector("td:nth-child(6)")).getText().trim();

                if (userRole.equals("Độc giả") && graduationStatus.equals("Chưa tốt nghiệp")) {
                    String foundID = row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                    System.out.println("Tìm thấy: " + foundID + " | Loại: " + userRole + " | Trạng thái: " + graduationStatus);
                    return foundID;
                }
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy bảng người dùng hoặc lỗi locator: " + e.getMessage());
        }
        return null;
    }
    public String getQualifiedReaderID() {
        // Khởi tạo bộ đợi
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        try {
            // Đợi cho đến khi bảng dữ liệu xuất hiện
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".user-data-row")));
            List<WebElement> rows = Constant.WEBDRIVER.findElements(By.cssSelector(".user-data-row"));

            for (WebElement row : rows) {
                // Cuộn đến dòng hiện tại để đảm bảo phần tử hiển thị
                ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", row);

                // Lấy loại người dùng (Cột 4 - td:nth-child(4))
                String userRole = row.findElement(By.cssSelector("td:nth-child(4) span.tag")).getText().trim();

                // Lấy trạng thái tốt nghiệp (Cột 6 - td:nth-child(6))
                String graduationStatus = row.findElement(By.cssSelector("td:nth-child(6)")).getText().trim();

                // Điều kiện: Là Độc giả VÀ Đã tốt nghiệp
                if (userRole.equals("Độc giả") && graduationStatus.equals("Đã tốt nghiệp")) {
                    // Lấy Mã người dùng ở cột 2
                    String foundID = row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                    System.out.println("Tìm thấy mẫu test: " + foundID + " | Loại: " + userRole + " | Trạng thái: " + graduationStatus);
                    return foundID;
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi quét bảng người dùng: " + e.getMessage());
        }

        System.out.println("Không tìm thấy Độc giả nào đã tốt nghiệp trong bảng.");
        return null;
    }
}
