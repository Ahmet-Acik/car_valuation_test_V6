package org.example.valuation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CarCheckingPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CarCheckingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterRegistrationNumber(String registrationNumber) {
        WebElement regNumberInput = driver.findElement(By.xpath("//input[@id='subForm1']"));
        regNumberInput.sendKeys(registrationNumber);
    }

    public void submitForm() {
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
    }
}