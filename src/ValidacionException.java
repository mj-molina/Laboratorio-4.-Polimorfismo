// excepcion para toda operacion que incumple una regla de negocio o una validacion de datos
public class ValidacionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
