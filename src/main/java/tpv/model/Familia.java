package tpv.model;

public enum Familia {
    PANTALO(1, "pantaló"),
    CAMISA(2, "camisa");

    private final int codi;
    private final String nom;

    Familia(int codi, String nom) {
        this.codi = codi;
        this.nom = nom;
    }

    public int getCodi() { return codi; }
    public String getNom() { return nom; }

    public static Familia perCodi(int codi) {
        for (Familia f : values()) {
            if (f.codi == codi) return f;
        }
        throw new IllegalArgumentException("Família no trobada: " + codi);
    }

    public static Familia perNom(String nom) {
        for (Familia f : values()) {
            if (f.nom.equalsIgnoreCase(nom)) return f;
        }
        throw new IllegalArgumentException("Família no trobada: " + nom);
    }

    @Override
    public String toString() {
        return codi + ". " + nom;
    }
}