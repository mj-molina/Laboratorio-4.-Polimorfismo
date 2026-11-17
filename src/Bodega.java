import java.math.BigDecimal;
import java.util.Iterator;
import java.util.TreeSet;

// clase abstracta de bodega: guarda sus lotes siempre en orden de salida y se recorre con for-each
// aqui se valida la capacidad; cada subclase decide que lotes admite (compatibilidad con el tipo de bodega)
public abstract class Bodega implements Iterable<Lote> {

    private final String codigo;
    private final BigDecimal capacidadKg;
    private final TreeSet<Lote> lotes = new TreeSet<>();

    protected Bodega(String codigo, BigDecimal capacidadKg) {
        this.codigo = Validaciones.exigirCodigo(codigo, "El codigo de la bodega");
        this.capacidadKg = Validaciones.exigirPositivo(capacidadKg, "La capacidad de la bodega");
    }

    // decide si el lote es compatible con este tipo de bodega
    public abstract boolean admite(Lote lote);

    public abstract String getTipo();

    public BigDecimal kgOcupados() {
        BigDecimal total = BigDecimal.ZERO;
        for (Lote lote : lotes) {
            total = total.add(lote.getPesoDisponible());
        }
        return total;
    }

    public BigDecimal kgLibres() {
        return capacidadKg.subtract(kgOcupados());
    }

    // lanza ValidacionException si el lote no es compatible o excede la capacidad; no modifica nada
    void validarRecepcion(Lote lote) {
        if (!admite(lote)) {
            throw new ValidacionException("El lote " + lote.getCodigo() + " (" + lote.getCategoria()
                    + ") no es compatible con la bodega " + codigo + " (" + getTipo() + ").");
        }
        if (kgOcupados().add(lote.getPesoRecibido()).compareTo(capacidadKg) > 0) {
            throw new ValidacionException("El lote " + lote.getCodigo() + " excede la capacidad de la bodega "
                    + codigo + ": libres " + Formato.kg(kgLibres()) + ", requeridos " + Formato.kg(lote.getPesoRecibido()) + ".");
        }
    }

    void almacenar(Lote lote) {
        validarRecepcion(lote);
        lote.asignarBodega(this);
        lotes.add(lote);
    }

    void retirar(Lote lote) {
        lotes.remove(lote);
    }

    public int cantidadLotes() {
        return lotes.size();
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getCapacidadKg() {
        return capacidadKg;
    }

    // recorre los lotes en orden de salida; el iterador permite retirar lotes mientras se recorre
    @Override
    public Iterator<Lote> iterator() {
        return lotes.iterator();
    }

    @Override
    public String toString() {
        return codigo + " | " + getTipo() + " | capacidad " + Formato.kg(capacidadKg)
                + " | ocupados " + Formato.kg(kgOcupados()) + " | libres " + Formato.kg(kgLibres())
                + " | lotes " + lotes.size();
    }
}
