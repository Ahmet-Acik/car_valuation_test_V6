package org.example.valuation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains tests for the Car Valuation application.
 * It includes tests for valid and invalid registration numbers,
 * as well as a comparison of the actual output with the expected output.
 */
@TestMethodOrder(OrderAnnotation.class)
public class CarValuationTest {

    private static final Logger logger = LogManager.getLogger(CarValuationTest.class);
    private static final String OUTPUT_FILE_PATH = "src/test/resources/car_output - V6.txt";
    private static final String EXPECTED_OUTPUT_FILE_PATH = "src/test/resources/expected_output.txt";
    private static final String CAR_CHECKING_URL = "https://car-checking.com/";
    private static final String CAR_REPORT_URL = "https://car-checking.com/report";
    private static WebDriver driver;

    /**
     * Sets up the test environment by initializing the output file and extracting registration numbers.
     *
     * @throws IOException if an I/O error occurs
     */
    @BeforeAll
    public static void setup() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH))) {
            writer.write("VARIANT_REG,MAKE,MODEL,YEAR\n");
        }
        VehicleRegistrationExtractor.extractAndWriteRegistrationNumbers();
    }

    /**
     * Initializes the WebDriver before each test.
     */
    @BeforeEach
    public void setUp() {
        driver = DriverSingleton.getDriver();
    }

    /**
     * Closes the WebDriver after each test.
     */
    @AfterEach
    public void tearDown() {
        DriverSingleton.closeDriver();
    }

    /**
     * Navigates to the car checking page and enters the given registration number.
     *
     * @param registrationNumber the registration number to enter
     */
    private void navigateToCarCheckingPage(String registrationNumber) {
        driver.get(CAR_CHECKING_URL);
        CarCheckingPage carCheckingPage = new CarCheckingPage(driver);
        carCheckingPage.enterRegistrationNumber(registrationNumber);
        carCheckingPage.submitForm();
    }

    /**
     * Checks for an error alert on the page and writes the alert message to the output file if found.
     *
     * @param wait               the WebDriverWait instance
     * @param registrationNumber the registration number that was entered
     * @return true if an error alert is found, false otherwise
     * @throws IOException if an I/O error occurs
     */
    private boolean checkForErrorAlert(WebDriverWait wait, String registrationNumber) throws IOException {
        WebElement errorAlert = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert.alert-danger")));
        if (errorAlert != null && errorAlert.isDisplayed()) {
            String alertMessage = errorAlert.getText();
            logger.info("Entered Registration Number: " + registrationNumber);
            logger.info("Alert Message: " + alertMessage);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
                writer.write(String.format("%s,%s%n", registrationNumber, alertMessage));
            }
            return true;
        }
        return false;
    }

    /**
     * Checks for data elements on the car report page.
     *
     * @param wait               the WebDriverWait instance
     * @param registrationNumber the registration number that was entered
     * @return true if data elements are found, false otherwise
     */
    private boolean checkForDataElements(WebDriverWait wait, String registrationNumber) {
        try {
            CarReportPage carReportPage = new CarReportPage(driver);
            String regNumber = carReportPage.getRegistrationNumber();
            return regNumber != null && !regNumber.isEmpty();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Extracts car details from the car report page and writes them to the output file.
     *
     * @throws IOException if an I/O error occurs
     */
    private void extractAndWriteCarDetails() throws IOException {
        CarReportPage carReportPage = new CarReportPage(driver);
        String regNumber = carReportPage.getRegistrationNumber();
        String make = carReportPage.getMake();
        String model = carReportPage.getModel();
        String year = carReportPage.getYearOfManufacture();

        logger.info("Extracted details - RegNumber: " + regNumber + ", Make: " + make + ", Model: " + model + ", Year: " + year);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            if (regNumber != null && !regNumber.isEmpty() && make != null && !make.isEmpty() && model != null && !model.isEmpty() && year != null && !year.isEmpty()) {
                writer.write(String.format("%s,%s,%s,%s%n", regNumber, make, model, year));
                logger.info("Written valid details to file.");
            } else {
                writer.write(String.format("%s,The license plate number is not recognised%n", regNumber != null ? regNumber : ""));
                logger.info("Written invalid details to file.");
            }
        }
    }

    /**
     * Tests valid registration numbers by navigating to the car checking page and extracting car details.
     *
     * @param validRegistrationNumber the valid registration number to test
     * @throws IOException if an I/O error occurs
     */
    @Order(1)
    @ParameterizedTest
    @MethodSource("validRegistrationNumbersProvider")
    public void testValidRegistrationNumber(String validRegistrationNumber) throws IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            navigateToCarCheckingPage(validRegistrationNumber);
            if (checkForDataElements(wait, validRegistrationNumber)) {
                extractAndWriteCarDetails();
            }
        } finally {
            // driver.quit();
        }
    }

    /**
     * Provides valid registration numbers for parameterized tests.
     *
     * @return a stream of valid registration numbers
     * @throws IOException if an I/O error occurs
     */
    static Stream<String> validRegistrationNumbersProvider() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/cleaned_test_data.txt"));
        return reader.lines()
                .skip(1)
                .map(line -> line.split(","))
                .filter(fields -> fields.length == 2 && "VALID".equals(fields[1]))
                .map(fields -> fields[0])
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    /**
     * Tests invalid registration numbers by navigating to the car checking page and checking for error alerts.
     *
     * @param invalidRegistrationNumber the invalid registration number to test
     * @throws IOException if an I/O error occurs
     */
    @Order(2)
    @ParameterizedTest
    @MethodSource("invalidRegistrationNumbersProvider")
    public void testInvalidRegistrationNumberDataDriven(String invalidRegistrationNumber) throws IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            navigateToCarCheckingPage(invalidRegistrationNumber);
            checkForErrorAlert(wait, invalidRegistrationNumber);
        } finally {
            // driver.quit();
        }
    }

    /**
     * Provides invalid registration numbers for parameterized tests.
     *
     * @return a stream of invalid registration numbers
     * @throws IOException if an I/O error occurs
     */
    static Stream<String> invalidRegistrationNumbersProvider() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("src/test/resources/cleaned_test_data.txt"));
        return reader.lines()
                .skip(1)
                .map(line -> line.split(","))
                .filter(fields -> fields.length == 2 && "The license plate number is not recognised".equals(fields[1]))
                .map(fields -> fields[0])
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    /**
     * Tests invalid registration numbers by navigating to the car checking page and checking for error alerts.
     *
     * @param registrationNumber the invalid registration number to test
     * @param expectedMessage    the expected error message
     * @throws IOException if an I/O error occurs
     */

    @Order(3)
    @ParameterizedTest
    @MethodSource("hardcodedInvalidRegistrationNumbersProvider")
    public void testHardcodedInvalidRegistrationNumber(String registrationNumber, String expectedMessage) throws IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            navigateToCarCheckingPage(registrationNumber);
            boolean alertFound = checkForErrorAlert(wait, registrationNumber);
            assertEquals(expectedMessage, alertFound ? "The license plate number is not recognised" : "");
        } finally {
            // driver.quit();
        }
    }

    static Stream<Arguments> hardcodedInvalidRegistrationNumbersProvider() {
        return Stream.of(
                Arguments.of("INVALID123", "The license plate number is not recognised"),
                Arguments.of("1", "The license plate number is not recognised"),
                Arguments.of("A", "The license plate number is not recognised"),
                Arguments.of("A1A", "The license plate number is not recognised"),
                Arguments.of("AAA", "The license plate number is not recognised"),
                Arguments.of("AA00", "The license plate number is not recognised"),
                Arguments.of("AA00 XXX", "The license plate number is not recognised")
        );
    }

    /**
     * Compares the actual output with the expected output.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(4)
    @Test
    public void compareOutputWithExpected() throws IOException {
        List<String> actualOutput = Files.readAllLines(Paths.get(OUTPUT_FILE_PATH));
        List<String> expectedOutput = Files.readAllLines(Paths.get(EXPECTED_OUTPUT_FILE_PATH));
        assertEquals(expectedOutput.size(), actualOutput.size(), "The number of lines in the actual output does not match the expected output.");
        for (int i = 0; i < expectedOutput.size(); i++) {
            String[] expectedFields = expectedOutput.get(i).split(",");
            String[] actualFields = actualOutput.get(i).split(",");
            assertEquals(expectedFields.length, actualFields.length, "The number of fields in line " + (i + 1) + " does not match.");
            for (int j = 0; j < expectedFields.length; j++) {
                assertEquals(expectedFields[j], actualFields[j], "Field " + (j + 1) + " in line " + (i + 1) + " does not match.");
            }
        }
    }

    /**
     * Tests the website's response to a non-existent page by checking for a 404 error.
     */
    @Order(5)
    @Test
    public void testWebsiteDown() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try {
            driver.get("https://car-checking.com/nonexistentpage");
            boolean is404 = wait.until(d -> {
                String pageSource = driver.getPageSource();
                return pageSource.contains("404") || pageSource.contains("Not Found");
            });
            assertEquals(true, is404, "Expected 404 Not Found error was not found.");
        } finally {
            // driver.quit();
        }
    }
}