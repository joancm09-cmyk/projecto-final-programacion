package tpv;

import tpv.db.Connexio;
import tpv.json.GestorJson;
import tpv.model.Article;
import tpv.model.Camisa;
import tpv.model.Pantalon;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal del TPV.
 * Gestiona el menú principal i delega a les classes corresponents.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final GestorJson jsonReader = new GestorJson();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         TPV - Botiga de Roba         ║");
        System.out.println("╚══════════════════════════════════════╝");

        int opcio;
        do {
            mostrarMenuPrincipal();
            opcio = llegirEnter("Selecciona una opció: ");

            switch (opcio) {
                case 1 -> importacioArticles();
                case 2 -> gestioArticles();
                case 3 -> gestioClients();
                case 4 -> tpv();
                case 5 -> consultaVendesPerClient();
                case 6 -> consultaVendesPerArticle();
                case 7 -> calcularBeneficis();
                case 8 -> recompraAutomatica();
                case 0 -> {
                    System.out.println("\nTancant l'aplicació... Fins aviat!");
                    Connexio.tancarConnexio();
                }
                default -> System.out.println("⚠ Opció no vàlida. Torna a intentar-ho.");
            }

        } while (opcio != 0);

        sc.close();
    }

    // ─── MENÚ PRINCIPAL ───────────────────────────────────────────────────────

    private static void mostrarMenuPrincipal() {
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│           MENÚ PRINCIPAL             │");
        System.out.println("├──────────────────────────────────────┤");
        System.out.println("│  1. Importació d'articles            │");
        System.out.println("│  2. Gestió d'articles                │");
        System.out.println("│  3. Gestió de clients                │");
        System.out.println("│  4. TPV (Terminal Punt de Venda)     │");
        System.out.println("│  5. Consultes vendes per client      │");
        System.out.println("│  6. Consultes vendes per article     │");
        System.out.println("│  7. Calcula els beneficis totals     │");
        System.out.println("│  8. Recompra automàtica d'articles   │");
        System.out.println("│  0. Sortir                           │");
        System.out.println("└──────────────────────────────────────┘");
    }

    // ─── OPCIÓ 1: IMPORTACIÓ D'ARTICLES ──────────────────────────────────────

    private static void importacioArticles() {
        System.out.println("── Importació d'articles des de JSON ──");

        List<Article> articles = jsonReader.llegirArticles("articles.json");

        if (articles.isEmpty()) {
            System.out.println("✘ No s'han pogut llegir articles del fitxer JSON.");
            return;
        }

        long numCamises   = articles.stream().filter(a -> a.getFamilia().equals("camisa")).count();
        long numPantalons = articles.stream().filter(a -> a.getFamilia().equals("pantaló")).count();

        System.out.println("Articles llegits del fitxer:");
        System.out.println("  • Camises:   " + numCamises);
        System.out.println("  • Pantalons: " + numPantalons);
        System.out.println("  • Total:     " + articles.size());

        System.out.print("Vols volcar els articles a la base de dades? (s/n): ");
        String resposta = sc.nextLine().trim().toLowerCase();

        if (!resposta.equals("s")) {
            System.out.println("Importació cancel·lada.");
            return;
        }

        int afegits = 0;
        int actualitzats = 0;

        try {
            Connection conn = Connexio.getConnexio();

            for (Article article : articles) {
                String sqlCheck = "SELECT id FROM articles WHERE id = ?";
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setInt(1, article.getId());
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    actualitzarArticle(conn, article);
                    actualitzats++;
                } else {
                    inserirArticle(conn, article);
                    afegits++;
                }
                rs.close();
                psCheck.close();
            }

            System.out.println("\n✔ Importació completada:");
            System.out.println("  • Articles afegits:      " + afegits);
            System.out.println("  • Articles actualitzats: " + actualitzats);

        } catch (SQLException e) {
            System.err.println("✘ Error accedint a la BD: " + e.getMessage());
        }
    }

    private static void inserirArticle(Connection conn, Article article) throws SQLException {
        PreparedStatement ps;

        if (article instanceof Camisa c) {
            String sql = "INSERT INTO articles (id, nom, familia, preu_base, iva, stock, talla_coll, amplada_pit) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId());
            ps.setString(2, c.getNom());
            ps.setString(3, c.getFamilia());
            ps.setDouble(4, c.getPreuBase());
            ps.setInt(5, c.getIva());
            ps.setInt(6, c.getStock());
            ps.setInt(7, c.getTallaColl());
            ps.setInt(8, c.getAmpladaPit());

        } else if (article instanceof Pantalon p) {
            String sql = "INSERT INTO articles (id, nom, familia, preu_base, iva, stock, talla_cintura, llargada_camal) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getFamilia());
            ps.setDouble(4, p.getPreuBase());
            ps.setInt(5, p.getIva());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getTallaCintura());
            ps.setInt(8, p.getLlargadaCamal());
        } else {
            return;
        }

        ps.executeUpdate();
        ps.close();
    }

    private static void actualitzarArticle(Connection conn, Article article) throws SQLException {
        PreparedStatement ps;

        if (article instanceof Camisa c) {
            String sql = "UPDATE articles SET nom=?, familia=?, preu_base=?, iva=?, stock=?, " +
                    "talla_coll=?, amplada_pit=?, talla_cintura=NULL, llargada_camal=NULL WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getNom());
            ps.setString(2, c.getFamilia());
            ps.setDouble(3, c.getPreuBase());
            ps.setInt(4, c.getIva());
            ps.setInt(5, c.getStock());
            ps.setInt(6, c.getTallaColl());
            ps.setInt(7, c.getAmpladaPit());
            ps.setInt(8, c.getId());

        } else if (article instanceof Pantalon p) {
            String sql = "UPDATE articles SET nom=?, familia=?, preu_base=?, iva=?, stock=?, " +
                    "talla_cintura=?, llargada_camal=?, talla_coll=NULL, amplada_pit=NULL WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getFamilia());
            ps.setDouble(3, p.getPreuBase());
            ps.setInt(4, p.getIva());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getTallaCintura());
            ps.setInt(7, p.getLlargadaCamal());
            ps.setInt(8, p.getId());
        } else {
            return;
        }

        ps.executeUpdate();
        ps.close();
    }

    // ─── OPCIONS PENDENTS (Sprints 2, 3, 4) ──────────────────────────────────

    private static void gestioArticles() {
        System.out.println("[TODO - Sprint 2] Gestió d'articles");
    }

    private static void gestioClients() {
        System.out.println("[TODO - Sprint 2] Gestió de clients");
    }

    private static void tpv() {
        System.out.println("[TODO - Sprint 3] TPV");
    }

    private static void consultaVendesPerClient() {
        System.out.println("[TODO - Sprint 4] Consulta vendes per client");
    }

    private static void consultaVendesPerArticle() {
        System.out.println("[TODO - Sprint 4] Consulta vendes per article");
    }

    private static void calcularBeneficis() {
        System.out.println("[TODO - Sprint 4] Càlcul de beneficis");
    }

    private static void recompraAutomatica() {
        System.out.println("[TODO - Sprint 4] Recompra automàtica");
    }

    // ─── UTILITATS ────────────────────────────────────────────────────────────

    public static int llegirEnter(String missatge) {
        while (true) {
            System.out.print(missatge);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Introdueix un número enter vàlid.");
            }
        }
    }

    public static String llegirText(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }

    public static Scanner getScanner() {
        return sc;
    }
}