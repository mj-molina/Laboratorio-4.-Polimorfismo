import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// coordina las operaciones de AcopioGT: recepcion, bodegas, despachos y fecha de operacion
// toda operacion valida primero y modifica despues, asi una operacion rechazada no altera nada
public class Cooperativa {

    private static final BigDecimal FACTOR_COBRO = new BigDecimal("1.35");

    private LocalDate fechaOperacion;
    private final CatalogoCategorias catalogo;
    private final Map<String, Bodega> bodegas = new LinkedHashMap<>();
    private final Map<String, Lote> lotes = new LinkedHashMap<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private final Contabilidad contabilidad = new Contabilidad();
    private int siguientePedido = 1;

    public Cooperativa(LocalDate fechaOperacionInicial, CatalogoCategorias catalogo) {
        this.fechaOperacion = Validaciones.exigirNoNulo(fechaOperacionInicial, "La fecha de operacion");
        this.catalogo = Validaciones.exigirNoNulo(catalogo, "El catalogo de categorias");
    }

    // ---- bodegas ----

    public void registrarBodega(Bodega bodega) {
        Validaciones.exigirNoNulo(bodega, "La bodega");
        if (bodegas.containsKey(bodega.getCodigo())) {
            throw new ValidacionException("Ya existe una bodega con el codigo " + bodega.getCodigo() + ".");
        }
        bodegas.put(bodega.getCodigo(), bodega);
    }

    public Bodega buscarBodega(String codigo) {
        String normalizado = Validaciones.exigirCodigo(codigo, "El codigo de la bodega");
        Bodega bodega = bodegas.get(normalizado);
        if (bodega == null) {
            throw new ValidacionException("No existe la bodega " + normalizado + ".");
        }
        return bodega;
    }

    // ---- recepcion ----

    // carga un lote inicial: se almacena sin pasar por la recepcion, por lo que no registra pago
    public void cargarLoteInicial(Lote lote, String codigoBodega) {
        exigirCodigoLibre(lote);
        if (lote.getFechaIngreso().isAfter(fechaOperacion)) {
            throw new ValidacionException("La fecha de ingreso del lote " + lote.getCodigo()
                    + " no puede ser posterior a la fecha de operacion.");
        }
        almacenarNuevo(lote, codigoBodega);
    }

    // muestra pago y vencimiento sin registrar nada
    public PreLiquidacion preLiquidar(Lote lote) {
        validarLoteNuevo(lote);
        return new PreLiquidacion(lote);
    }

    // recibe el lote en la bodega indicada y registra el pago completo
    public BigDecimal recibirLote(Lote lote, String codigoBodega) {
        validarLoteNuevo(lote);
        almacenarNuevo(lote, codigoBodega);
        BigDecimal pago = lote.calcularPago();
        contabilidad.registrarPago(lote.getCategoria(), pago);
        return pago;
    }

    private void validarLoteNuevo(Lote lote) {
        Validaciones.exigirNoNulo(lote, "El lote");
        exigirCodigoLibre(lote);
        if (!lote.getFechaIngreso().equals(fechaOperacion)) {
            throw new ValidacionException("Todo lote nuevo ingresa en la fecha de operacion (" + fechaOperacion + ").");
        }
    }

    private void exigirCodigoLibre(Lote lote) {
        if (lotes.containsKey(lote.getCodigo())) {
            throw new ValidacionException("Ya existe un lote con el codigo " + lote.getCodigo() + ".");
        }
    }

    private void almacenarNuevo(Lote lote, String codigoBodega) {
        Bodega bodega = buscarBodega(codigoBodega);
        bodega.almacenar(lote);
        lotes.put(lote.getCodigo(), lote);
    }

    // ---- pedidos y despachos ----

    public Pedido registrarPedido(String categoria, BigDecimal kilogramos) {
        CategoriaProducto producto = catalogo.buscar(categoria);
        Validaciones.exigirPositivo(kilogramos, "Los kilogramos del pedido");
        Pedido pedido = new Pedido(siguientePedido, producto.getNombre(), kilogramos);
        siguientePedido++;
        pedidos.add(pedido);
        return pedido;
    }

