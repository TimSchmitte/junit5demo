package be.ordina.junit5.demo.displaynames;

import be.ordina.junit5.demo.model.UpperCaseConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing 123")
class DisplayNameTests {

    @DisplayName("Normal test name")
    @Test
    void test() {
        assertTrue(true);
    }

    @DisplayName("Name of parameterized test root node")
    @ParameterizedTest(name = "Case {index} : {0} becomes {1} after conversion, all arguments: {arguments}")
    @CsvSource({"test, TEST", "PiZzA, PIZZA", "Number1234, NUMBER1234"})
    void convertToUpper(String orig, String result, TestReporter testReporter) {
        testReporter.publishEntry("derp");
        assertEquals(new UpperCaseConverter().convert(orig), result);
    }
}


