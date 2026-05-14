package tpv.model;

/**
 * Classe LiniaFactura. Representa una línia dins d'un tiquet.
 */
public class LiniaFactura {

    private int idTiquet;
    private int idArticle;
    private int quantitat;
    private double preuBase;   // preu total sense IVA (preuBase unitari * quantitat)
    private double iva;        // percentatge d'IVA aplicat
    private double preuFinal;  // preu total amb IVA

    public LiniaFactura(int idTiquet, int idArticle, int quantitat,
                        double preuBaseUnitari, double iva) {
        this.idTiquet = idTiquet;
        this.idArticle = idArticle;
        this.quantitat = quantitat;
        this.iva = iva;
        this.preuBase = preuBaseUnitari * quantitat;
        this.preuFinal = this.preuBase * (1 + iva / 100.0);
    }

    public LiniaFactura() {}

    public int getIdTiquet() { return idTiquet; }
    public void setIdTiquet(int idTiquet) { this.idTiquet = idTiquet; }

    public int getIdArticle() { return idArticle; }
    public void setIdArticle(int idArticle) { this.idArticle = idArticle; }

    public int getQuantitat() { return quantitat; }
    public void setQuantitat(int quantitat) { this.quantitat = quantitat; }

    public double getPreuBase() { return preuBase; }
    public void setPreuBase(double preuBase) { this.preuBase = preuBase; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getPreuFinal() { return preuFinal; }
    public void setPreuFinal(double preuFinal) { this.preuFinal = preuFinal; }

    @Override
    public String toString() {
        return String.format("  Article #%d | Quantitat: %d | Base: %.2f€ | IVA: %.0f%% | Total: %.2f€",
                idArticle, quantitat, preuBase, iva, preuFinal);
    }
}
