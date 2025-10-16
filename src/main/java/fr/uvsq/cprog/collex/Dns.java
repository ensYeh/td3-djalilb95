package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Dns {
  private final Path dbPath;

  
  private final Map<String, DnsItem> byName = new HashMap<>();
  private final Map<String, DnsItem> byIp = new HashMap<>();

  /**
   * Construit le service et charge la base de données texte.
   *
   * @param dbPath chemin du fichier texte (ex: Path.of("data/dns.txt"))
   * @throws RuntimeException si une erreur d'E/S survient ou si une ligne est invalide
   */
  public Dns(final Path dbPath) {
    this.dbPath = dbPath;
    load(); // charge immédiatement la BD
  }

  /** Charge le fichier texte dans les index mémoire. */
  private void load() {
    byName.clear();
    byIp.clear();

    if (dbPath == null || !Files.exists(dbPath)) {
      // Pas de fichier → base vide (pas d'exception).
      return;
    }

    try {
      for (String raw : Files.readAllLines(dbPath)) {
        final String line = raw.strip();
        if (line.isEmpty() || line.startsWith("#")) {
          continue; 
        }

        final String[] parts = line.split("\\s+");
        if (parts.length != 2) {
          throw new RuntimeException("Ligne invalide dans la BD : '" + line + "'");
        }

        
        final NomMachine nom = new NomMachine(parts[0].toLowerCase(Locale.ROOT));
        final AdresseIP ip = new AdresseIP(parts[1]);

        
        if (byName.containsKey(nom.value())) {
          throw new RuntimeException("Doublon nom : " + nom.value());
        }
        if (byIp.containsKey(ip.value())) {
          throw new RuntimeException("Doublon IP : " + ip.value());
        }

        final DnsItem item = new DnsItem(nom, ip);
        byName.put(nom.value(), item);
        byIp.put(ip.value(), item);
      }
    } catch (IOException e) {
      throw new RuntimeException("Erreur de lecture du fichier " + dbPath, e);
    }
  }

  

  /** Nombre d’entrées chargées. */
  public int size() {
    return byName.size();
  }

  /** true si un nom (FQDN) est présent. */
  public boolean containsName(final String fqdn) {
    return byName.containsKey(fqdn.toLowerCase(Locale.ROOT));
  }

  /** true si une IP est présente. */
  public boolean containsIp(final String ip) {
    return byIp.containsKey(ip);
  }

  
  public Path getDbPath() {
    return dbPath;
  }
}

