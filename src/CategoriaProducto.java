import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// describe una categoria para el menu: su nombre, los datos extra que pide y como se crea su lote
public class CategoriaProducto {

    private final String nombre;
    private final List<Campo> campos;
    private final FabricaLote fabrica;

    public CategoriaProducto(String nombre, List<Campo> campos, FabricaLote fabrica) {
        this.nombre = Validaciones.exigirTexto(nombre, "El nombre de la categoria");
        this.campos = new ArrayList<>(campos);
        this.fabrica = Validaciones.exigirNoNulo(fabrica, "La fabrica de lotes");
    }

    public String getNombre() {
        return nombre;
    }

    public List<Campo> getCampos() {
        return Collections.unmodifiableList(campos);
    }

    public Lote crearLote(DatosLote datos) {
        return fabrica.crear(datos);
    }
}
