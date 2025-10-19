package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * Point d'entrée de l'application DNS en ligne de commande.
 * - Charge la configuration (db.path) si disponible.
 * - Instancie le service Dns.
 * - Boucle IHM : nextCommande -> execute -> affiche, jusqu'à Quit.
 */
public class DnsApp {

  /** Lance la boucle applicative. */
  public void run() {
    //  Charger la config (facultatif : src/main/resources/config.properties)
    Properties props = loadProperties();

    //  Résoudre le chemin de la base (par défaut vers src/main/resources/data/dns.txt)
    String dbPathStr = props.getProperty("db.path", "src/main/resources/data/dns.txt");
    Path dbPath = Path.of(dbPathStr);

    // Créer le service et l'IHM
    Dns dns = new Dns(dbPath);
    DnsTUI ui = new DnsTUI();

    
    while (true) {
      Commande cmd = ui.nextCommande();
      String out = cmd.execute(dns);
      ui.affiche(out);
      if (cmd.shouldQuit()) {
        break;
      }
    }
  }

  /**
   * Charge un fichier config.properties présent sur le classpath (optionnel).
   * Clés possibles :
   *   - db.path = chemin vers la base texte (ex: data/dns.txt)
   */
  private Properties loadProperties() {
    Properties p = new Properties();
    try (InputStream is = DnsApp.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (is != null) {
        p.load(is);
      }
    } catch (IOException ignored) {
      
    }
    return p;
  }

  /** Méthode main : lance l'application. */
  public static void main(String[] args) {
    new DnsApp().run();
  }
}

