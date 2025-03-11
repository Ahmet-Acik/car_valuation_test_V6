package org.example.valuation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Singleton class to manage WebDriver instance.
 */
public class DriverSingleton {
    private static final Logger logger = LogManager.getLogger(DriverSingleton.class);
    private static WebDriver driver;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    private static final Duration IMPLICIT_WAIT_TIMEOUT = Duration.ofSeconds(2);

    private DriverSingleton() {}

    /**
     * Returns the singleton WebDriver instance.
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(CONFIG_FILE_PATH));
                String browser = properties.getProperty("browser");

                switch (browser.toLowerCase()) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        driver = new FirefoxDriver();
                        break;
                    case "safari":
                        WebDriverManager.safaridriver().setup();
                        driver = new SafariDriver();
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported browser: " + browser);
                }
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_TIMEOUT);
                logger.info("Initialized WebDriver for browser: " + browser);
            } catch (IOException e) {
                logger.error("Error loading configuration file", e);
            }
        }
        return driver;
    }

    /**
     * Closes the WebDriver instance.
     */
    public static void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            logger.info("Closed WebDriver instance");
        }
    }
}