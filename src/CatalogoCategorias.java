import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// registro de las categorias que maneja la cooperativa; agregar una categoria nueva es registrarla aqui
public class CatalogoCategorias {

    private final List<CategoriaProducto> categorias = new ArrayList<>();

    public void registrar(CategoriaProducto categoria) {
        for (CategoriaProducto existente : categorias) {
            if (existente.getNombre().equalsIgnoreCase(categoria.getNombre())) {
                throw new ValidacionException("La categoria " + categoria.getNombre() + " ya esta registrada.");
            }
        }
        categorias.add(categoria);
    }

    public List<CategoriaProducto> getCategorias() {
        return Collections.unmodifiableList(categorias);
    }

    public CategoriaProducto buscar(String nombre) {
        String limpio = Validaciones.exigirTexto(nombre, "La categoria");
        for (CategoriaProducto categoria : categorias) {
            if (categoria.getNombre().equalsIgnoreCase(limpio)) {
                return categoria;
            }
        }
        throw new ValidacionException("No existe la categoria \"" + limpio + "\".");
    }

    public static CatalogoCategorias porDefecto() {
        CatalogoCategorias catalogo = new CatalogoCategorias();
        catalogo.registrar(new CategoriaProducto(CafePergamino.CATEGORIA,
                List.of(new Campo("humedad", "Humedad del grano en % (8 a 14)")),
                CafePergamino::crear));
        catalogo.registrar(new CategoriaProducto(CafeOrganico.CATEGORIA_ORGANICO,
                List.of(new Campo("humedad", "Humedad del grano en % (8 a 14)"),
                        new Campo("entidad", "Entidad certificadora"),
                        new Campo("selloHasta", "Sello valido hasta (aaaa-mm-dd)")),
                CafeOrganico::crearOrganico));
        catalogo.registrar(new CategoriaProducto(Cardamomo.CATEGORIA,
                List.of(new Campo("granoVerde", "Grano verde en % (0 a 100)")),
                Cardamomo::crear));
        catalogo.registrar(new CategoriaProducto(Hortaliza.CATEGORIA,
                List.of(new Campo("vidaUtil", "Vida util en dias (3 a 14)")),
                Hortaliza::crear));
        catalogo.registrar(new CategoriaProducto(QuesoFresco.CATEGORIA,
                List.of(new Campo("grasa", "Grasa en % (10 a 40)")),
                QuesoFresco::crear));
        return catalogo;
    }
}
