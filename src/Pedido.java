import java.math.BigDecimal;

// pedido de un comprador: categoria y kilogramos; se despacha completo o se rechaza
public class Pedido {

    private final int id;
    private final String categoria;
    private final BigDecimal kilogramos;
    private EstadoPedido estado;
    private BigDecimal totalCobrado;

    Pedido(int id, String categoria, BigDecimal kilogramos) {
        this.id = id;
        this.categoria = categoria;
        this.kilogramos = kilogramos;
        this.estado = EstadoPedido.PENDIENTE;
        this.totalCobrado = BigDecimal.ZERO;
    }

    void marcarDespachado(BigDecimal total) {
        this.estado = EstadoPedido.DESPACHADO;
        this.totalCobrado = total;
    }

    void marcarRechazado() {
        this.estado = EstadoPedido.RECHAZADO;
    }

    public int getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getKilogramos() {
        return kilogramos;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public BigDecimal getTotalCobrado() {
        return totalCobrado;
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " | " + categoria + " | " + Formato.kg(kilogramos) + " | " + estado
                + (estado == EstadoPedido.DESPACHADO ? " | cobrado " + Formato.dinero(totalCobrado) : "");
    }
}
