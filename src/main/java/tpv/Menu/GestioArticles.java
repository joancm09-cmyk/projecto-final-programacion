package tpv.Menu;

import tpv.dao.ArticleDAO;
import tpv.model.Article;
import tpv.model.Camisa;
import tpv.model.Familia;
import tpv.model.Pantalon;

import java.util.List;
import java.util.Scanner;

/**
 * Submenú de Gestió d'Articles.
 * Alta, baixa, modificació i consulta.
 */
public class GestioArticles {

    private static final ArticleDAO dao = new ArticleDAO();
    private static final Scanner sc = new Scanner(System.in);

    public static void mostrarMenu() {
        int opcio;
        do {
            System.out.println("┌──────────────────────────────────────┐");
            System.out.println("│         GESTIÓ D'ARTICLES            │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Alta d'article                   │");
            System.out.println("│  2. Modificar article                │");
            System.out.println("│  3. Eliminar article                 │");
            System.out.println("│  4. Consultar article per ID         │");
            System.out.println("│  5. Llistar tots els articles        │");
            System.out.println("│  0. Tornar al menú principal         │");
            System.out.println("└──────────────────────────────────────┘");

            opcio = llegirEnter("Selecciona una opció: ");

            switch (opcio) {
                case 1 -> altaArticle();
                case 2 -> modificarArticle();
                case 3 -> eliminarArticle();
                case 4 -> consultarArticle();
                case 5 -> llistarArticles();
                case 0 -> System.out.println("Tornant al menú principal...");
                default -> System.out.println("⚠ Opció no vàlida.");
            }

        } while (opcio != 0);
    }

    // ─── ALTA ─────────────────────────────────────────────────────────────────

    private static void altaArticle() {
        System.out.println("\n── Alta d'article ──");

        int id = llegirEnter("ID de l'article: ");
        if (dao.existeix(id)) {
            System.out.println("⚠ Ja existeix un article amb l'ID " + id + ".");
            return;
        }

        String nom = llegirText("Nom de l'article: ");

        // Triar família
        System.out.println("Família:");
        for (Familia f : Familia.values()) {
            System.out.println("  " + f);
        }
        Familia familia = null;
        while (familia == null) {
            try {
                familia = Familia.perCodi(llegirEnter("Tria família: "));
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Família no vàlida. Torna a intentar-ho.");
            }
        }

        double preuBase = llegirDouble("Preu base (sense IVA): ");
        int iva = llegirIva();
        int stock = llegirStockPositiu();

        Article article;

        if (familia == Familia.CAMISA) {
            int tallaColl  = llegirEnterRang("Talla coll (36-52): ", 36, 52);
            int ampladaPit = llegirEnterRang("Amplada pit (10-15): ", 10, 15);
            article = new Camisa(id, nom, preuBase, iva, stock, tallaColl, ampladaPit);

        } else {
            int tallaCintura  = llegirEnterRang("Talla cintura (24-56): ", 24, 56);
            int llargadaCamal = llegirEnterRang("Llargada camal (32-46): ", 32, 46);
            article = new Pantalon(id, nom, preuBase, iva, stock, tallaCintura, llargadaCamal);
        }

        if (dao.inserir(article)) {
            System.out.println("✔ Article donat d'alta correctament.");
            System.out.println(article);
        } else {
            System.out.println("✘ Error donant d'alta l'article.");
        }
    }

    // ─── MODIFICAR ────────────────────────────────────────────────────────────

    private static void modificarArticle() {
        System.out.println("\n── Modificar article ──");

        int id = llegirEnter("ID de l'article a modificar: ");
        Article article = dao.buscarPerId(id);

        if (article == null) {
            System.out.println("⚠ No existeix cap article amb l'ID " + id + ".");
            return;
        }

        System.out.println("Article actual: " + article);
        System.out.println("(Deixa en blanc per mantenir el valor actual)");

        String nom = llegirTextOpcional("Nou nom [" + article.getNom() + "]: ");
        if (!nom.isEmpty()) article.setNom(nom);

        String preuStr = llegirTextOpcional("Nou preu base [" + article.getPreuBase() + "]: ");
        if (!preuStr.isEmpty()) {
            try {
                article.setPreuBase(Double.parseDouble(preuStr.replace(",", ".")));
            } catch (NumberFormatException e) {
                System.out.println("⚠ Preu no vàlid, es manté l'anterior.");
            }
        }

        String ivaStr = llegirTextOpcional("Nou IVA (4/10/21) [" + article.getIva() + "]: ");
        if (!ivaStr.isEmpty()) {
            try {
                int nouIva = Integer.parseInt(ivaStr);
                if (nouIva == 4 || nouIva == 10 || nouIva == 21) {
                    article.setIva(nouIva);
                } else {
                    System.out.println("⚠ IVA no vàlid, es manté l'anterior.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ IVA no vàlid, es manté l'anterior.");
            }
        }

        String stockStr = llegirTextOpcional("Nou stock [" + article.getStock() + "]: ");
        if (!stockStr.isEmpty()) {
            try {
                int nouStock = Integer.parseInt(stockStr);
                if (nouStock >= 0) {
                    article.setStock(nouStock);
                } else {
                    System.out.println("⚠ El stock no pot ser negatiu, es manté l'anterior.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Stock no vàlid, es manté l'anterior.");
            }
        }

        // Camps específics segons família
        if (article instanceof Camisa c) {
            String collStr = llegirTextOpcional("Nova talla coll (36-52) [" + c.getTallaColl() + "]: ");
            if (!collStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(collStr);
                    if (val >= 36 && val <= 52) c.setTallaColl(val);
                    else System.out.println("⚠ Valor fora de rang, es manté l'anterior.");
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Valor no vàlid, es manté l'anterior.");
                }
            }

            String pitStr = llegirTextOpcional("Nova amplada pit (10-15) [" + c.getAmpladaPit() + "]: ");
            if (!pitStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(pitStr);
                    if (val >= 10 && val <= 15) c.setAmpladaPit(val);
                    else System.out.println("⚠ Valor fora de rang, es manté l'anterior.");
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Valor no vàlid, es manté l'anterior.");
                }
            }

        } else if (article instanceof Pantalon p) {
            String cinturaStr = llegirTextOpcional("Nova talla cintura (24-56) [" + p.getTallaCintura() + "]: ");
            if (!cinturaStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(cinturaStr);
                    if (val >= 24 && val <= 56) p.setTallaCintura(val);
                    else System.out.println("⚠ Valor fora de rang, es manté l'anterior.");
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Valor no vàlid, es manté l'anterior.");
                }
            }

            String camalStr = llegirTextOpcional("Nova llargada camal (32-46) [" + p.getLlargadaCamal() + "]: ");
            if (!camalStr.isEmpty()) {
                try {
                    int val = Integer.parseInt(camalStr);
                    if (val >= 32 && val <= 46) p.setLlargadaCamal(val);
                    else System.out.println("⚠ Valor fora de rang, es manté l'anterior.");
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Valor no vàlid, es manté l'anterior.");
                }
            }
        }

