package challengeconversordemonedas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConversionUtilsTest {

    @Test
    public void convert_happyPath() {
        double amount = 100.0;
        double rate = 0.123456;
        double expected = 12.345600; // 100 * 0.123456
        double actual = ConversionUtils.convert(amount, rate);
        assertEquals(expected, actual, 1e-9);
    }

    @Test
    public void convert_zeroAmount() {
        assertEquals(0.0, ConversionUtils.convert(0.0, 5.0), 1e-9);
    }

    @Test
    public void convert_negativeRate_throws() {
        assertThrows(IllegalArgumentException.class, () -> ConversionUtils.convert(10.0, -1.0));
    }
}
