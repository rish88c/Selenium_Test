package com.autoamtiononthego;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class SeleniumTest {

    WebDriver driver;
    String appUrl = "https://admin-demo.virtocommerce.com/#/login";

    @Parameters({ "browser" })
    @BeforeClass
    public void setUp(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            // Set Chrome driver executable path
            System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedrivernew.exe");
            // Initialize ChromeDriver with options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);
        } else if (browser.equalsIgnoreCase("firefox")) {
            // You can add Firefox setup here if needed
        } else if (browser.equalsIgnoreCase("localchrome")) {
            // Initialize local ChromeDriver
            System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedrivernew.exe");
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        } else {
            throw new IllegalArgumentException("Invalid browser specified: " + browser);
        }
        // Open the application URL
        driver.get(appUrl);
    }

    @Test(priority = 0)
    public void testLoginWithInvalidCredentials() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        // Find login element and enter invalid credentials
        WebElement loginInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("login")));
        loginInput.sendKeys("Demo");
        driver.findElement(By.name("password")).sendKeys("demo");
        driver.findElement(By.xpath("//button[text()='Log in']")).click();
        // Verify error message
        WebElement errorMessage = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='form-error']")));
        Assert.assertEquals(errorMessage.getText().trim(), "The login or password is incorrect.");
        // Refresh the page
        driver.navigate().refresh();
    }

    @Test(priority = 1)
    public void testLoginWithValidCredentials() {
        WebDriverWait wait = new WebDriverWait(driver, 20);
        // Find login element and enter valid credentials
        WebElement loginInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("login")));
        loginInput.sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("store");
        driver.findElement(By.xpath("//button[text()='Log in']")).click();
        // Wait for the 'New password' label to appear
        WebElement newPasswordLabel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'New password:')]")));
        // Verify the presence of 'New password' label
        Assert.assertTrue(newPasswordLabel.isDisplayed(), "'New password' label is not displayed.");
    }

    @Test(priority = 2)
    public void testFailingScenario() {
        Assert.assertTrue(false, "This test intentionally fails for demonstration.");
    }

    @AfterClass
    public void tearDown() {
        // Quit the WebDriver instance
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterMethod
    public void captureScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            try {
                // Capture screenshot on test failure
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);
                String screenshotPath = Paths.get("").toAbsolutePath().toString() + "/Screenshots/" + result.getName()
                        + ".png";
                File target = new File(screenshotPath);
                FileUtils.copyFile(source, target);
                System.out.println("Screenshot captured at: " + screenshotPath);
            } catch (IOException e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }
}