    // toma kilogramos de todas las bodegas en orden de salida; si no alcanza, rechaza el pedido sin descontar nada
    public Despacho despachar(int idPedido) {
        Pedido pedido = buscarPedido(idPedido);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ValidacionException("El pedido " + idPedido + " ya esta en estado " + pedido.getEstado() + ".");
        }
        List<LineaDespacho> plan = new ArrayList<>();
        BigDecimal restante = pedido.getKilogramos();
        for (Lote lote : lotesAlmacenados(pedido.getCategoria())) {
            if (restante.signum() == 0) {
                break;
            }
            BigDecimal tomar = restante.min(lote.getPesoDisponible());
            BigDecimal importe = lote.costoDe(tomar).multiply(FACTOR_COBRO).setScale(2, RoundingMode.HALF_UP);
            plan.add(new LineaDespacho(lote, tomar, importe));
            restante = restante.subtract(tomar);
        }
        if (restante.signum() > 0) {
            pedido.marcarRechazado();
            throw new ValidacionException("Pedido " + idPedido + " rechazado: faltan " + Formato.kg(restante)
                    + " de " + pedido.getCategoria() + " para cubrir " + Formato.kg(pedido.getKilogramos()) + ".");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (LineaDespacho linea : plan) {
            Lote lote = linea.getLote();
            lote.consumir(linea.getKilogramos());
            if (lote.getEstado() == EstadoLote.AGOTADO) {
                lote.getBodega().retirar(lote);
            }
            total = total.add(linea.getImporte());
        }
        contabilidad.registrarCobro(pedido.getCategoria(), total);
        pedido.marcarDespachado(total);
        return new Despacho(pedido, plan, total);
    }

    private Pedido buscarPedido(int id) {
        for (Pedido pedido : pedidos) {
            if (pedido.getId() == id) {
                return pedido;
            }
        }
        throw new ValidacionException("No existe el pedido " + id + ".");
    }

    // ---- fecha de operacion y descarte ----

    // avanza la fecha y descarta los lotes vencidos mientras recorre cada bodega
    public List<Descarte> avanzarFecha(LocalDate nuevaFecha) {
        Validaciones.exigirNoNulo(nuevaFecha, "La nueva fecha de operacion");
        if (!nuevaFecha.isAfter(fechaOperacion)) {
            throw new ValidacionException("La fecha de operacion solo puede avanzar: actual " + fechaOperacion
                    + ", solicitada " + nuevaFecha + ".");
        }
        fechaOperacion = nuevaFecha;
        List<Descarte> descartes = new ArrayList<>();
        for (Bodega bodega : bodegas.values()) {
            Iterator<Lote> iterador = bodega.iterator();
            while (iterador.hasNext()) {
                Lote lote = iterador.next();
                if (lote.estaVencidoEn(fechaOperacion)) {
                    BigDecimal kilogramos = lote.descartar();
                    BigDecimal perdida = lote.costoDe(kilogramos).setScale(2, RoundingMode.HALF_UP);
                    contabilidad.registrarPerdida(lote.getCategoria(), perdida);
                    iterador.remove();
                    descartes.add(new Descarte(lote, kilogramos, perdida));
                }
            }
        }
        return descartes;
    }

    // ---- consultas ----

    // lotes almacenados de todas las bodegas, en orden de salida
    public List<Lote> lotesAlmacenados() {
        List<Lote> resultado = new ArrayList<>();
        for (Bodega bodega : bodegas.values()) {
            for (Lote lote : bodega) {
                resultado.add(lote);
            }
        }
        Collections.sort(resultado);
        return resultado;
    }

    public List<Lote> lotesAlmacenados(String categoria) {
        List<Lote> resultado = new ArrayList<>();
        for (Lote lote : lotesAlmacenados()) {
            if (lote.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(lote);
            }
        }
        return resultado;
    }

    public Lote buscarLote(String codigo) {
        String normalizado = Validaciones.exigirCodigo(codigo, "El codigo del lote");
        Lote lote = lotes.get(normalizado);
        if (lote == null) {
            throw new ValidacionException("No existe el lote " + normalizado + ".");
        }
        return lote;
    }

    public LocalDate getFechaOperacion() {
        return fechaOperacion;
    }

    public CatalogoCategorias getCatalogo() {
        return catalogo;
    }

    public Collection<Bodega> getBodegas() {
        return Collections.unmodifiableCollection(bodegas.values());
    }

    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    public Contabilidad getContabilidad() {
        return contabilidad;
    }
}
