import java.math.BigDecimal;

// lote retirado por vencimiento, con los kilogramos perdidos y el costo de esa perdida
public class Descarte {

    private final Lote lote;
    private final BigDecimal kilogramos;
    private final BigDecimal perdida;

    public Descarte(Lote lote, BigDecimal kilogramos, BigDecimal perdida) {
        this.lote = lote;
        this.kilogramos = kilogramos;
        this.perdida = perdida;
    }

    public Lote getLote() {
        return lote;
    }

    public BigDecimal getKilogramos() {
        return kilogramos;
    }

    public BigDecimal getPerdida() {
        return perdida;
    }

    @Override
    public String toString() {
        return lote.getCodigo() + " (" + lote.getCategoria() + "): " + Formato.kg(kilogramos)
                + " descartados, perdida " + Formato.dinero(perdida);
    }
}
