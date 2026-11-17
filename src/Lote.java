import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

// clase abstracta base de todos los lotes
// aqui se decide el orden de salida (compareTo); cada categoria decide su pago, su validacion y sus capacidades
public abstract class Lote implements Comparable<Lote> {

    private final String codigo;
    private final String productor;
    private final LocalDate fechaIngreso;
    private final BigDecimal pesoRecibido;
    private final Calidad calidad;
    private BigDecimal pesoDisponible;
    private EstadoLote estado;
    private Bodega bodega;

    protected Lote(String codigo, String productor, LocalDate fechaIngreso, BigDecimal pesoRecibido, Calidad calidad) {
        this.codigo = Validaciones.exigirCodigo(codigo, "El codigo del lote");
        this.productor = Validaciones.exigirTexto(productor, "El nombre del productor");
        this.fechaIngreso = Validaciones.exigirNoNulo(fechaIngreso, "La fecha de ingreso");
        this.pesoRecibido = Validaciones.exigirPositivo(pesoRecibido, "El peso recibido");
        this.calidad = Validaciones.exigirNoNulo(calidad, "La calidad");
        this.pesoDisponible = pesoRecibido;
        this.estado = EstadoLote.DISPONIBLE;
    }

    // ---- decisiones que dependen de la categoria ----

    public abstract String getCategoria();

    // pago al productor segun las reglas de la categoria, con dos decimales
    public abstract BigDecimal calcularPago();

    // datos propios de la categoria para mostrarlos en pantalla
    protected abstract String detalleCategoria();

    // por defecto un lote no se vence; las categorias perecederas lo sobrescriben
    public Optional<Vencible> comoVencible() {
        return Optional.empty();
    }

    // por defecto un lote no requiere camara fria; las categorias que lo requieren lo sobrescriben
    public Optional<RequiereCamaraFria> comoRefrigerable() {
        return Optional.empty();
    }

    // ---- comportamiento comun ----

    public boolean estaVencidoEn(LocalDate hoy) {
        return comoVencible().map(vencible -> vencible.estaVencido(hoy)).orElse(false);
    }

    public BigDecimal costoPorKg() {
        return calcularPago().divide(pesoRecibido, 10, RoundingMode.HALF_UP);
    }

    // costo de una cantidad de kilogramos de este lote
    public BigDecimal costoDe(BigDecimal kilogramos) {
        return calcularPago().multiply(kilogramos).divide(pesoRecibido, 10, RoundingMode.HALF_UP);
    }

    protected static BigDecimal redondear(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP);
    }

    // ---- cambios de estado, solo los usa el sistema ----

    void consumir(BigDecimal kilogramos) {
        if (estado != EstadoLote.DISPONIBLE) {
            throw new ValidacionException("El lote " + codigo + " no esta disponible.");
        }
        Validaciones.exigirPositivo(kilogramos, "Los kilogramos a consumir");
        if (kilogramos.compareTo(pesoDisponible) > 0) {
            throw new ValidacionException("El lote " + codigo + " solo tiene " + Formato.kg(pesoDisponible) + " disponibles.");
        }
        pesoDisponible = pesoDisponible.subtract(kilogramos);
        if (pesoDisponible.signum() == 0) {
            estado = EstadoLote.AGOTADO;
        }
    }

    // retira el lote por vencimiento y devuelve los kilogramos que se perdieron
    BigDecimal descartar() {
        if (estado != EstadoLote.DISPONIBLE) {
            throw new ValidacionException("El lote " + codigo + " no esta disponible.");
        }
        BigDecimal perdidos = pesoDisponible;
        pesoDisponible = BigDecimal.ZERO;
        estado = EstadoLote.DESCARTADO;
        return perdidos;
    }

    // un lote nunca cambia de bodega: solo se puede asignar una vez
    void asignarBodega(Bodega nueva) {
        if (bodega != null) {
            throw new ValidacionException("El lote " + codigo + " ya tiene bodega asignada.");
        }
        bodega = nueva;
    }

    // ---- consultas ----

    public String getCodigo() {
        return codigo;
    }

    public String getProductor() {
        return productor;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public BigDecimal getPesoRecibido() {
        return pesoRecibido;
    }

    public Calidad getCalidad() {
        return calidad;
    }

    public BigDecimal getPesoDisponible() {
        return pesoDisponible;
    }

    public EstadoLote getEstado() {
        return estado;
    }

    public Bodega getBodega() {
        return bodega;
    }

    // orden de salida: perecederos primero (el que vence antes), luego no perecederos (el que ingreso antes)
    // los empates se resuelven por mejor calidad y luego por codigo en orden alfabetico
    @Override
    public int compareTo(Lote otro) {
        Optional<Vencible> mio = comoVencible();
        Optional<Vencible> suyo = otro.comoVencible();
        int resultado;
        if (mio.isPresent() && suyo.isPresent()) {
            resultado = mio.get().fechaVencimiento().compareTo(suyo.get().fechaVencimiento());
        } else if (mio.isPresent()) {
            resultado = -1;
        } else if (suyo.isPresent()) {
            resultado = 1;
        } else {
            resultado = fechaIngreso.compareTo(otro.fechaIngreso);
        }
        if (resultado != 0) {
            return resultado;
        }
        resultado = calidad.compareTo(otro.calidad);
        if (resultado != 0) {
            return resultado;
        }
        return codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        String vencimiento = comoVencible()
                .map(vencible -> "vence " + vencible.fechaVencimiento())
                .orElse("no se vence");
        String ubicacion = bodega == null ? "sin bodega" : "bodega " + bodega.getCodigo();
        return codigo + " | " + getCategoria() + " | calidad " + calidad + " | ingreso " + fechaIngreso
                + " | " + vencimiento + " | " + detalleCategoria()
                + " | disponible " + Formato.kg(pesoDisponible) + " de " + Formato.kg(pesoRecibido)
                + " | " + estado + " | " + ubicacion;
    }
}
