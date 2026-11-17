import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

// interfaz de consola: lee datos, delega en la cooperativa y muestra resultados
// toda operacion rechazada muestra un mensaje claro y vuelve al menu sin cerrar el programa
public class Menu {

    private final Cooperativa cooperativa;
    private final Scanner entrada;
    private boolean entradaTerminada;

    public Menu(Cooperativa cooperativa, Scanner entrada) {
        this.cooperativa = cooperativa;
        this.entrada = entrada;
        this.entradaTerminada = false;
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir && !entradaTerminada) {
            mostrarMenu();
            String opcion = leer("Opcion: ");
            try {
                salir = procesar(opcion);
            } catch (ValidacionException e) {
                System.out.println("OPERACION RECHAZADA: " + e.getMessage());
            }
        }
        System.out.println("Hasta pronto.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== ACOPIOGT | fecha de operacion: " + cooperativa.getFechaOperacion() + " =====");
        System.out.println("1. Pre-liquidar y recibir un lote");
        System.out.println("2. Registrar una bodega");
        System.out.println("3. Registrar un pedido");
        System.out.println("4. Despachar un pedido");
        System.out.println("5. Avanzar la fecha de operacion");
        System.out.println("6. Consultar lotes y bodegas");
        System.out.println("7. Reportes");
        System.out.println("0. Salir");
    }

    // devuelve true cuando el usuario decide salir
    private boolean procesar(String opcion) {
        switch (opcion) {
            case "1":
                recibirLote();
                return false;
            case "2":
                registrarBodega();
                return false;
            case "3":
                registrarPedido();
                return false;
            case "4":
                despacharPedido();
                return false;
            case "5":
                avanzarFecha();
                return false;
            case "6":
                consultar();
                return false;
            case "7":
                reportes();
                return false;
            case "0":
                return true;
            default:
                if (!entradaTerminada) {
                    System.out.println("Opcion no valida, escriba un numero del menu.");
                }
                return false;
        }
    }

    // ---- opciones ----

    private void recibirLote() {
        CategoriaProducto categoria = elegirCategoria();
        DatosLote datos = new DatosLote(cooperativa.getFechaOperacion());
        datos.poner(DatosLote.CODIGO, leer("Codigo del lote: "));
        datos.poner(DatosLote.PRODUCTOR, leer("Nombre del productor: "));
        datos.poner(DatosLote.PESO, leer("Peso recibido en kg: "));
        datos.poner(DatosLote.CALIDAD, leer("Calidad (A, B o C): "));
        for (Campo campo : categoria.getCampos()) {
            datos.poner(campo.getClave(), leer(campo.getEtiqueta() + ": "));
        }
        Lote lote = categoria.crearLote(datos);

        PreLiquidacion preLiquidacion = cooperativa.preLiquidar(lote);
        System.out.println();
        System.out.println("--- PRE-LIQUIDACION (no se ha registrado nada) ---");
        System.out.println("Lote: " + lote.getCodigo() + " | " + lote.getCategoria() + " | " + Formato.kg(lote.getPesoRecibido()));
        System.out.println("Pago al productor: " + Formato.dinero(preLiquidacion.getPago()));
        System.out.println("Costo por kilogramo: " + Formato.dinero(lote.costoPorKg()));
        System.out.println("Vencimiento: " + preLiquidacion.getFechaVencimiento()
                .map(LocalDate::toString).orElse("no se vence"));

        String respuesta = leer("El productor acepta? (s/n): ").toLowerCase();
        if (!respuesta.equals("s")) {
            System.out.println("Pre-liquidacion descartada. No se registro ningun lote ni pago.");
            return;
        }
        System.out.println("Bodegas disponibles:");
        for (Bodega bodega : cooperativa.getBodegas()) {
            System.out.println("   " + bodega);
        }
        String codigoBodega = leer("Codigo de la bodega destino: ");
        BigDecimal pago = cooperativa.recibirLote(lote, codigoBodega);
        System.out.println("Lote recibido y disponible en la bodega " + lote.getBodega().getCodigo()
                + ". Pago registrado: " + Formato.dinero(pago));
    }

    private void registrarBodega() {
        System.out.println("Tipo de bodega: 1. Bodega seca  2. Camara fria");
        String tipo = leer("Tipo: ");
        String codigo = leer("Codigo de la bodega: ");
        BigDecimal capacidad = Validaciones.parsearDecimal(leer("Capacidad maxima en kg: "), "La capacidad");
        Bodega bodega;
        if (tipo.equals("1")) {
            bodega = new BodegaSeca(codigo, capacidad);
        } else if (tipo.equals("2")) {
            bodega = new CamaraFria(codigo, capacidad);
        } else {
            throw new ValidacionException("El tipo de bodega debe ser 1 o 2.");
        }
        cooperativa.registrarBodega(bodega);
        System.out.println("Bodega registrada: " + bodega);
    }

    private void registrarPedido() {
        CategoriaProducto categoria = elegirCategoria();
        BigDecimal kilogramos = Validaciones.parsearDecimal(leer("Kilogramos que necesita el comprador: "), "Los kilogramos");
        Pedido pedido = cooperativa.registrarPedido(categoria.getNombre(), kilogramos);
        System.out.println("Pedido registrado: " + pedido);
    }

    private void despacharPedido() {
        List<Pedido> pedidos = cooperativa.getPedidos();
        if (pedidos.isEmpty()) {
            throw new ValidacionException("No hay pedidos registrados.");
        }
        System.out.println("Pedidos:");
        for (Pedido pedido : pedidos) {
            System.out.println("   " + pedido);
        }
        int id = Validaciones.parsearEntero(leer("Numero del pedido a despachar: "), "El numero de pedido");
        Despacho despacho = cooperativa.despachar(id);
        System.out.println("--- DESPACHO DEL PEDIDO " + id + " ---");
        for (LineaDespacho linea : despacho.getLineas()) {
            System.out.println("   " + linea);
        }
        System.out.println("TOTAL COBRADO: " + Formato.dinero(despacho.getTotal()));
    }

    private void avanzarFecha() {
        LocalDate nueva = Validaciones.parsearFecha(leer("Nueva fecha de operacion (aaaa-mm-dd): "), "La fecha");
        List<Descarte> descartes = cooperativa.avanzarFecha(nueva);
        System.out.println("Fecha de operacion: " + cooperativa.getFechaOperacion());
        if (descartes.isEmpty()) {
            System.out.println("No se descarto ningun lote.");
            return;
        }
        System.out.println("Lotes descartados por vencimiento:");
        for (Descarte descarte : descartes) {
            System.out.println("   " + descarte);
        }
    }

    private void consultar() {
        System.out.println("1. Todos los lotes almacenados (orden de salida)");
        System.out.println("2. Contenido de una bodega");
        System.out.println("3. Listado de bodegas");
        System.out.println("4. Buscar un lote por codigo");
        System.out.println("5. Pedidos registrados");
        String opcion = leer("Opcion: ");
        switch (opcion) {
            case "1":
                for (Lote lote : cooperativa.lotesAlmacenados()) {
                    System.out.println("   " + lote);
                }
                break;
            case "2":
                Bodega bodega = cooperativa.buscarBodega(leer("Codigo de la bodega: "));
                System.out.print(Reportes.contenidoBodega(bodega));
                break;
            case "3":
                for (Bodega actual : cooperativa.getBodegas()) {
                    System.out.println("   " + actual);
                }
                break;
            case "4":
                System.out.println("   " + cooperativa.buscarLote(leer("Codigo del lote: ")));
                break;
            case "5":
                for (Pedido pedido : cooperativa.getPedidos()) {
                    System.out.println("   " + pedido);
                }
                break;
            default:
                System.out.println("Opcion no valida.");
        }
    }

    private void reportes() {
        System.out.println("1. Existencias por categoria y contenido de bodegas");
        System.out.println("2. Lotes perecederos que vencen en los proximos 3 dias");
        System.out.println("3. Totales pagados, cobrados y perdidos por categoria");
        String opcion = leer("Opcion: ");
        switch (opcion) {
            case "1":
                System.out.print(Reportes.existenciasPorCategoria(cooperativa));
                break;
            case "2":
                System.out.print(Reportes.proximosAVencer(cooperativa));
                break;
            case "3":
                System.out.print(Reportes.totalesPorCategoria(cooperativa));
                break;
            default:
                System.out.println("Opcion no valida.");
        }
    }

    // ---- lectura ----

    private CategoriaProducto elegirCategoria() {
        List<CategoriaProducto> categorias = cooperativa.getCatalogo().getCategorias();
        System.out.println("Categorias:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + categorias.get(i).getNombre());
        }
        int numero = Validaciones.parsearEntero(leer("Categoria: "), "El numero de categoria");
        if (numero < 1 || numero > categorias.size()) {
            throw new ValidacionException("La categoria debe estar entre 1 y " + categorias.size() + ".");
        }
        return categorias.get(numero - 1);
    }

    // lee una linea; si la entrada se acaba devuelve texto vacio y marca el fin para cerrar el menu
    private String leer(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            entradaTerminada = true;
            System.out.println();
            return "";
        }
        return entrada.nextLine().trim();
    }
}
