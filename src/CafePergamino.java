import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

// cafe en pergamino: se paga por calidad con descuento por humedad; no se vence
public class CafePergamino extends Lote {

    public static final String CATEGORIA = "Cafe en pergamino";

    private static final BigDecimal HUMEDAD_MINIMA = new BigDecimal("8");
    private static final BigDecimal HUMEDAD_MAXIMA = new BigDecimal("14");
    private static final BigDecimal HUMEDAD_BASE = new BigDecimal("12");
    private static final BigDecimal DESCUENTO_POR_PUNTO = new BigDecimal("0.03");
    private static final Map<Calidad, BigDecimal> PRECIOS = new EnumMap<>(Calidad.class);

    static {
        PRECIOS.put(Calidad.A, new BigDecimal("12"));
        PRECIOS.put(Calidad.B, new BigDecimal("9"));
        PRECIOS.put(Calidad.C, new BigDecimal("6"));
    }

    private final BigDecimal humedad;

    public CafePergamino(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido,
                         Calidad calidad, BigDecimal humedad) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        this.humedad = Validaciones.exigirEnRango(humedad, HUMEDAD_MINIMA, HUMEDAD_MAXIMA, "La humedad");
    }

    public static Lote crear(DatosLote datos) {
        return new CafePergamino(datos.codigo(), datos.productor(), datos.getFechaIngreso(), datos.peso(),
                datos.calidad(), datos.decimal("humedad", "La humedad"));
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    // se descuenta 3 % por cada punto porcentual o fraccion por encima de 12
    @Override
    public BigDecimal calcularPago() {
        BigDecimal bruto = getPesoRecibido().multiply(PRECIOS.get(getCalidad()));
        BigDecimal exceso = humedad.subtract(HUMEDAD_BASE);
        if (exceso.signum() <= 0) {
            return redondear(bruto);
        }
        BigDecimal puntos = exceso.setScale(0, RoundingMode.CEILING);
        BigDecimal descuento = bruto.multiply(DESCUENTO_POR_PUNTO).multiply(puntos);
        return redondear(bruto.subtract(descuento));
    }

    @Override
    protected String detalleCategoria() {
        return "humedad " + Formato.porcentaje(humedad);
    }

    public BigDecimal getHumedad() {
        return humedad;
    }
}
