package org.example.valuation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for the Car Checking Page.
 */
public class CarCheckingPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Enters the registration number into the input field.
     *
     * @param registrationNumber the registration number to enter
     */
    public void enterRegistrationNumber(String registrationNumber) {
        WebElement regNumberInput = driver.findElement(REG_NUMBER_INPUT);
        regNumberInput.sendKeys(registrationNumber);
    }

    /**
     * Submits the form.
     */
    public void submitForm() {
        WebElement submitButton = driver.findElement(SUBMIT_BUTTON);
        submitButton.click();
    }
}