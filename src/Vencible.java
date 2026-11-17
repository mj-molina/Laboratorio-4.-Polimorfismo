import java.time.LocalDate;

// capacidad de vencer: la comparten categorias sin parentesco (cardamomo, hortaliza, queso fresco)
// la fecha de vencimiento es ingreso mas vida util y esta vencido a partir del dia siguiente
public interface Vencible {

    LocalDate getFechaIngreso();

    int getVidaUtilDias();

    default LocalDate fechaVencimiento() {
        return getFechaIngreso().plusDays(getVidaUtilDias());
    }

    default boolean estaVencido(LocalDate hoy) {
        return hoy.isAfter(fechaVencimiento());
    }
}
