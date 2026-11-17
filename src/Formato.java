import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

// formato de salida uniforme: montos con dos decimales y kilogramos con dos decimales
public final class Formato {

    private Formato() {
    }

    public static String dinero(BigDecimal monto) {
        return "Q" + String.format(Locale.US, "%,.2f", monto.setScale(2, RoundingMode.HALF_UP));
    }

    public static String kg(BigDecimal kilogramos) {
        return String.format(Locale.US, "%.2f kg", kilogramos.setScale(2, RoundingMode.HALF_UP));
    }

    public static String porcentaje(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString() + " %";
    }
}
