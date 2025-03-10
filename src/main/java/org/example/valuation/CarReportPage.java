package org.example.valuation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CarReportPage {
    private WebDriver driver;

    public CarReportPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getRegistrationNumber() {
        WebElement regNumberElement = driver.findElement(By.id("subForm"));
        return regNumberElement.getAttribute("value");
    }

    public String getMake() {
        WebElement makeElement = driver.findElement(By.xpath("//td[text()='Make']/following-sibling::td"));
        return makeElement.getText();
    }

    public String getModel() {
        WebElement modelElement = driver.findElement(By.xpath("//td[text()='Model']/following-sibling::td"));
        return modelElement.getText();
    }

    public String getYearOfManufacture() {
        WebElement yearElement = driver.findElement(By.xpath("//td[text()='Year of manufacture']/following-sibling::td"));
        return yearElement.getText();
    }
}

