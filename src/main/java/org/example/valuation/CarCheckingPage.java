package org.example.valuation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

/**
 * Page Object Model for the Car Checking Page.
 */
public class CarCheckingPage {
    private static final Logger logger = LogManager.getLogger(CarCheckingPage.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    // Locators
    private static final By REG_NUMBER_INPUT = By.xpath("//input[@id='subForm1']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    /**
     * Constructor to initialize the CarCheckingPage with WebDriver.
     *
     * @param driver the WebDriver instance
     */
    public CarCheckingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    /**
     * Enters the registration number into the input field.
     *
     * @param registrationNumber the registration number to enter
     */
    public void enterRegistrationNumber(String registrationNumber) {
        WebElement regNumberInput = driver.findElement(REG_NUMBER_INPUT);
        regNumberInput.sendKeys(registrationNumber);
        logger.info("Entered registration number: " + registrationNumber);
    }

    /**
     * Submits the form.
     */
    public void submitForm() {
        WebElement submitButton = driver.findElement(SUBMIT_BUTTON);
        submitButton.click();
        logger.info("Submitted the form");
    }
}