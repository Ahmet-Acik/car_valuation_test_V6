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

/**
 * Extracts vehicle registration numbers from input files and categorizes them as valid or invalid.
 */
public class VehicleRegistrationExtractor {

    private static final Path INPUT_DIR = Paths.get("src/test/resources");
    private static final String VALID_REGEX = "\\b[A-Z]{2}[0-9]{2} [A-Z]{3}\\b";
    private static final String INVALID_REGEX = "\\b(?![A-Z]{2}[0-9]{2} [A-Z]{3}\\b)[A-Z0-9]{1,7}\\b";
    private static final Pattern VALID_PATTERN = Pattern.compile(VALID_REGEX);
    private static final Pattern INVALID_PATTERN = Pattern.compile(INVALID_REGEX);
    private static final Path OUTPUT_FILE = Paths.get("src/test/resources/cleaned_test_data.txt");

    public static void main(String[] args) {
        try {
            List<String> validRegistrationNumbers = extractRegistrationNumbers(VALID_PATTERN);
            List<String> invalidRegistrationNumbers = extractRegistrationNumbers(INVALID_PATTERN)
                    .stream()
                    .filter(reg -> !validRegistrationNumbers.contains(reg))
                    .collect(Collectors.toList());

            List<String> allRegistrationNumbers = Stream.concat(
                    Stream.of("VARIANT_REG,STATUS"), // Add the header
                    Stream.concat(
                            validRegistrationNumbers.stream().map(reg -> reg + ",VALID"),
                            invalidRegistrationNumbers.stream().map(reg -> reg + ",The license plate number is not recognised")
                    )
            ).collect(Collectors.toList());

            Files.write(OUTPUT_FILE, allRegistrationNumbers);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    /**
     * Extracts registration numbers from input files based on the provided pattern.
     *
     * @param pattern the pattern to match registration numbers
     * @return a list of matched registration numbers
     * @throws IOException if an I/O error occurs
     */
    private static List<String> extractRegistrationNumbers(Pattern pattern) throws IOException {
        return Files.list(INPUT_DIR)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .filter(path -> path.getFileName().toString().contains("_input")) // Filter for files with "_input" in the name
                .flatMap(VehicleRegistrationExtractor::linesFromFile)
                .flatMap(line -> {
                    Matcher matcher = pattern.matcher(line);
                    return matcher.results().map(MatchResult::group);
                })
                .collect(Collectors.toList());
    }

    /**
     * Reads lines from a file.
     *
     * @param path the path to the file
     * @return a stream of lines from the file
     */
    private static Stream<String> linesFromFile(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + path, e);
        }
    }
}