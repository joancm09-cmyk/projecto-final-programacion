package tpv.dao;

import tpv.db.Connexio;
import tpv.model.Article;
import tpv.model.Camisa;
import tpv.model.Pantalon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per gestionar els Articles a la base de dades.
 * Separa tota la lògica d'accés a dades de la lògica principal.
 */
public class ArticleDAO {

    // ─── INSERIR ──────────────────────────────────────────────────────────────

    public boolean inserir(Article article) {
        String sql;
        try {
            Connection conn = Connexio.getConnexio();
            PreparedStatement ps;

            if (article instanceof Camisa c) {
                sql = "INSERT INTO articles (id, nom, familia, preu_base, iva, stock, talla_coll, amplada_pit) " +
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
                sql = "INSERT INTO articles (id, nom, familia, preu_base, iva, stock, talla_cintura, llargada_camal) " +
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
                return false;
            }

            ps.executeUpdate();
            ps.close();
            return true;

        } catch (SQLException e) {
            System.err.println("✘ Error inserint article: " + e.getMessage());
            return false;
        }
    }

    // ─── ACTUALITZAR ──────────────────────────────────────────────────────────

    public boolean actualitzar(Article article) {
        try {
            Connection conn = Connexio.getConnexio();
            PreparedStatement ps;
            String sql;

            if (article instanceof Camisa c) {
                sql = "UPDATE articles SET nom=?, familia=?, preu_base=?, iva=?, stock=?, " +
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
                sql = "UPDATE articles SET nom=?, familia=?, preu_base=?, iva=?, stock=?, " +
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
                return false;
            }

            int files = ps.executeUpdate();
            ps.close();
            return files > 0;

        } catch (SQLException e) {
            System.err.println("✘ Error actualitzant article: " + e.getMessage());
            return false;
        }
    }

    // ─── ESBORRAR ─────────────────────────────────────────────────────────────

    public boolean esborrar(int id) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "DELETE FROM articles WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int files = ps.executeUpdate();
            ps.close();
            return files > 0;

        } catch (SQLException e) {
            System.err.println("✘ Error esborrant article: " + e.getMessage());
            return false;
        }
    }

    // ─── BUSCAR PER ID ────────────────────────────────────────────────────────

    public Article buscarPerId(int id) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM articles WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Article article = null;
            if (rs.next()) {
                article = construirArticle(rs);
            }

            rs.close();
            ps.close();
            return article;

        } catch (SQLException e) {
            System.err.println("✘ Error buscant article: " + e.getMessage());
            return null;
        }
    }

    // ─── LLISTAR TOTS ─────────────────────────────────────────────────────────

    public List<Article> llistarTots() {
        List<Article> llista = new ArrayList<>();
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM articles ORDER BY id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Article article = construirArticle(rs);
                if (article != null) llista.add(article);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("✘ Error llistant articles: " + e.getMessage());
        }
        return llista;
    }

    // ─── EXISTEIX? ────────────────────────────────────────────────────────────

    public boolean existeix(int id) {
        return buscarPerId(id) != null;
    }

    // ─── ACTUALITZAR STOCK ────────────────────────────────────────────────────

    public boolean actualitzarStock(int id, int nouStock) {
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "UPDATE articles SET stock = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, nouStock);
            ps.setInt(2, id);
            int files = ps.executeUpdate();
            ps.close();
            return files > 0;

        } catch (SQLException e) {
            System.err.println("✘ Error actualitzant stock: " + e.getMessage());
            return false;
        }
    }

    // ─── ARTICLES PER SOTA D'UN STOCK ─────────────────────────────────────────

    public List<Article> llistarPerSotaStock(int llindar) {
        List<Article> llista = new ArrayList<>();
        try {
            Connection conn = Connexio.getConnexio();
            String sql = "SELECT * FROM articles WHERE stock < ? ORDER BY stock ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, llindar);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Article article = construirArticle(rs);
                if (article != null) llista.add(article);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            System.err.println("✘ Error llistant articles per stock: " + e.getMessage());
        }
        return llista;
    }

    // ─── MÈTODE AUXILIAR: construir Article des de ResultSet ─────────────────

    private Article construirArticle(ResultSet rs) throws SQLException {
        String familia = rs.getString("familia").toLowerCase();

        if (familia.equals("camisa")) {
            return new Camisa(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("preu_base"),
                    rs.getInt("iva"),
                    rs.getInt("stock"),
                    rs.getInt("talla_coll"),
                    rs.getInt("amplada_pit")
            );
        } else if (familia.equals("pantaló") || familia.equals("pantalo")) {
            return new Pantalon(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("preu_base"),
                    rs.getInt("iva"),
                    rs.getInt("stock"),
                    rs.getInt("talla_cintura"),
                    rs.getInt("llargada_camal")
            );
        }
        return null;
    }
}