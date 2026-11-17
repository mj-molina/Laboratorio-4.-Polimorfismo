import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// resultado de despachar un pedido: lineas por lote y total cobrado
public class Despacho {

    private final Pedido pedido;
    private final List<LineaDespacho> lineas;
    private final BigDecimal total;

    public Despacho(Pedido pedido, List<LineaDespacho> lineas, BigDecimal total) {
        this.pedido = pedido;
        this.lineas = new ArrayList<>(lineas);
        this.total = total;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public List<LineaDespacho> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public BigDecimal getTotal() {
        return total;
    }
}
