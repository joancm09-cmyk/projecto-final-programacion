package tpv.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tpv.model.Article;
import tpv.model.Camisa;
import tpv.model.Pantalon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de gestió de dades JSON amb json-simple.
 * Separada de la lògica principal (requisit del pràctica).
 */
public class GestorJson {

    /**
     * Llegeix el fitxer articles.json des de resources i retorna una llista d'Articles.
     */
    public List<Article> llegirArticles(String rutaFitxer) {
        List<Article> articles = new ArrayList<>();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(rutaFitxer);

            if (is == null) {
                System.err.println("✘ No s'ha trobat el fitxer: " + rutaFitxer);
                return articles;
            }

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(
                    new InputStreamReader(is, StandardCharsets.UTF_8));

            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;

                int id          = (int) (long) json.get("id");
                String nom      = (String) json.get("nom");
                String familia  = ((String) json.get("familia")).toLowerCase();
                double preuBase = (double) json.get("preu_base");
                int iva         = (int) (long) json.get("iva");
                int stock       = (int) (long) json.get("stock");

                if (familia.equals("camisa")) {
                    int tallaColl  = (int) (long) json.get("talla_coll");
                    int ampladaPit = (int) (long) json.get("amplada_pit");

                    Camisa camisa = new Camisa();
                    camisa.setId(id);
                    camisa.setNom(nom);
                    camisa.setFamilia("camisa");
                    camisa.setPreuBase(preuBase);
                    camisa.setIva(iva);
                    camisa.setStock(stock);
                    camisa.setTallaColl(tallaColl);
                    camisa.setAmpladaPit(ampladaPit);
                    articles.add(camisa);

                } else if (familia.equals("pantaló") || familia.equals("pantalo")) {
                    int tallaCintura  = (int) (long) json.get("talla_cintura");
                    int llargadaCamal = (int) (long) json.get("llargada_camal");

                    Pantalon pantalon = new Pantalon();
                    pantalon.setId(id);
                    pantalon.setNom(nom);
                    pantalon.setFamilia("pantaló");
                    pantalon.setPreuBase(preuBase);
                    pantalon.setIva(iva);
                    pantalon.setStock(stock);
                    pantalon.setTallaCintura(tallaCintura);
                    pantalon.setLlargadaCamal(llargadaCamal);
                    articles.add(pantalon);

                } else {
                    System.err.println("⚠ Família desconeguda: " + familia +
                            " (id=" + id + "). S'omet.");
                }
            }

        } catch (ParseException e) {
            System.err.println("✘ Error parsejant el JSON: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("✘ Error llegint el fitxer: " + e.getMessage());
        }

        return articles;
    }

    /**
     * Escriu una llista d'articles en format JSON a un fitxer.
     * Útil per generar el JSON de recompra (opció 8).
     */
    @SuppressWarnings("unchecked")
    public void escriureArticles(List<Article> articles, String rutaFitxer) {
        JSONArray array = new JSONArray();

        for (Article article : articles) {
            JSONObject obj = new JSONObject();
            obj.put("id", article.getId());
            obj.put("nom", article.getNom());
            obj.put("familia", article.getFamilia());
            obj.put("preu_base", article.getPreuBase());
            obj.put("iva", article.getIva());
            obj.put("stock", article.getStock());

            if (article instanceof Camisa c) {
                obj.put("talla_coll", c.getTallaColl());
                obj.put("amplada_pit", c.getAmpladaPit());
            } else if (article instanceof Pantalon p) {
                obj.put("talla_cintura", p.getTallaCintura());
                obj.put("llargada_camal", p.getLlargadaCamal());
            }

            array.add(obj);
        }

        try (FileWriter writer = new FileWriter(rutaFitxer, StandardCharsets.UTF_8)) {
            writer.write(array.toJSONString());
            System.out.println("✔ Fitxer JSON generat: " + rutaFitxer);
        } catch (IOException e) {
            System.err.println("✘ Error escrivint el JSON: " + e.getMessage());
        }
    }
}