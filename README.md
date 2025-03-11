
# Vehicle Registration Extractor and Car Valuation

## Overview

This project extracts vehicle registration numbers from input files, categorizes them as valid or invalid, and performs car valuation tests using Selenium WebDriver. The project is structured to follow best practices in Java development, including clear method names, encapsulated constants, proper exception handling, and comprehensive documentation.

## Technologies Used

- **Java 17**: The programming language used for the project.
- **Maven**: Build automation tool used for managing dependencies and building the project.
- **JUnit 5**: Testing framework used for writing and running tests.
- **Selenium WebDriver 4.12.1**: Tool for automating web browser interaction.
- **WebDriverManager 5.6.3**: Library for managing WebDriver binaries.

## Project Structure

```
src
├── main
│   └── java
│       └── org
│           └── example
│               └── valuation
│                   ├── CarCheckingPage.java
│                   ├── CarReportPage.java
│                   ├── DriverSingleton.java
│                   └── VehicleRegistrationExtractor.java
└── test
  └── java
      └── org
         └── example
                    └── valuation
                     ├── CarValuationTest.java
                      └── TestDataProvider.java

    └── resources
            ├── car_input - V6.txt
            ├── car_output - V6.txt
            ├── cleaned_test_data.txt
            ├── config.properties
            └── expected_output.txt
```

## Explanation of Classes and Files

### 1. `car_input - V6.txt`
This file contains a textual description of car valuation in the UK and provides sample registration numbers along with their estimated value or market status.

### 2. `car_output - V6.txt`
This file provides a structured CSV-style output that maps valid vehicle registrations to their respective make, model, and year. It also lists unrecognized registrations.

### 3. `cleaned_test_data.txt`
This file contains a refined version of extracted registration numbers, classifying them as either **VALID** or **not recognised**.

### 4. `expected_output.txt`
This file represents the expected final structured output, to be compared with `car_output - V6.txt`, ensuring consistency between the processed data and expected results.



### `VehicleRegistrationExtractor.java`

- **Purpose**: Extracts vehicle registration numbers from input files and categorizes them as valid or invalid.
- **Key Methods**:
    - `main(String[] args)`: Entry point for the extraction process. It extracts valid and invalid registration numbers and writes them to an output file.
    - `extractRegistrationNumbers(Pattern pattern)`: Extracts registration numbers from input files based on the provided pattern.
    - `linesFromFile(Path path)`: Reads lines from a file.

###  Implementation

The  class **VehicleRegistrationExtractor** is responsible for processing the input files, extracting registration numbers, and generating cleaned test data.

### **Key Functionalities:**
- **Extracts valid and invalid vehicle registrations** using regex patterns.
- **Filters out invalid registrations** that do not conform to UK formats.
- **Writes cleaned data** into `cleaned_test_data.txt`.
- **Compares results** with `expected_output.txt` for validation.

### **Regex Patterns Used:**
- **Valid UK registrations:** `\\b[A-Z]{2}[0-9]{2} [A-Z]{3}\\b`
- **Invalid registrations:** The regex identifies unrecognized registration numbers by capturing 1-7 character alphanumeric entries, excluding valid UK formats (XX00 XXX) using a negative lookahead.

