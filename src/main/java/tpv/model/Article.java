package tpv.model;

/**
 * Classe abstracta que representa un Article genèric de la botiga.
 * Les classes Camisa i Pantaló hereten d'aquesta.
 */
public abstract class Article {

    private int id;
    private String nom;
    private String familia;
    private double preuBase;
    private int iva;
    private int stock;

    // Constructor
    public Article(int id, String nom, String familia, double preuBase, int iva, int stock) {
        this.id = id;
        this.nom = nom;
        this.familia = familia;
        this.preuBase = preuBase;
        this.iva = iva;
        this.stock = stock;
    }

    // Constructor buit (útil per instanciar des de DAO)
    public Article() {}

    // Mètode abstracte: cada subclasse calcula el seu preu de cost diferent
    public abstract double calcularPreuCost();

    // Preu final amb IVA
    public double calcularPreuFinal() {
        return preuBase * (1 + iva / 100.0);
    }

    // Getters i Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getFamilia() { return familia; }
    public void setFamilia(String familia) { this.familia = familia; }

    public double getPreuBase() { return preuBase; }
    public void setPreuBase(double preuBase) { this.preuBase = preuBase; }

    public int getIva() { return iva; }
    public void setIva(int iva) { this.iva = iva; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return String.format("[%d] %s | Família: %s | Preu base: %.2f€ | IVA: %d%% | Stock: %d",
                id, nom, familia, preuBase, iva, stock);
    }
}
