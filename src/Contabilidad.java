import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// totales pagados, cobrados y perdidos, separados por categoria
public class Contabilidad {

    private final Map<String, BigDecimal> pagado = new LinkedHashMap<>();
    private final Map<String, BigDecimal> cobrado = new LinkedHashMap<>();
    private final Map<String, BigDecimal> perdidas = new LinkedHashMap<>();

    void registrarPago(String categoria, BigDecimal monto) {
        acumular(pagado, categoria, monto);
    }

    void registrarCobro(String categoria, BigDecimal monto) {
        acumular(cobrado, categoria, monto);
    }

    void registrarPerdida(String categoria, BigDecimal monto) {
        acumular(perdidas, categoria, monto);
    }

    public BigDecimal totalPagado(String categoria) {
        return pagado.getOrDefault(categoria, BigDecimal.ZERO);
    }

    public BigDecimal totalCobrado(String categoria) {
        return cobrado.getOrDefault(categoria, BigDecimal.ZERO);
    }

    public BigDecimal totalPerdidas(String categoria) {
        return perdidas.getOrDefault(categoria, BigDecimal.ZERO);
    }

    public BigDecimal totalPagado() {
        return sumar(pagado);
    }

    public BigDecimal totalCobrado() {
        return sumar(cobrado);
    }

    public BigDecimal totalPerdidas() {
        return sumar(perdidas);
    }

    private void acumular(Map<String, BigDecimal> mapa, String categoria, BigDecimal monto) {
        mapa.merge(categoria, monto, BigDecimal::add);
    }

    private BigDecimal sumar(Map<String, BigDecimal> mapa) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal valor : mapa.values()) {
            total = total.add(valor);
        }
        return total;
    }
}
