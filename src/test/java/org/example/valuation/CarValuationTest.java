package org.example.valuation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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

public class CarValuationTest {

    @BeforeAll
    public static void setup() throws IOException {
        // Clear the output file before starting the tests
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/car_output - V6.txt"))) {
            writer.write("VARIANT_REG,MAKE,MODEL,YEAR\n");
        }

        // Run the data extraction step
        VehicleRegistrationExtractor.main(new String[]{});
    }

    @ParameterizedTest
    @MethodSource("org.example.valuation.TestDataProvider#carDataProvider")
    public void testCarValuation(String registrationNumber, String expectedMessage) throws IOException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Navigate to the car checking page
            driver.get("https://car-checking.com/");
            CarCheckingPage carCheckingPage = new CarCheckingPage(driver);
            carCheckingPage.enterRegistrationNumber(registrationNumber);
            carCheckingPage.submitForm();

            // Check for error alert
            boolean isErrorPresent = wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")),
                    ExpectedConditions.urlContains("report")
            ));

            if (isErrorPresent) {
                try {
                    WebElement errorAlert = driver.findElement(By.cssSelector(".alert.alert-danger"));
                    if (errorAlert.isDisplayed()) {
                        String alertMessage = errorAlert.getText();
                        System.out.println("Entered Registration Number: " + registrationNumber);
                        System.out.println("Alert Message: " + alertMessage);
                        // Write the error details to the output file
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/car_output - V6.txt", true))) {
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
            driver.get("https://car-checking.com/report");
            CarReportPage carReportPage = new CarReportPage(driver);

            // Extract car details
            String regNumber = carReportPage.getRegistrationNumber();
            String make = carReportPage.getMake();
            String model = carReportPage.getModel();
            String year = carReportPage.getYearOfManufacture();

            // Write the results to the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/car_output - V6.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s%n", regNumber, make, model, year));
            }
        } finally {
            driver.quit();
        }
    }

    @Test
public void compareOutputWithExpected() throws IOException {
    List<String> actualOutput = Files.readAllLines(Paths.get("src/test/resources/car_output - V6.txt"));
    List<String> expectedOutput = Files.readAllLines(Paths.get("src/test/resources/expected_output.txt"));

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
}