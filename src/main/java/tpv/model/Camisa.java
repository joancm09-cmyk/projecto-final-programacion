package tpv.model;

/**
 * Classe Camisa. Hereta d'Article.
 * Camps específics: talla_coll (36-52), amplada_pit (10-15)
 */
public class Camisa extends Article {

    private int tallaColl;    // entre 36 i 52
    private int ampladaPit;   // entre 10 i 15

    public Camisa(int id, String nom, double preuBase, int iva, int stock,
                  int tallaColl, int ampladaPit) {
        super(id, nom, "camisa", preuBase, iva, stock);
        this.tallaColl = tallaColl;
        this.ampladaPit = ampladaPit;
    }

    public Camisa() {
        super();
    }

    /**
     * Fórmula del pràctica:
     * Preu_Cost = preu_base * 0,35 + talla_coll * 0,3
     */
    @Override
    public double calcularPreuCost() {
        return getPreuBase() * 0.35 + tallaColl * 0.3;
    }

    public int getTallaColl() { return tallaColl; }
    public void setTallaColl(int tallaColl) { this.tallaColl = tallaColl; }

    public int getAmpladaPit() { return ampladaPit; }
    public void setAmpladaPit(int ampladaPit) { this.ampladaPit = ampladaPit; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Talla coll: %d | Amplada pit: %d",
                tallaColl, ampladaPit);
    }
}
