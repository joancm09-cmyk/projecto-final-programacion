package tpv.Menu;

import tpv.dao.ArticleDAO;
import tpv.dao.ClientDAO;
import tpv.dao.TiquetDAO;
import tpv.model.*;

import java.util.Scanner;

/**
 * Terminal Punt de Venda (TPV).
 * Registra vendes, genera tiquets i actualitza estoc.
 */
public class TPV {

    private static final ArticleDAO articleDAO = new ArticleDAO();
    private static final ClientDAO clientDAO   = new ClientDAO();
    private static final TiquetDAO tiquetDAO   = new TiquetDAO();
    private static final Scanner sc            = new Scanner(System.in);

    public static void iniciarVenda() {
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│       TERMINAL PUNT DE VENDA         │");
        System.out.println("└──────────────────────────────────────┘");

        // 1. Identificar client
        Client client = identificarClient();
        if (client == null) return;

        System.out.println("✔ Client: " + client.getNom() + " (" + client.getDni() + ")");

        // 2. Crear tiquet buit
        Tiquet tiquet = new Tiquet(0, client.getDni());

        // 3. Afegir articles de forma iterativa
        System.out.println("\nAfegeix articles (introdueix 0 per finalitzar):");

        while (true) {
            int idArticle = llegirEnter("ID article (0 per acabar): ");
            if (idArticle == 0) break;

            Article article = articleDAO.buscarPerId(idArticle);
            if (article == null) {
                System.out.println("⚠ No existeix cap article amb l'ID " + idArticle + ".");
                continue;
            }

            if (article.getStock() <= 0) {
                System.out.println("⚠ L'article '" + article.getNom() + "' no té stock disponible!");
                continue;
            }

            System.out.println("  Article: " + article.getNom() +
                    " | Preu: " + String.format("%.2f", article.calcularPreuFinal()) + "€" +
                    " | Stock: " + article.getStock());

            int quantitat = llegirQuantitat(article.getStock());

            LiniaFactura linia = new LiniaFactura(0, idArticle, quantitat,
                    article.getPreuBase(), article.getIva());
            tiquet.afegirLinia(linia);

            System.out.printf("  ✔ Afegit: %s x%d = %.2f€%n",
                    article.getNom(), quantitat, linia.getPreuFinal());
        }

        if (tiquet.getLinies().isEmpty()) {
            System.out.println("⚠ No s'ha afegit cap article. Venda cancel·lada.");
            return;
        }

        // 4. Mostrar resum i confirmar
        mostrarResumVenda(tiquet);
        System.out.print("\nConfirmes la venda? (s/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Venda cancel·lada.");
            return;
        }

        // 5. Guardar tiquet a la BD
        int idTiquet = tiquetDAO.inserirTiquet(tiquet);
        if (idTiquet == -1) {
            System.out.println("✘ Error guardant el tiquet.");
            return;
        }
        tiquet.setId(idTiquet);

        // 6. Guardar línies i actualitzar stock
        for (LiniaFactura linia : tiquet.getLinies()) {
            linia.setIdTiquet(idTiquet);
            tiquetDAO.inserirLinia(linia);

            Article article = articleDAO.buscarPerId(linia.getIdArticle());
            int nouStock = article.getStock() - linia.getQuantitat();
            articleDAO.actualitzarStock(linia.getIdArticle(), nouStock);
        }

        // 7. Imprimir tiquet
        imprimirTiquet(tiquet, client);
    }

    // ─── IDENTIFICAR CLIENT ───────────────────────────────────────────────────

    private static Client identificarClient() {
        System.out.print("DNI/NIF del client (o '000' per client genèric): ");
        String dni = sc.nextLine().trim().toUpperCase();

        Client client = clientDAO.buscarPerDni(dni);

        if (client != null) return client;

        if (dni.equals("000")) {
            // Assegura que el client genèric existeix
            Client generic = new Client("000", "Client Genèric", "-", "-");
            clientDAO.inserir(generic);
            return clientDAO.buscarPerDni("000");
        }

        System.out.println("⚠ Client no trobat. Pots usar '000' per client genèric.");
        System.out.print("Vols usar el client genèric? (s/n): ");
        String resp = sc.nextLine().trim().toLowerCase();

        if (resp.equals("s")) {
            Client generic = clientDAO.buscarPerDni("000");
            if (generic == null) {
                generic = new Client("000", "Client Genèric", "-", "-");
                clientDAO.inserir(generic);
                generic = clientDAO.buscarPerDni("000");
            }
            return generic;
        }

        return null;
    }

    // ─── MOSTRAR RESUM ────────────────────────────────────────────────────────

    private static void mostrarResumVenda(Tiquet tiquet) {
        System.out.println("── Resum de la venda ──");
        for (LiniaFactura linia : tiquet.getLinies()) {
            Article article = articleDAO.buscarPerId(linia.getIdArticle());
            String nom = article != null ? article.getNom() : "Article #" + linia.getIdArticle();
            System.out.printf("  %-30s x%d  %.2f€%n",
                    nom, linia.getQuantitat(), linia.getPreuFinal());
        }
        System.out.println("  ──────────────────────────────────────");
        System.out.printf("  Base imposable:  %.2f€%n", tiquet.getTotalBase());
        System.out.printf("  IVA:             %.2f€%n", tiquet.getTotalIva());
        System.out.printf("  TOTAL:           %.2f€%n", tiquet.getTotalFinal());
    }

    // ─── IMPRIMIR TIQUET ──────────────────────────────────────────────────────

    private static void imprimirTiquet(Tiquet tiquet, Client client) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║          BOTIGA DE ROBA              ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Tiquet #%-5d   Data: %-12s  ║%n",
                tiquet.getId(), tiquet.getDataCompraFormatada());
        System.out.printf( "║  Client: %-28s  ║%n", client.getNom());
        System.out.println("╠══════════════════════════════════════╣");

        for (LiniaFactura linia : tiquet.getLinies()) {
            Article article = articleDAO.buscarPerId(linia.getIdArticle());
            String nom = article != null ? article.getNom() : "Article #" + linia.getIdArticle();
            if (nom.length() > 22) nom = nom.substring(0, 22);
            System.out.printf("║  %-22s x%-2d  %6.2f€  ║%n",
                    nom, linia.getQuantitat(), linia.getPreuFinal());
        }

        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  Base:  %28.2f€  ║%n", tiquet.getTotalBase());
        System.out.printf( "║  IVA:   %28.2f€  ║%n", tiquet.getTotalIva());
        System.out.printf( "║  TOTAL: %28.2f€  ║%n", tiquet.getTotalFinal());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║       Gràcies per la seva compra!    ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ─── UTILITATS ────────────────────────────────────────────────────────────

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

    private static int llegirQuantitat(int stockDisponible) {
        while (true) {
            int q = llegirEnter("Quantitat (màx " + stockDisponible + "): ");
            if (q <= 0) {
                System.out.println("⚠ La quantitat ha de ser major que 0.");
            } else if (q > stockDisponible) {
                System.out.println("⚠ No hi ha prou stock. Disponible: " + stockDisponible);
            } else {
                return q;
            }
        }
    }
}