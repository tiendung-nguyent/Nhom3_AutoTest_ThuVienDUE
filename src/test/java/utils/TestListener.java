package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import common.Constant;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static ExtentReports extent = ExtentReportManager.getExtentReports();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        System.out.println("--- STARTING TEST SUITE: " + context.getName() + " ---");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("--- FINISHED TEST SUITE: " + context.getName() + " ---");
        extent.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        
        // Create test in report
        ExtentTest extentTest = extent.createTest(methodName, description);
        test.set(extentTest);
        
        // Log automatic start info
        test.get().log(Status.INFO, MarkupHelper.createLabel("STARTED: " + methodName, ExtentColor.BLUE));
        
        // Try to log browser info automatically if driver is available
        try {
            if (Constant.WEBDRIVER != null && Constant.WEBDRIVER instanceof RemoteWebDriver) {
                Capabilities caps = ((RemoteWebDriver) Constant.WEBDRIVER).getCapabilities();
                String browserInfo = caps.getBrowserName() + " " + caps.getBrowserVersion();
                test.get().assignDevice(browserInfo);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, MarkupHelper.createLabel("PASSED: " + result.getName(), ExtentColor.GREEN));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, MarkupHelper.createLabel("FAILED: " + result.getName(), ExtentColor.RED));
        test.get().log(Status.FAIL, result.getThrowable());

        // Auto screenshot
        String screenshotPath = CaptureHelper.captureScreenshot(result.getName());
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath);
            test.get().log(Status.INFO, "Screenshot captured for failure.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, MarkupHelper.createLabel("SKIPPED: " + result.getName(), ExtentColor.ORANGE));
    }
}
