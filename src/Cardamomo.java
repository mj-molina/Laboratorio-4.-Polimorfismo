import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

// cardamomo: perecedero (180 dias), paga mas si el grano verde es al menos 85 %
public class Cardamomo extends Lote implements Vencible {

    public static final String CATEGORIA = "Cardamomo";

    private static final int VIDA_UTIL_DIAS = 180;
    private static final BigDecimal VERDE_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal VERDE_MAXIMO = new BigDecimal("100");
    private static final BigDecimal UMBRAL_BONO = new BigDecimal("85");
    private static final BigDecimal FACTOR_BONO = new BigDecimal("1.10");
    private static final Map<Calidad, BigDecimal> PRECIOS = new EnumMap<>(Calidad.class);

    static {
        PRECIOS.put(Calidad.A, new BigDecimal("60"));
        PRECIOS.put(Calidad.B, new BigDecimal("48"));
        PRECIOS.put(Calidad.C, new BigDecimal("35"));
    }

    private final BigDecimal granoVerde;

    public Cardamomo(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido,
                     Calidad calidad, BigDecimal granoVerde) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        this.granoVerde = Validaciones.exigirEnRango(granoVerde, VERDE_MINIMO, VERDE_MAXIMO, "El porcentaje de grano verde");
    }

    public static Lote crear(DatosLote datos) {
        return new Cardamomo(datos.codigo(), datos.productor(), datos.getFechaIngreso(), datos.peso(),
                datos.calidad(), datos.decimal("granoVerde", "El porcentaje de grano verde"));
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    @Override
    public BigDecimal calcularPago() {
        BigDecimal pago = getPesoRecibido().multiply(PRECIOS.get(getCalidad()));
        if (granoVerde.compareTo(UMBRAL_BONO) >= 0) {
            pago = pago.multiply(FACTOR_BONO);
        }
        return redondear(pago);
    }

    @Override
    public int getVidaUtilDias() {
        return VIDA_UTIL_DIAS;
    }

    @Override
    public Optional<Vencible> comoVencible() {
        return Optional.of(this);
    }

    @Override
    protected String detalleCategoria() {
        return "grano verde " + Formato.porcentaje(granoVerde);
    }

    public BigDecimal getGranoVerde() {
        return granoVerde;
    }
}