###  Process:
1. The program dynamically reads all *_input.txt files from the test directory, regardless of the number of files present in the directory.
2. It extracts valid and invalid vehicle registrations.
3. Valid registrations are marked as **VALID**.
4. Unrecognized registrations are flagged as **"The license plate number is not recognised"**.
5. The processed data is written to `cleaned_test_data.txt`.
6. The output is compared with `expected_output.txt

- **Best Practices Used**:
    - **Descriptive Method Names**: Methods are named clearly to describe their actions.
    - **Encapsulated Constants**: Constants are used for regular expressions and file paths to improve maintainability.
    - **Proper Exception Handling**: Meaningful error messages are provided when exceptions occur.
    - **Resource Management**: Resources such as file streams are properly managed and closed after use.


### `CarCheckingPage.java`

- **Purpose**: Page Object Model for the car checking page. It provides methods to interact with the car checking form on the web page.
- **Key Methods**:
    - `CarCheckingPage(WebDriver driver)`: Constructor to initialize the `CarCheckingPage` with a `WebDriver` instance.
    - `enterRegistrationNumber(String registrationNumber)`: Enters the registration number into the input field.
    - `submitForm()`: Submits the form.

- **Best Practices Used**:
    - **Descriptive Method Names**: Methods are named clearly to describe their actions.
    - **Encapsulated Constants**: Constants are used for locators to improve maintainability.
    - **Resource Management**: Resources such as `WebDriver` are properly managed and closed after use.


### `CarReportPage.java`

- **Purpose**: Page Object Model for the car report page. It provides methods to extract car details from the report page.
- **Key Methods**:
    - `CarReportPage(WebDriver driver)`: Constructor to initialize the `CarReportPage` with a `WebDriver` instance.
    - `getRegistrationNumber()`: Retrieves the registration number from the report page.
    - `getMake()`: Retrieves the make of the car from the report page.
    - `getModel()`: Retrieves the model of the car from the report page.
    - `getYearOfManufacture()`: Retrieves the year of manufacture of the car from the report page.

- **Best Practices Used**:
    - **Descriptive Method Names**: Methods are named clearly to describe their actions.
    - **Encapsulated Constants**: Constants are used for locators to improve maintainability.
    - **Resource Management**: Resources such as `WebDriver` are properly managed and closed after use.


### `CarValuationTest.java`

- **Purpose**: Test class for car valuation functionality. It uses Selenium WebDriver to interact with a web application and verify the car valuation process.
- **Key Methods**:
    - `setup()`: This method is annotated with `@BeforeAll` and is executed once before all tests. It clears the output file and runs the data extraction step.
    - `testCarValuation(String registrationNumber, String expectedMessage)`: This is a parameterized test method that takes a registration number and an expected message as inputs. It performs the following steps:
        - Sets up the Chrome WebDriver.
        - Navigates to the car checking page and enters the registration number.
        - Submits the form and checks for any error alerts.
        - **If an error alert is present (this is where the test fails if the car registration details are not found on the comparison site or there are mismatches), it writes the error details to the output file and skips the rest of the test logic.**
        - If no error alert is found, it navigates to the report page and extracts car details (registration number, make, model, year).
        - Writes the extracted car details to the output file.
        - `compareOutputWithExpected()`: This method reads the actual output from the output file and compares it with the expected output. It verifies that the number of lines and the content of each field match the expected output.

- **Best Practices Used**:
    - **Descriptive Method Names**: Methods are named clearly to describe their actions.
    - **Encapsulated Constants**: Constants are used for file paths to improve maintainability.
    - **Proper Exception Handling**: Meaningful error messages are provided when exceptions occur.
    - **Parameterized Tests**: Tests are parameterized to run with multiple sets of data.
    - **Resource Management**: Resources such as WebDriver and file writers are properly managed and closed after use.
    - 
### `TestDataProvider.java`

- **Purpose**: Provides test data for car valuation tests.
- **Key Methods**:
    - `carDataProvider()`: Provides car data from the cleaned input file.

- **Best Practices Used**:
    - **Descriptive Method Names**: Methods are named clearly to describe their actions.
    - **Encapsulated Constants**: Constants are used for file paths to improve maintainability.
    - **Proper Exception Handling**: Meaningful error messages are provided when exceptions occur.
    - **Resource Management**: Resources such as file streams are properly managed and closed after use.

## Best Practices Used

- **Descriptive Method Names**: Methods are named clearly to describe their actions.
- **Encapsulated Constants**: Constants are used for regular expressions and file paths to improve maintainability.
- **Javadoc Comments**: Classes and methods are documented for better understanding.
- **Proper Exception Handling**: Meaningful error messages are provided when exceptions occur.
- **Parameterized Tests**: Tests are parameterized to run with multiple sets of data.


### Project Enhancements

#### SingletonDriver
- **Purpose**: Ensures a single instance of the WebDriver is used throughout the tests, improving resource management and test stability.
- **Implementation**: The `SingletonDriver` class provides a static method to get the WebDriver instance and ensures it is initialized only once.

#### config.properties
- **Purpose**: Centralizes configuration settings for the tests, such as the browser type.
- **Usage**: The `config.properties` file contains key-value pairs for configuration settings. For example:
  ```ini
  browser=chrome
  ```
- **Integration**: The `SingletonDriver` class reads this file to determine which browser to initialize.

#### Logging
- **Purpose**: Replaces `System.out.println` statements with a robust logging framework to improve traceability and debugging.
- **Implementation**: The `log4j` library is used for logging. Log messages are written to the console and can be configured to write to files or other destinations.
- **Example**:
  ```java
  private static final Logger logger = LogManager.getLogger(CarValuationTest.class);
  ```

#### Class Reformatting
- **Purpose**: Improves code readability and maintainability by adhering to consistent coding standards and best practices.
- **Changes**:
    - Encapsulated constants for file paths and URLs.
    - Improved exception handling with meaningful error messages.
    - Added parameterized tests for better test coverage.

These enhancements collectively improve the maintainability, readability, and robustness of the test suite.


## Setup and Run

### Prerequisites

- Java 17
- Maven
- Chrome browser

### Steps to Setup and Run

1. **Clone the repository**:
   ```sh
   git clone https://github.com/Ahmet-Acik/car_valuation_test_V6.git
   cd car_valuation_test_V6
   ```

2.  **Install dependencies**:
   ```sh
   mvn install
   ```

2. **Install dependencies if you want to skip tests during the install phase**:
   ```sh
   mvn install -DskipTests
   ```
   
3. **Run the tests**:
   ```sh
   mvn test
   ```

4. **Check the output**:
    - The output of the car valuation tests will be written to `src/test/resources/car_output - V6.txt`.
    - Compare the output with `src/test/resources/expected_output.txt` to verify the results.


## License

This project is licensed under the MIT License.
```