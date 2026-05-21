package tpv.Menu;

import tpv.dao.ClientDAO;
import tpv.model.Client;

import java.util.List;
import java.util.Scanner;

/**
 * Submenú de Gestió de Clients.
 * Alta, baixa, modificació i consulta.
 */
public class GestioClients {

    private static final ClientDAO dao = new ClientDAO();
    private static final Scanner sc = new Scanner(System.in);

    public static void mostrarMenu() {
        int opcio;
        do {
            System.out.println("┌──────────────────────────────────────┐");
            System.out.println("│          GESTIÓ DE CLIENTS           │");
            System.out.println("├──────────────────────────────────────┤");
            System.out.println("│  1. Alta de client                   │");
            System.out.println("│  2. Modificar client                 │");
            System.out.println("│  3. Eliminar client                  │");
            System.out.println("│  4. Consultar client per DNI         │");
            System.out.println("│  5. Llistar tots els clients         │");
            System.out.println("│  0. Tornar al menú principal         │");
            System.out.println("└──────────────────────────────────────┘");

            opcio = llegirEnter("Selecciona una opció: ");

            switch (opcio) {
                case 1 -> altaClient();
                case 2 -> modificarClient();
                case 3 -> eliminarClient();
                case 4 -> consultarClient();
                case 5 -> llistarClients();
                case 0 -> System.out.println("Tornant al menú principal...");
                default -> System.out.println("⚠ Opció no vàlida.");
            }

        } while (opcio != 0);
    }

    // ─── ALTA ─────────────────────────────────────────────────────────────────

    private static void altaClient() {
        System.out.println("── Alta de client ──");

        String dni = llegirText("DNI/NIF del client: ").toUpperCase();

        if (dni.equals("000")) {
            System.out.println("⚠ El codi '000' és reservat per al client genèric.");
            return;
        }

        if (!validarDni(dni)) {
            System.out.println("⚠ Format de DNI no vàlid. Ha de tenir 8 digits i 1 lletra (ex: 12345678A).");
            return;
        }

        if (dao.existeix(dni)) {
            System.out.println("⚠ Ja existeix un client amb el DNI " + dni + ".");
            return;
        }

        String nom = llegirText("Nom del client o empresa: ");
        if (nom.isEmpty()) {
            System.out.println("⚠ El nom no pot estar buit.");
            return;
        }

        String email = llegirEmail();
        String telefon = llegirTelefon();

        Client client = new Client(dni, nom, email, telefon);

        if (dao.inserir(client)) {
            System.out.println("✔ Client donat d'alta correctament.");
            System.out.println(client);
        } else {
            System.out.println("✘ Error donant d'alta el client.");
        }
    }

    // ─── MODIFICAR ────────────────────────────────────────────────────────────

    private static void modificarClient() {
        System.out.println("\n── Modificar client ──");

        String dni = llegirText("DNI del client a modificar: ").toUpperCase();
        Client client = dao.buscarPerDni(dni);

        if (client == null) {
            System.out.println("⚠ No existeix cap client amb el DNI " + dni + ".");
            return;
        }

        System.out.println("Client actual: " + client);
        System.out.println("(Deixa en blanc per mantenir el valor actual)");

        String nom = llegirTextOpcional("Nou nom [" + client.getNom() + "]: ");
        if (!nom.isEmpty()) client.setNom(nom);

        String email = llegirTextOpcional("Nou email [" + client.getEmail() + "]: ");
        if (!email.isEmpty()) {
            if (email.contains("@")) {
                client.setEmail(email);
            } else {
                System.out.println("⚠ Email no vàlid, es manté l'anterior.");
            }
        }

        String telefon = llegirTextOpcional("Nou telèfon [" + client.getTelefon() + "]: ");
        if (!telefon.isEmpty()) client.setTelefon(telefon);

        if (dao.actualitzar(client)) {
            System.out.println("✔ Client modificat correctament.");
            System.out.println(client);
        } else {
            System.out.println("✘ Error modificant el client.");
        }
    }

    // ─── ELIMINAR ─────────────────────────────────────────────────────────────

    private static void eliminarClient() {
        System.out.println("\n── Eliminar client ──");

        String dni = llegirText("DNI del client a eliminar: ").toUpperCase();

        if (dni.equals("000")) {
            System.out.println("⚠ No es pot eliminar el client genèric (000).");
            return;
        }

        Client client = dao.buscarPerDni(dni);

        if (client == null) {
            System.out.println("⚠ No existeix cap client amb el DNI " + dni + ".");
            return;
        }

        System.out.println("Client trobat: " + client);
        System.out.print("Estàs segur que vols eliminar-lo? (s/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Eliminació cancel·lada.");
            return;
        }

        if (dao.esborrar(dni)) {
            System.out.println("✔ Client eliminat correctament.");
        } else {
            System.out.println("✘ Error eliminant el client.");
        }
    }

    // ─── CONSULTAR ────────────────────────────────────────────────────────────

    private static void consultarClient() {
        System.out.println("\n── Consultar client ──");

        String dni = llegirText("DNI del client: ").toUpperCase();
        Client client = dao.buscarPerDni(dni);

        if (client == null) {
            System.out.println("⚠ No existeix cap client amb el DNI " + dni + ".");
        } else {
            System.out.println("\n" + client);
        }
    }

    // ─── LLISTAR ──────────────────────────────────────────────────────────────

    private static void llistarClients() {
        System.out.println("\n── Llistat de clients ──");

        List<Client> clients = dao.llistarTots();

        if (clients.isEmpty()) {
            System.out.println("No hi ha clients a la base de dades.");
            return;
        }

        System.out.println("Total: " + clients.size() + " clients\n");
        for (Client c : clients) {
            System.out.println(c);
        }
    }

    // ─── VALIDACIONS ──────────────────────────────────────────────────────────

    private static boolean validarDni(String dni) {
        // Format: 8 digits + 1 lletra (ex: 12345678A) o codi empresa (ex: B12345678)
        return dni.matches("\\d{8}[A-Z]") || dni.matches("[A-Z]\\d{7}[A-Z0-9]");
    }

    private static String llegirEmail() {
        while (true) {
            String email = llegirText("Email: ");
            if (email.contains("@") && email.contains(".")) return email;
            System.out.println("⚠ Email no vàlid. Ha de contenir '@' i '.'");
        }
    }

    private static String llegirTelefon() {
        while (true) {
            String tel = llegirText("Telèfon: ");
            if (tel.matches("\\+?[0-9 ]{9,15}")) return tel;
            System.out.println("⚠ Telèfon no vàlid. Ha de tenir entre 9 i 15 dígits.");
        }
    }

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

    private static String llegirText(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }

    private static String llegirTextOpcional(String missatge) {
        System.out.print(missatge);
        return sc.nextLine().trim();
    }
}