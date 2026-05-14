package tpv.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de connexió a la base de dades MySQL.
 * Singleton: retorna sempre la mateixa connexió.
 */
public class Connexio {

    private static final String URL      = "jdbc:mysql://localhost:3306/tpv_botiga";
    private static final String USUARI   = "root";
    private static final String PASSWORD = "Joanypau.1";

    private static Connection connexio = null;

    private Connexio() {}

    public static Connection getConnexio() throws SQLException {
        if (connexio == null || connexio.isClosed()) {
            try {
                connexio = DriverManager.getConnection(URL, USUARI, PASSWORD);
                System.out.println("✔ Connexió a la base de dades establerta.");
            } catch (SQLException e) {
                System.err.println("✘ Error en connectar a la BD: " + e.getMessage());
                throw e;
            }
        }
        return connexio;
    }

    public static void tancarConnexio() {
        if (connexio != null) {
            try {
                connexio.close();
                System.out.println("✔ Connexió tancada.");
            } catch (SQLException e) {
                System.err.println("✘ Error en tancar la connexió: " + e.getMessage());
            }
        }
    }
}