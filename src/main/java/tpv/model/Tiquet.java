package tpv.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Tiquet. Representa una venda completa.
 * Conté una llista de LiniaFactura.
 */
public class Tiquet {

    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private LocalDate dataCompra;
    private String dniClient;
    private double totalBase;
    private double totalIva;
    private double totalFinal;

    // Línies de factura associades (no es guarden directament a la taula tiquets)
    private List<LiniaFactura> linies;

    public Tiquet(int id, String dniClient) {
        this.id = id;
        this.dniClient = dniClient;
        this.dataCompra = LocalDate.now();
        this.linies = new ArrayList<>();
        this.totalBase = 0;
        this.totalIva = 0;
        this.totalFinal = 0;
    }

    public Tiquet() {
        this.linies = new ArrayList<>();
    }

    // Afegeix una línia i recalcula totals
    public void afegirLinia(LiniaFactura linia) {
        linies.add(linia);
        recalcularTotals();
    }

    // Recalcula totalBase, totalIva i totalFinal a partir de les línies
    public void recalcularTotals() {
        totalBase = 0;
        totalIva = 0;
        totalFinal = 0;
        for (LiniaFactura linia : linies) {
            totalBase += linia.getPreuBase();
            totalIva += (linia.getPreuFinal() - linia.getPreuBase());
            totalFinal += linia.getPreuFinal();
        }
    }

    // Getters i Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }
    public String getDataCompraFormatada() { return dataCompra.format(FORMAT_DATA); }

    public String getDniClient() { return dniClient; }
    public void setDniClient(String dniClient) { this.dniClient = dniClient; }

    public double getTotalBase() { return totalBase; }
    public void setTotalBase(double totalBase) { this.totalBase = totalBase; }

    public double getTotalIva() { return totalIva; }
    public void setTotalIva(double totalIva) { this.totalIva = totalIva; }

    public double getTotalFinal() { return totalFinal; }
    public void setTotalFinal(double totalFinal) { this.totalFinal = totalFinal; }

    public List<LiniaFactura> getLinies() { return linies; }
    public void setLinies(List<LiniaFactura> linies) { this.linies = linies; }

    @Override
    public String toString() {
        return String.format("Tiquet #%d | Data: %s | Client: %s | Total: %.2f€",
                id, getDataCompraFormatada(), dniClient, totalFinal);
    }
}
