// contrato para crear el lote de una categoria a partir de los datos en bruto
@FunctionalInterface
public interface FabricaLote {

    Lote crear(DatosLote datos);
}
