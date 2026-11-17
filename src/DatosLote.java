import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

// datos en bruto (texto) de un lote por crear; cada categoria los interpreta y valida al construir su lote
public class DatosLote {

    public static final String CODIGO = "codigo";
    public static final String PRODUCTOR = "productor";
    public static final String PESO = "peso";
    public static final String CALIDAD = "calidad";

    private final LocalDate fechaIngreso;
    private final Map<String, String> valores = new LinkedHashMap<>();

    public DatosLote(LocalDate fechaIngreso) {
        this.fechaIngreso = Validaciones.exigirNoNulo(fechaIngreso, "La fecha de ingreso");
    }

    public void poner(String clave, String valor) {
        valores.put(clave, valor);
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public String codigo() {
        return texto(CODIGO, "El codigo del lote");
    }

    public String productor() {
        return texto(PRODUCTOR, "El nombre del productor");
    }

    public BigDecimal peso() {
        return decimal(PESO, "El peso recibido");
    }

    public Calidad calidad() {
        return Calidad.desde(valores.get(CALIDAD));
    }

    public String texto(String clave, String nombre) {
        return Validaciones.exigirTexto(valores.get(clave), nombre);
    }

    public BigDecimal decimal(String clave, String nombre) {
        return Validaciones.parsearDecimal(valores.get(clave), nombre);
    }

    public int entero(String clave, String nombre) {
        return Validaciones.parsearEntero(valores.get(clave), nombre);
    }

    public LocalDate fecha(String clave, String nombre) {
        return Validaciones.parsearFecha(valores.get(clave), nombre);
    }
}
