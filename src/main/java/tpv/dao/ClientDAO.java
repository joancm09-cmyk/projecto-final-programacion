package tpv.dao;

import tpv.db.Connexio;
import tpv.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per gestionar els Clients a la base de dades.
 */
public class ClientDAO {

    // ─── INSERIR ──────────────────────────────────────────────────────────────

    public boolean inserir(Client client) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "INSERT INTO clients (dni, nom, email, telefon) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getDni());
            ps.setString(2, client.getNom());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getTelefon());
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            System.err.println("✘ Error inserint client: " + e.getMessage());
            return false;
        }
    }

    // ─── ACTUALITZAR ──────────────────────────────────────────────────────────

    public boolean actualitzar(Client client) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "UPDATE clients SET nom=?, email=?, telefon=? WHERE dni=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getNom());
            ps.setString(2, client.getEmail());
            ps.setString(3, client.getTelefon());
            ps.setString(4, client.getDni());
            int files = ps.executeUpdate();
            ps.close();
            return files > 0;

        } catch (SQLException e) {
            System.err.println("✘ Error actualitzant client: " + e.getMessage());
            return false;
        }
    }

    // ─── ESBORRAR ─────────────────────────────────────────────────────────────

    public boolean esborrar(String dni) {
        if (dni.equals("000")) {
            System.out.println("⚠ No es pot eliminar el client genèric (000).");
            return false;
        }
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "DELETE FROM clients WHERE dni = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dni);
            int files = ps.executeUpdate();
            ps.close();
            return files > 0;

        } catch (SQLException e) {
            System.err.println("✘ Error esborrant client: " + e.getMessage());
            return false;
        }
    }

    // ─── BUSCAR PER DNI ───────────────────────────────────────────────────────

    public Client buscarPerDni(String dni) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM clients WHERE dni = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            Client client = null;
            if (rs.next()) {
                client = construirClient(rs);
            }

            rs.close();
            ps.close();
            return client;

        } catch (SQLException e) {
            System.err.println("✘ Error buscant client: " + e.getMessage());
            return null;
        }
    }

    // ─── LLISTAR TOTS ─────────────────────────────────────────────────────────

    public List<Client> llistarTots() {
        List<Client> llista = new ArrayList<>();
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM clients ORDER BY dni";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                llista.add(construirClient(rs));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("✘ Error llistant clients: " + e.getMessage());
        }
        return llista;
    }

    // ─── EXISTEIX ─────────────────────────────────────────────────────────────

    public boolean existeix(String dni) {
        return buscarPerDni(dni) != null;
    }

    // ─── AUXILIAR ─────────────────────────────────────────────────────────────

    private Client construirClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getString("dni"),
                rs.getString("nom"),
                rs.getString("email"),
                rs.getString("telefon")
        );
    }
}