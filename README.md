
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
ssrc
├── main
│   └── java
│       └── org
│           └── example
│               └── valuation
│                   ├── CarCheckingPage.java
│                   ├── CarReportPage.java
│                   └── VehicleRegistrationExtractor.java
└── test
    └── java
        └── org
            └── example
                └── valuation
                    ├── CarValuationTest.java
                    └── TestDataProvider.java
resources
└── test
    └── resources
        ├── cleaned_test_data.txt
        └── expected_output.txt
```

## Explanation of Classes and Files

### `VehicleRegistrationExtractor.java`

- **Purpose**: Extracts vehicle registration numbers from input files and categorizes them as valid or invalid.
- **Key Methods**:
    - `main(String[] args)`: Entry point for the extraction process. It extracts valid and invalid registration numbers and writes them to an output file.
    - `extractRegistrationNumbers(Pattern pattern)`: Extracts registration numbers from input files based on the provided pattern.
    - `linesFromFile(Path path)`: Reads lines from a file.

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