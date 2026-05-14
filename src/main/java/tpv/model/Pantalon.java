package tpv.model;

/**
 * Classe Pantaló. Hereta d'Article.
 * Camps específics: talla_cintura (24-56), llargada_camal (32-46)
 */
public class Pantalon extends Article {

    private int tallaCintura;   // entre 24 i 56
    private int llargadaCamal;  // entre 32 i 46

    public Pantalon(int id, String nom, double preuBase, int iva, int stock,
                    int tallaCintura, int llargadaCamal) {
        super(id, nom, "pantaló", preuBase, iva, stock);
        this.tallaCintura = tallaCintura;
        this.llargadaCamal = llargadaCamal;
    }

    public Pantalon() {
        super();
    }

    /**
     * Fórmula del pràctica:
     * Preu_Cost = preu_base * 0,30 + llargada_camal * 0,2
     */
    @Override
    public double calcularPreuCost() {
        return getPreuBase() * 0.30 + llargadaCamal * 0.2;
    }

    public int getTallaCintura() { return tallaCintura; }
    public void setTallaCintura(int tallaCintura) { this.tallaCintura = tallaCintura; }

    public int getLlargadaCamal() { return llargadaCamal; }
    public void setLlargadaCamal(int llargadaCamal) { this.llargadaCamal = llargadaCamal; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Talla cintura: %d | Llargada camal: %d",
                tallaCintura, llargadaCamal);
    }
}
