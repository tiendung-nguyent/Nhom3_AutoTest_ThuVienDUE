package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    private static ExtentReports extentReports;

    public synchronized static ExtentReports getExtentReports() {
        if (extentReports == null) {
            extentReports = new ExtentReports();
            
            // Output path
            ExtentSparkReporter spark = new ExtentSparkReporter("target/ExtentReport/ExtentReport.html");
            
            // Basic Configuration (Standard for ExtentReports 5.x)
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Automated Test Report - Nhom 3");
            spark.config().setReportName("Selenium Execution Results");
            spark.config().setTimelineEnabled(true);
            spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            spark.config().setEncoding("utf-8");

            extentReports.attachReporter(spark);
            
            // Automatic System Info
            extentReports.setSystemInfo("Project", "Nhom3_AutoTest_ThuVienDUE");
            extentReports.setSystemInfo("Environment", "Local");
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extentReports;
    }
}
