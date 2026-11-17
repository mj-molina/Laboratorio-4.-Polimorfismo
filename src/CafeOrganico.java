import java.math.BigDecimal;
import java.time.LocalDate;

// cafe organico: es un cafe en pergamino con sello de certificacion y bonificacion por kilogramo
// hereda el pago base y la humedad, y solo agrega la bonificacion y la validacion del sello
public class CafeOrganico extends CafePergamino {

    public static final String CATEGORIA_ORGANICO = "Cafe organico";

    private static final BigDecimal BONIFICACION_POR_KG = new BigDecimal("0.75");

    private final String entidadCertificadora;
    private final LocalDate selloValidoHasta;

    public CafeOrganico(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido,
                        Calidad calidad, BigDecimal humedad, String entidadCertificadora, LocalDate selloValidoHasta) {
        super(codigo, productor, fechaIngreso, pesoRecibido, calidad, humedad);
        this.entidadCertificadora = Validaciones.exigirTexto(entidadCertificadora, "La entidad certificadora");
        this.selloValidoHasta = Validaciones.exigirNoNulo(selloValidoHasta, "La fecha de validez del sello");
        if (selloValidoHasta.isBefore(fechaIngreso)) {
            throw new ValidacionException("El sello de certificacion esta vencido: es valido hasta "
                    + selloValidoHasta + " y el lote ingresa el " + fechaIngreso + ".");
        }
    }

    public static Lote crearOrganico(DatosLote datos) {
        return new CafeOrganico(datos.codigo(), datos.productor(), datos.getFechaIngreso(), datos.peso(),
                datos.calidad(), datos.decimal("humedad", "La humedad"),
                datos.texto("entidad", "La entidad certificadora"),
                datos.fecha("selloHasta", "La fecha de validez del sello"));
    }

    @Override
    public String getCategoria() {
        return CATEGORIA_ORGANICO;
    }

    @Override
    public BigDecimal calcularPago() {
        return redondear(super.calcularPago().add(getPesoRecibido().multiply(BONIFICACION_POR_KG)));
    }

    @Override
    protected String detalleCategoria() {
        return super.detalleCategoria() + ", sello " + entidadCertificadora + " hasta " + selloValidoHasta;
    }

    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }

    public LocalDate getSelloValidoHasta() {
        return selloValidoHasta;
    }
}