        if (dao.actualitzar(article)) {
            System.out.println("✔ Article modificat correctament.");
            System.out.println(article);
        } else {
            System.out.println("✘ Error modificant l'article.");
        }
    }

    // ─── ELIMINAR ─────────────────────────────────────────────────────────────

    private static void eliminarArticle() {
        System.out.println("\n── Eliminar article ──");

        int id = llegirEnter("ID de l'article a eliminar: ");
        Article article = dao.buscarPerId(id);

        if (article == null) {
            System.out.println("⚠ No existeix cap article amb l'ID " + id + ".");
            return;
        }

        System.out.println("Article trobat: " + article);
        System.out.print("Estàs segur que vols eliminar-lo? (s/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Eliminació cancel·lada.");
            return;
        }

        if (dao.esborrar(id)) {
            System.out.println("✔ Article eliminat correctament.");
        } else {
            System.out.println("✘ Error eliminant l'article.");
        }
    }

    // ─── CONSULTAR ────────────────────────────────────────────────────────────

    private static void consultarArticle() {
        System.out.println("\n── Consultar article ──");

        int id = llegirEnter("ID de l'article: ");
        Article article = dao.buscarPerId(id);

        if (article == null) {
            System.out.println("⚠ No existeix cap article amb l'ID " + id + ".");
        } else {
            System.out.println("\n" + article);
            System.out.printf("  Preu final (amb IVA): %.2f€%n", article.calcularPreuFinal());
            System.out.printf("  Preu de cost:         %.2f€%n", article.calcularPreuCost());
        }
    }

    // ─── LLISTAR ──────────────────────────────────────────────────────────────

    private static void llistarArticles() {
        System.out.println("\n── Llistat d'articles ──");

        List<Article> articles = dao.llistarTots();

        if (articles.isEmpty()) {
            System.out.println("No hi ha articles a la base de dades.");
            return;
        }

        long camises   = articles.stream().filter(a -> a instanceof Camisa).count();
        long pantalons = articles.stream().filter(a -> a instanceof Pantalon).count();

        System.out.println("Total: " + articles.size() +
                " (" + camises + " camises, " + pantalons + " pantalons)\n");

        for (Article a : articles) {
            System.out.println(a);
        }
    }

    // ─── VALIDACIONS ──────────────────────────────────────────────────────────

    private static int llegirEnter(String missatge) {
        while (true) {
            System.out.print(missatge);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Introdueix un número enter vàlid.");
            }
        }
    }

    private static int llegirEnterRang(String missatge, int min, int max) {
        while (true) {
            int valor = llegirEnter(missatge);
            if (valor >= min && valor <= max) return valor;
            System.out.println("⚠ El valor ha d'estar entre " + min + " i " + max + ".");
        }
    }

    private static double llegirDouble(String missatge) {
        while (true) {
            System.out.print(missatge);
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("⚠ Introdueix un número decimal vàlid.");
            }
        }
    }

    private static int llegirIva() {
        while (true) {
            int iva = llegirEnter("IVA (4, 10 o 21): ");
            if (iva == 4 || iva == 10 || iva == 21) return iva;
            System.out.println("⚠ L'IVA només pot ser 4, 10 o 21.");
        }
    }

    private static int llegirStockPositiu() {
        while (true) {
            int stock = llegirEnter("Stock inicial: ");
            if (stock >= 0) return stock;
            System.out.println("⚠ El stock no pot ser negatiu.");
        }
    }

    private static String llegirText(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }

    private static String llegirTextOpcional(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }
}