import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// utilidades estaticas de validacion y lectura segura de datos, sin estado
public final class Validaciones {

    private Validaciones() {
    }

    public static <T> T exigirNoNulo(T valor, String nombre) {
        if (valor == null) {
            throw new ValidacionException(nombre + " es obligatorio.");
        }
        return valor;
    }

    public static String exigirTexto(String valor, String nombre) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException(nombre + " no puede estar vacio.");
        }
        return valor.trim();
    }

    // los codigos se normalizan a mayusculas para que no existan duplicados por diferencia de formato
    public static String exigirCodigo(String valor, String nombre) {
        return exigirTexto(valor, nombre).toUpperCase();
    }

    public static BigDecimal exigirPositivo(BigDecimal valor, String nombre) {
        exigirNoNulo(valor, nombre);
        if (valor.signum() <= 0) {
            throw new ValidacionException(nombre + " debe ser mayor que cero.");
        }
        return valor;
    }

    public static BigDecimal exigirEnRango(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, String nombre) {
        exigirNoNulo(valor, nombre);
        if (valor.compareTo(minimo) < 0 || valor.compareTo(maximo) > 0) {
            throw new ValidacionException(nombre + " debe estar entre " + minimo.toPlainString()
                    + " y " + maximo.toPlainString() + ".");
        }
        return valor;
    }

    public static int exigirEnteroEnRango(int valor, int minimo, int maximo, String nombre) {
        if (valor < minimo || valor > maximo) {
            throw new ValidacionException(nombre + " debe estar entre " + minimo + " y " + maximo + ".");
        }
        return valor;
    }

    public static BigDecimal parsearDecimal(String texto, String nombre) {
        String limpio = exigirTexto(texto, nombre).replace(',', '.');
        if (limpio.length() > 18 || !limpio.matches("[+-]?\\d*\\.?\\d+")) {
            throw new ValidacionException(nombre + " no es un numero valido: \"" + texto.trim() + "\".");
        }
        return new BigDecimal(limpio);
    }

    public static int parsearEntero(String texto, String nombre) {
        String limpio = exigirTexto(texto, nombre);
        if (limpio.length() > 9 || !limpio.matches("[+-]?\\d+")) {
            throw new ValidacionException(nombre + " no es un entero valido: \"" + limpio + "\".");
        }
        return Integer.parseInt(limpio);
    }

    public static LocalDate parsearFecha(String texto, String nombre) {
        String limpio = exigirTexto(texto, nombre);
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new ValidacionException(nombre + " no es una fecha valida (formato aaaa-mm-dd): \"" + limpio + "\".");
        }
    }
}
