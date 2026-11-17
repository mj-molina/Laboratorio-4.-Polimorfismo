// calidad del lote; el orden natural del enum (A, B, C) es de mayor a menor calidad
public enum Calidad {
    A, B, C;

    public static Calidad desde(String texto) {
        String limpio = Validaciones.exigirTexto(texto, "La calidad").toUpperCase();
        for (Calidad calidad : values()) {
            if (calidad.name().equals(limpio)) {
                return calidad;
            }
        }
        throw new ValidacionException("La calidad debe ser A, B o C.");
    }
}
