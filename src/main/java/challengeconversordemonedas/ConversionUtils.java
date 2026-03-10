package challengeconversordemonedas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversionUtils {

    /**
     * Convierte una cantidad usando la tasa proporcionada.
     * Devuelve el resultado redondeado a 6 decimales por precisión interna.
     */
    public static double convert(double amount, double rate) {
        if (rate < 0) throw new IllegalArgumentException("rate must be >= 0");
        BigDecimal a = BigDecimal.valueOf(amount);
        BigDecimal r = BigDecimal.valueOf(rate);
        BigDecimal res = a.multiply(r).setScale(6, RoundingMode.HALF_UP);
        return res.doubleValue();
    }
}
