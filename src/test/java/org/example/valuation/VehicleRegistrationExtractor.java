package org.example.valuation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.MatchResult;

public class VehicleRegistrationExtractor {

    public static void main(String[] args) throws IOException {
        Path inputDir = Paths.get("src/test/resources");

        // Define the regular expression pattern for valid vehicle registration numbers
        String validRegex = "\\b[A-Z]{2}[0-9]{2} [A-Z]{3}\\b";
        Pattern validPattern = Pattern.compile(validRegex);

        // Define the regular expression pattern for invalid vehicle registration numbers
        String invalidRegex = "\\b(?![A-Z]{2}[0-9]{2} [A-Z]{3}\\b)[A-Z0-9]{1,7}\\b";
        Pattern invalidPattern = Pattern.compile(invalidRegex);

        // Extract valid and invalid vehicle registration numbers from all input files
        List<String> validRegistrationNumbers = Files.list(inputDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .filter(path -> path.getFileName().toString().contains("_input")) // Filter for files with "_input" in the name
                .flatMap(VehicleRegistrationExtractor::linesFromFile)
                .flatMap(line -> {
                    Matcher matcher = validPattern.matcher(line);
                    return matcher.results().map(MatchResult::group);
                })
                .collect(Collectors.toList());

        List<String> invalidRegistrationNumbers = Files.list(inputDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .filter(path -> path.getFileName().toString().contains("_input")) // Filter for files with "_input" in the name
                .flatMap(VehicleRegistrationExtractor::linesFromFile)
                .flatMap(line -> {
                    Matcher matcher = invalidPattern.matcher(line);
                    return matcher.results().map(MatchResult::group);
                })
                .filter(reg -> !validRegistrationNumbers.contains(reg)) // Exclude valid registrations
                .collect(Collectors.toList());

        // Combine valid and invalid registration numbers with messages
        List<String> allRegistrationNumbers = Stream.concat(
                Stream.of("VARIANT_REG,STATUS"), // Add the header
                Stream.concat(
                        validRegistrationNumbers.stream().map(reg -> reg + ",VALID"),
                        invalidRegistrationNumbers.stream().map(reg -> reg + ",The license plate number is not recognised")
                )
        ).collect(Collectors.toList());

        // Write the combined registration numbers to a new file
        Files.write(Paths.get("src/test/resources/cleaned_test_data.txt"), allRegistrationNumbers);
    }

    // Helper method to read lines from a file
    private static Stream<String> linesFromFile(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}