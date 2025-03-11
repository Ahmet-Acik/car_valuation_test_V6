package org.example.valuation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for car valuation functionality.
 */
public class CarValuationTest {

    private static final Logger logger = LogManager.getLogger(CarValuationTest.class);
    private static final String OUTPUT_FILE_PATH = "src/test/resources/car_output - V6.txt";
    private static final String EXPECTED_OUTPUT_FILE_PATH = "src/test/resources/expected_output.txt";
    private static final String CAR_CHECKING_URL = "https://car-checking.com/";
    private static final String CAR_REPORT_URL = "https://car-checking.com/report";
    private static WebDriver driver;

    @BeforeAll
    public static void setup() throws IOException {
        // Clear the output file before starting the tests
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
            writer.write("VARIANT_REG,MAKE,MODEL,YEAR\n");
        }
        // Run the data extraction step
        VehicleRegistrationExtractor.main(new String[]{});
    }

    @BeforeEach
    public void setUp() {
        // Initialize the WebDriver
        driver = DriverSingleton.getDriver();
    }

    @AfterEach
    public void tearDown() {
        DriverSingleton.closeDriver();
    }

    @ParameterizedTest
    @MethodSource("org.example.valuation.TestDataProvider#carDataProvider")
    public void testCarValuation(String registrationNumber, String expectedMessage) throws IOException {
        WebDriver driver = DriverSingleton.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        try {
            // Navigate to the car checking page
            driver.get(CAR_CHECKING_URL);
            CarCheckingPage carCheckingPage = new CarCheckingPage(driver);
            carCheckingPage.enterRegistrationNumber(registrationNumber);
            carCheckingPage.submitForm();

            // Check for error alert or report page
            boolean isErrorPresent = wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")),
                    ExpectedConditions.urlContains("report")
            )) != null;

            if (isErrorPresent) {
                try {
                    WebElement errorAlert = driver.findElement(By.cssSelector(".alert.alert-danger"));
                    if (errorAlert.isDisplayed()) {
                        String alertMessage = errorAlert.getText();
                        logger.info("Entered Registration Number: " + registrationNumber);
                        logger.info("Alert Message: " + alertMessage);
                        // Write the error details to the output file
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
                            writer.write(String.format("%s,%s%n", registrationNumber, alertMessage));
                        }
                        // Skip the rest of the test logic for this input
                        return;
                    }
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // No error alert found, proceed with the happy path
                }
            }

            // Navigate to the report page
            driver.get(CAR_REPORT_URL);
            CarReportPage carReportPage = new CarReportPage(driver);

            // Extract car details
            String regNumber = carReportPage.getRegistrationNumber();
            String make = carReportPage.getMake();
            String model = carReportPage.getModel();
            String year = carReportPage.getYearOfManufacture();

            // Write the results to the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
                writer.write(String.format("%s,%s,%s,%s%n", regNumber, make, model, year));
            }
        } finally {
            // driver.quit();
        }
    }

    @Test
    public void compareOutputWithExpected() throws IOException {
        List<String> actualOutput = Files.readAllLines(Paths.get(OUTPUT_FILE_PATH));
        List<String> expectedOutput = Files.readAllLines(Paths.get(EXPECTED_OUTPUT_FILE_PATH));

        // Assert that the number of lines in the actual output matches the expected output
        assertEquals(expectedOutput.size(), actualOutput.size(), "The number of lines in the actual output does not match the expected output.");

        for (int i = 0; i < expectedOutput.size(); i++) {
            String[] expectedFields = expectedOutput.get(i).split(",");
            String[] actualFields = actualOutput.get(i).split(",");

            // Assert that the number of fields in each line matches
            assertEquals(expectedFields.length, actualFields.length, "The number of fields in line " + (i + 1) + " does not match.");

            for (int j = 0; j < expectedFields.length; j++) {
                // Assert that each field's content matches
                assertEquals(expectedFields[j], actualFields[j], "Field " + (j + 1) + " in line " + (i + 1) + " does not match.");
            }
        }
    }

    @Test
    public void testInvalidRegistrationNumber() throws IOException {
        String invalidRegistrationNumber = "INVALID123";
        WebDriver driver = DriverSingleton.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        try {
            // Navigate to the car checking page
            driver.get(CAR_CHECKING_URL);
            CarCheckingPage carCheckingPage = new CarCheckingPage(driver);
            carCheckingPage.enterRegistrationNumber(invalidRegistrationNumber);
            carCheckingPage.submitForm();

            // Check for error alert
            WebElement errorAlert = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")));
            String alertMessage = errorAlert.getText();
            logger.info("Entered Invalid Registration Number: " + invalidRegistrationNumber);
            logger.info("Alert Message: " + alertMessage);

            // Assert that the error message is as expected
            assertEquals("The license plate number is not recognised", alertMessage);
        } finally {
            // driver.quit();
        }
    }

    @Test
public void testWebsiteDown() {
    WebDriver driver = DriverSingleton.getDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

    try {
        // Navigate to a non-existent page to simulate website down
        driver.get("https://car-checking.com/nonexistentpage");

        // Wait for the page to load and check the status code
        boolean is404 = wait.until(d -> {
            String pageSource = driver.getPageSource();
            return pageSource.contains("404") || pageSource.contains("Not Found");
        });

        // Assert that the 404 error is displayed
        assertEquals(true, is404, "Expected 404 Not Found error was not found.");
    } finally {
        // driver.quit();
    }
}
}