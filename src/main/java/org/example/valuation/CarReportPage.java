package org.example.valuation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for the Car Report Page.
 */
public class CarReportPage {
    private final WebDriver driver;

    // Locators
    private static final By REG_NUMBER_ELEMENT = By.id("subForm");
    private static final By MAKE_ELEMENT = By.xpath("//td[text()='Make']/following-sibling::td");
    private static final By MODEL_ELEMENT = By.xpath("//td[text()='Model']/following-sibling::td");
    private static final By YEAR_ELEMENT = By.xpath("//td[text()='Year of manufacture']/following-sibling::td");

    /**
     * Constructor to initialize the CarReportPage with WebDriver.
     *
     * @param driver the WebDriver instance
     */
    public CarReportPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Retrieves the registration number from the report page.
     *
     * @return the registration number
     */
    public String getRegistrationNumber() {
        WebElement regNumberElement = driver.findElement(REG_NUMBER_ELEMENT);
        return regNumberElement.getAttribute("value");
    }

    /**
     * Retrieves the make of the car from the report page.
     *
     * @return the make of the car
     */
    public String getMake() {
        WebElement makeElement = driver.findElement(MAKE_ELEMENT);
        return makeElement.getText();
    }

    /**
     * Retrieves the model of the car from the report page.
     *
     * @return the model of the car
     */
    public String getModel() {
        WebElement modelElement = driver.findElement(MODEL_ELEMENT);
        return modelElement.getText();
    }

    /**
     * Retrieves the year of manufacture of the car from the report page.
     *
     * @return the year of manufacture
     */
    public String getYearOfManufacture() {
        WebElement yearElement = driver.findElement(YEAR_ELEMENT);
        return yearElement.getText();
    }
}