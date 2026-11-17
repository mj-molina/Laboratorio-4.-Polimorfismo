import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

// queso fresco: perecedero (21 dias), requiere camara fria, no acepta calidad C y descuenta el empaque por kilogramo
public class QuesoFresco extends Lote implements Vencible, RequiereCamaraFria {

    public static final String CATEGORIA = "Queso fresco";

    private static final int VIDA_UTIL_DIAS = 21;
    private static final BigDecimal GRASA_MINIMA = new BigDecimal("10");
    private static final BigDecimal GRASA_MAXIMA = new BigDecimal("40");
    private static final BigDecimal EMPAQUE_POR_KG = new BigDecimal("1.50");
    private static final Map<Calidad, BigDecimal> PRECIOS = new EnumMap<>(Calidad.class);

    static {
        PRECIOS.put(Calidad.A, new BigDecimal("38"));
        PRECIOS.put(Calidad.B, new BigDecimal("30"));
    }

    private final BigDecimal grasa;

    public QuesoFresco(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido,
                       Calidad calidad, BigDecimal grasa) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        if (!PRECIOS.containsKey(calidad)) {
            throw new ValidacionException("No se acepta queso fresco de calidad " + calidad + ".");
        }
        this.grasa = Validaciones.exigirEnRango(grasa, GRASA_MINIMA, GRASA_MAXIMA, "El porcentaje de grasa");
    }

    public static Lote crear(DatosLote datos) {
        return new QuesoFresco(datos.codigo(), datos.productor(), datos.getFechaIngreso(), datos.peso(),
                datos.calidad(), datos.decimal("grasa", "El porcentaje de grasa"));
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    @Override
    public BigDecimal calcularPago() {
        BigDecimal precioNeto = PRECIOS.get(getCalidad()).subtract(EMPAQUE_POR_KG);
        return redondear(getPesoRecibido().multiply(precioNeto));
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
    public Optional<RequiereCamaraFria> comoRefrigerable() {
        return Optional.of(this);
    }

    @Override
    protected String detalleCategoria() {
        return "grasa " + Formato.porcentaje(grasa);
    }
}
