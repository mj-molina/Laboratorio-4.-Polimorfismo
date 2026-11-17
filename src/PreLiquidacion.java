import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

// calculo informativo de pago y vencimiento de un lote; no registra nada en el sistema
public class PreLiquidacion {

    private final Lote lote;

    public PreLiquidacion(Lote lote) {
        this.lote = lote;
    }

    public Lote getLote() {
        return lote;
    }

    public BigDecimal getPago() {
        return lote.calcularPago();
    }

    public Optional<LocalDate> getFechaVencimiento() {
        return lote.comoVencible().map(Vencible::fechaVencimiento);
    }
}
