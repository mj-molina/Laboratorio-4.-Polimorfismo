// dato propio de una categoria que el menu debe pedir al usuario
public class Campo {

    private final String clave;
    private final String etiqueta;

    public Campo(String clave, String etiqueta) {
        this.clave = clave;
        this.etiqueta = etiqueta;
    }

    public String getClave() {
        return clave;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
