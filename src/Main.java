import java.util.Scanner;

// punto de entrada: solo inicializa el programa y le cede el control al menu
public class Main {

    public static void main(String[] args) {
        Cooperativa cooperativa = new Cooperativa(DatosIniciales.FECHA_INICIAL, CatalogoCategorias.porDefecto());
        DatosIniciales.cargar(cooperativa);
        new Menu(cooperativa, new Scanner(System.in)).ejecutar();
    }
}
