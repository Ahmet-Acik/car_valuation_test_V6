package org.example.valuation;

import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TestDataProvider {

    // Method to provide car data from the cleaned input file
    public static Stream<Arguments> carDataProvider() throws IOException {
        Path inputFile = Paths.get("src/test/resources/cleaned_test_data.txt");

        return Files.lines(inputFile)
                .skip(1) // Skip the header line
                .filter(line -> !line.trim().isEmpty()) // Filter out empty lines
                .map(line -> {
                    String[] parts = line.split(",");
                    return Arguments.of(parts[0], parts[1]);
                });
    }
}