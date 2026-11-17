import java.math.BigDecimal;
import java.time.LocalDate;

// datos con los que inicia el programa; se cargan directamente, sin recepcion, por lo que no hay pagos ni cobros
public final class DatosIniciales {

    public static final LocalDate FECHA_INICIAL = LocalDate.of(2026, 11, 15);

    private DatosIniciales() {
    }

    public static void cargar(Cooperativa cooperativa) {
        cooperativa.registrarBodega(new BodegaSeca("SEC-01", kg("1500")));
        cooperativa.registrarBodega(new BodegaSeca("SEC-02", kg("800")));
        cooperativa.registrarBodega(new CamaraFria("FRIA-01", kg("500")));

        // cafe en pergamino: CP-002 y CP-001 ingresaron el mismo dia, el empate lo resuelve la calidad (A antes que B)
        cooperativa.cargarLoteInicial(new CafePergamino("CP-001", "Maria Lopez", fecha(2026, 11, 1), kg("300"),
                Calidad.B, new BigDecimal("11")), "SEC-02");
        cooperativa.cargarLoteInicial(new CafePergamino("CP-002", "Juan Perez", fecha(2026, 11, 1), kg("500"),
                Calidad.A, new BigDecimal("13.4")), "SEC-01");

        // cafe organico
        cooperativa.cargarLoteInicial(new CafeOrganico("CO-001", "Rosa Mendez", fecha(2026, 11, 5), kg("200"),
                Calidad.A, new BigDecimal("12.5"), "OrgaCert GT", fecha(2027, 6, 30)), "SEC-01");
        cooperativa.cargarLoteInicial(new CafeOrganico("CO-002", "Luis Castillo", fecha(2026, 11, 8), kg("150"),
                Calidad.B, new BigDecimal("10"), "OrgaCert GT", fecha(2027, 3, 31)), "SEC-02");

        // cardamomo: CD-002 vence el 2026-11-16 (a punto de vencer) y CD-001 el 2026-11-28
        cooperativa.cargarLoteInicial(new Cardamomo("CD-001", "Pedro Xol", fecha(2026, 6, 1), kg("80"),
                Calidad.B, new BigDecimal("90")), "SEC-01");
        cooperativa.cargarLoteInicial(new Cardamomo("CD-002", "Elena Caal", fecha(2026, 5, 20), kg("120"),
                Calidad.C, new BigDecimal("70")), "SEC-02");

        // hortaliza: HT-001 vence hoy (2026-11-15) y queda vencida desde el 2026-11-16
        cooperativa.cargarLoteInicial(new Hortaliza("HT-001", "Carlos Ajpop", fecha(2026, 11, 8), kg("200"),
                Calidad.A, 7), "FRIA-01");
        cooperativa.cargarLoteInicial(new Hortaliza("HT-002", "Sofia Tzoc", fecha(2026, 11, 12), kg("150"),
                Calidad.B, 10), "FRIA-01");

        // queso fresco
        cooperativa.cargarLoteInicial(new QuesoFresco("QF-001", "Ana Garcia", fecha(2026, 11, 10), kg("40"),
                Calidad.A, new BigDecimal("25")), "FRIA-01");
        cooperativa.cargarLoteInicial(new QuesoFresco("QF-002", "Jose Morales", fecha(2026, 11, 12), kg("60"),
                Calidad.B, new BigDecimal("30")), "FRIA-01");
    }

    private static BigDecimal kg(String valor) {
        return new BigDecimal(valor);
    }

    private static LocalDate fecha(int anio, int mes, int dia) {
        return LocalDate.of(anio, mes, dia);
    }
}
