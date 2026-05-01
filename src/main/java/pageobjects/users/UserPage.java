package pageobjects.users;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pageobjects.GeneralPage;
import java.util.List;

public class UserPage extends GeneralPage {

    private final By userRow = By.cssSelector(".user-data-row");

    public String getNonReaderUserID() {
        try {
            List<WebElement> rows = driver.findElements(userRow);
            for (WebElement row : rows) {
                scrollToElement(row);
                String userRole = row.findElement(By.cssSelector("td:nth-child(4) span.tag")).getText().trim();
                if (!userRole.equals("Độc giả")) {
                    return row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUnqualifiedReaderID() {
        try {
            List<WebElement> rows = driver.findElements(userRow);
            for (WebElement row : rows) {
                scrollToElement(row);
                String userRole = row.findElement(By.cssSelector("td:nth-child(4) span.tag")).getText().trim();
                String graduationStatus = row.findElement(By.cssSelector("td:nth-child(6)")).getText().trim();
                if (userRole.equals("Độc giả") && graduationStatus.equals("Chưa tốt nghiệp")) {
                    return row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getQualifiedReaderID() {
        try {
            List<WebElement> rows = driver.findElements(userRow);
            for (WebElement row : rows) {
                scrollToElement(row);
                String userRole = row.findElement(By.cssSelector("td:nth-child(4) span.tag")).getText().trim();
                String graduationStatus = row.findElement(By.cssSelector("td:nth-child(6)")).getText().trim();
                if (userRole.equals("Độc giả") && graduationStatus.equals("Đã tốt nghiệp")) {
                    return row.findElement(By.cssSelector("td:nth-child(2)")).getText().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
