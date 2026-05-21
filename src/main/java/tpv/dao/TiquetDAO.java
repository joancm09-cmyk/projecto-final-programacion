package tpv.dao;

import tpv.db.Connexio;
import tpv.model.LiniaFactura;
import tpv.model.Tiquet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per gestionar Tiquets i Línies de Factura a la base de dades.
 */
public class TiquetDAO {

    // ─── INSERIR TIQUET ───────────────────────────────────────────────────────

    public int inserirTiquet(Tiquet tiquet) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "INSERT INTO tiquets (data_compra, dni_client, total_base, total_iva, total_final) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tiquet.getDataCompraFormatada());
            ps.setString(2, tiquet.getDniClient());
            ps.setDouble(3, tiquet.getTotalBase());
            ps.setDouble(4, tiquet.getTotalIva());
            ps.setDouble(5, tiquet.getTotalFinal());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int idGenerat = -1;
            if (keys.next()) idGenerat = keys.getInt(1);
            keys.close();
            ps.close();
            return idGenerat;

        } catch (SQLException e) {
            System.err.println("✘ Error inserint tiquet: " + e.getMessage());
            return -1;
        }
    }

    // ─── INSERIR LINIA FACTURA ────────────────────────────────────────────────

    public boolean inserirLinia(LiniaFactura linia) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "INSERT INTO linies_factura (id_tiquet, id_article, quantitat, preu_base, iva, preu_final) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, linia.getIdTiquet());
            ps.setInt(2, linia.getIdArticle());
            ps.setInt(3, linia.getQuantitat());
            ps.setDouble(4, linia.getPreuBase());
            ps.setDouble(5, linia.getIva());
            ps.setDouble(6, linia.getPreuFinal());
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            System.err.println("✘ Error inserint línia: " + e.getMessage());
            return false;
        }
    }

    // ─── VENDES PER CLIENT ────────────────────────────────────────────────────

    public List<Tiquet> vendesPerClient(String dni) {
        List<Tiquet> tiquets = new ArrayList<>();
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM tiquets WHERE dni_client = ? ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tiquet t = new Tiquet();
                t.setId(rs.getInt("id"));
                t.setDniClient(rs.getString("dni_client"));
                t.setTotalBase(rs.getDouble("total_base"));
                t.setTotalIva(rs.getDouble("total_iva"));
                t.setTotalFinal(rs.getDouble("total_final"));
                tiquets.add(t);
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("✘ Error consultant vendes per client: " + e.getMessage());
        }
        return tiquets;
    }

    // ─── VENDES PER ARTICLE ───────────────────────────────────────────────────

    public int quantitatVendaPerArticle(int idArticle) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT SUM(quantitat) as total FROM linies_factura WHERE id_article = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idArticle);
            ResultSet rs = ps.executeQuery();
            int total = 0;
            if (rs.next()) total = rs.getInt("total");
            rs.close();
            ps.close();
            return total;

        } catch (SQLException e) {
            System.err.println("✘ Error consultant vendes per article: " + e.getMessage());
            return 0;
        }
    }

    // ─── INGRESSOS PER ARTICLE ────────────────────────────────────────────────

    public double ingresosPerArticle(int idArticle) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT SUM(preu_final) as total FROM linies_factura WHERE id_article = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idArticle);
            ResultSet rs = ps.executeQuery();
            double total = 0;
            if (rs.next()) total = rs.getDouble("total");
            rs.close();
            ps.close();
            return total;

        } catch (SQLException e) {
            System.err.println("✘ Error consultant ingressos per article: " + e.getMessage());
            return 0;
        }
    }
}