import java.math.BigDecimal;

// kilogramos tomados de un lote para un despacho y su importe cobrado
public class LineaDespacho {

    private final Lote lote;
    private final BigDecimal kilogramos;
    private final BigDecimal importe;

    public LineaDespacho(Lote lote, BigDecimal kilogramos, BigDecimal importe) {
        this.lote = lote;
        this.kilogramos = kilogramos;
        this.importe = importe;
    }

    public Lote getLote() {
        return lote;
    }

    public BigDecimal getKilogramos() {
        return kilogramos;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    @Override
    public String toString() {
        return lote.getCodigo() + " (bodega " + lote.getBodega().getCodigo() + "): " + Formato.kg(kilogramos)
                + " a " + Formato.dinero(lote.costoPorKg()) + "/kg + 35 % = " + Formato.dinero(importe);
    }
}
