import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// genera el texto de los reportes de gerencia a partir del estado de la cooperativa; no modifica nada
public final class Reportes {

    private Reportes() {
    }

    public static String existenciasPorCategoria(Cooperativa cooperativa) {
        StringBuilder texto = new StringBuilder();
        texto.append("=== EXISTENCIAS POR CATEGORIA (fecha de operacion ")
                .append(cooperativa.getFechaOperacion()).append(") ===\n");
        for (CategoriaProducto categoria : cooperativa.getCatalogo().getCategorias()) {
            List<Lote> lotes = cooperativa.lotesAlmacenados(categoria.getNombre());
            BigDecimal total = BigDecimal.ZERO;
            for (Lote lote : lotes) {
                total = total.add(lote.getPesoDisponible());
            }
            texto.append(categoria.getNombre()).append(": ").append(Formato.kg(total)).append(" disponibles\n");
            if (lotes.isEmpty()) {
                texto.append("   (sin existencias)\n");
            }
            for (Lote lote : lotes) {
                texto.append("   - ").append(lote).append("\n");
            }
        }
        texto.append("\n=== CONTENIDO DE CADA BODEGA (orden de salida) ===\n");
        for (Bodega bodega : cooperativa.getBodegas()) {
            texto.append(contenidoBodega(bodega));
        }
        return texto.toString();
    }

    public static String contenidoBodega(Bodega bodega) {
        StringBuilder texto = new StringBuilder();
        texto.append(bodega).append("\n");
        if (bodega.cantidadLotes() == 0) {
            texto.append("   (vacia)\n");
        }
        int posicion = 1;
        for (Lote lote : bodega) {
            texto.append("   ").append(posicion).append(". ").append(lote).append("\n");
            posicion++;
        }
        return texto.toString();
    }

    public static String proximosAVencer(Cooperativa cooperativa) {
        LocalDate hoy = cooperativa.getFechaOperacion();
        LocalDate limite = hoy.plusDays(3);
        StringBuilder texto = new StringBuilder();
        texto.append("=== LOTES PERECEDEROS QUE VENCEN ENTRE ").append(hoy).append(" Y ").append(limite).append(" ===\n");
        int cantidad = 0;
        for (Lote lote : cooperativa.lotesAlmacenados()) {
            boolean proximo = lote.comoVencible()
                    .map(vencible -> !vencible.fechaVencimiento().isBefore(hoy) && !vencible.fechaVencimiento().isAfter(limite))
                    .orElse(false);
            if (proximo) {
                texto.append("   - ").append(lote).append("\n");
                cantidad++;
            }
        }
        if (cantidad == 0) {
            texto.append("   (ningun lote vence en los proximos 3 dias)\n");
        }
        return texto.toString();
    }

    public static String totalesPorCategoria(Cooperativa cooperativa) {
        Contabilidad contabilidad = cooperativa.getContabilidad();
        StringBuilder texto = new StringBuilder();
        texto.append("=== TOTALES POR CATEGORIA ===\n");
        for (CategoriaProducto categoria : cooperativa.getCatalogo().getCategorias()) {
            String nombre = categoria.getNombre();
            texto.append(nombre).append(":\n")
                    .append("   pagado a productores: ").append(Formato.dinero(contabilidad.totalPagado(nombre))).append("\n")
                    .append("   cobrado en despachos: ").append(Formato.dinero(contabilidad.totalCobrado(nombre))).append("\n")
                    .append("   perdidas por vencimiento: ").append(Formato.dinero(contabilidad.totalPerdidas(nombre))).append("\n");
        }
        texto.append("TOTAL GENERAL:\n")
                .append("   pagado a productores: ").append(Formato.dinero(contabilidad.totalPagado())).append("\n")
                .append("   cobrado en despachos: ").append(Formato.dinero(contabilidad.totalCobrado())).append("\n")
                .append("   perdidas por vencimiento: ").append(Formato.dinero(contabilidad.totalPerdidas())).append("\n");
        return texto.toString();
    }
}
