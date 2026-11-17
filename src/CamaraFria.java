// camara fria: solo admite lotes que requieren camara fria
public class CamaraFria extends Bodega {

    public CamaraFria(String codigo, java.math.BigDecimal capacidadKg) {
        super(codigo, capacidadKg);
    }

    @Override
    public boolean admite(Lote lote) {
        return lote.comoRefrigerable().isPresent();
    }

    @Override
    public String getTipo() {
        return "Camara fria";
    }
}
