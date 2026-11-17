import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

// hortaliza: perecedera con vida util propia (3 a 14 dias), requiere camara fria
// solo se paga el 95 % del peso recibido porque el resto es merma de transporte
public class Hortaliza extends Lote implements Vencible, RequiereCamaraFria {

    public static final String CATEGORIA = "Hortaliza";

    private static final int VIDA_MINIMA = 3;
    private static final int VIDA_MAXIMA = 14;
    private static final BigDecimal FACTOR_PAGABLE = new BigDecimal("0.95");
    private static final Map<Calidad, BigDecimal> PRECIOS = new EnumMap<>(Calidad.class);

    static {
        PRECIOS.put(Calidad.A, new BigDecimal("4"));
        PRECIOS.put(Calidad.B, new BigDecimal("3"));
        PRECIOS.put(Calidad.C, new BigDecimal("2"));
    }

    private final int vidaUtilDias;

    public Hortaliza(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido,
                     Calidad calidad, int vidaUtilDias) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad);
        this.vidaUtilDias = Validaciones.exigirEnteroEnRango(vidaUtilDias, VIDA_MINIMA, VIDA_MAXIMA, "La vida util");
    }

    public static Lote crear(DatosLote datos) {
        return new Hortaliza(datos.codigo(), datos.productor(), datos.getFechaIngreso(), datos.peso(),
                datos.calidad(), datos.entero("vidaUtil", "La vida util"));
    }

    @Override
    public String getCategoria() {
        return CATEGORIA;
    }

    @Override
    public BigDecimal calcularPago() {
        BigDecimal pesoPagable = getPesoRecibido().multiply(FACTOR_PAGABLE);
        return redondear(pesoPagable.multiply(PRECIOS.get(getCalidad())));
    }

    @Override
    public int getVidaUtilDias() {
        return vidaUtilDias;
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
        return "vida util " + vidaUtilDias + " dias";
    }
}
