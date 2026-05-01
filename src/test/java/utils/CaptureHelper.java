package utils;

import common.Constant;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureHelper {

    public static String captureScreenshot(String screenshotName) {
        if (Constant.WEBDRIVER == null) {
            return null;
        }
        try {
            String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            TakesScreenshot ts = (TakesScreenshot) Constant.WEBDRIVER;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            String destination = System.getProperty("user.dir") + "/target/ExtentReport/Screenshots/" + screenshotName + "_" + dateName + ".png";
            File finalDestination = new File(destination);
            
            if (!finalDestination.getParentFile().exists()) {
                finalDestination.getParentFile().mkdirs();
            }
            
            FileHandler.copy(source, finalDestination);
            return destination;
        } catch (IOException e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
            return null;
        }
    }
}
