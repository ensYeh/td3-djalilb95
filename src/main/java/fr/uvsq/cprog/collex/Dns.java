package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Dns {

  /** Chemin vers le fichier texte de la base DNS. */
  private final Path dbPath;

  /** Index des entrées par nom de machine. */
  private final Map<String, DnsItem> byName = new HashMap<>();

  /** Index des entrées par adresse IP. */
  private final Map<String, DnsItem> byIp = new HashMap<>();

  /**
   * Construit le service DNS et charge la base de données texte.
   *
   * @param dbPath chemin du fichier texte (ex : Path.of("data/dns.txt"))
   * @throws RuntimeException si une erreur d'E/S survient ou si une ligne est invalide
   */
  public Dns(final Path dbPath) {
    this.dbPath = dbPath;
    load(); // charge immédiatement la base de données
  }

  
  private void load() {
    byName.clear();
    byIp.clear();

    if (dbPath == null || !Files.exists(dbPath)) {
      
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
          throw new RuntimeException("Ligne invalide dans la base DNS : '" + line + "'");
        }

        final NomMachine nom = new NomMachine(parts[0].toLowerCase(Locale.ROOT));
        final AdresseIP ip = new AdresseIP(parts[1]);

        
        if (byName.containsKey(nom.value())) {
          throw new RuntimeException("Doublon de nom détecté : " + nom.value());
        }
        if (byIp.containsKey(ip.value())) {
          throw new RuntimeException("Doublon d'adresse IP détecté : " + ip.value());
        }

        final DnsItem item = new DnsItem(nom, ip);
        byName.put(nom.value(), item);
        byIp.put(ip.value(), item);
      }
    } catch (IOException e) {
      throw new RuntimeException("Erreur de lecture du fichier " + dbPath, e);
    }
  }

  /**
   * Recherche une entrée DNS à partir d'un nom de machine.
   *
   * @param nom le nom de machine recherché (non null)
   * @return l'objet {@link DnsItem} correspondant, ou null si non trouvé
   */
  public DnsItem getItem(final NomMachine nom) {
    if (nom == null) {
      throw new IllegalArgumentException("Nom de machine nul");
    }
    return byName.get(nom.value());
  }

  /**
   * Recherche une entrée DNS à partir d'une adresse IP.
   *
   * @param ip l'adresse IP recherchée (non null)
   * @return l'objet {@link DnsItem} correspondant, ou null si non trouvé
   */
  public DnsItem getItem(final AdresseIP ip) {
    if (ip == null) {
      throw new IllegalArgumentException("Adresse IP nulle");
    }
    return byIp.get(ip.value());
  }

  /**
   * Retourne le nombre total d’entrées chargées.
   *
   * @return nombre d’entrées
   */
  public int size() {
    return byName.size();
  }

  /**
   * Vérifie si un nom de machine est présent dans la base.
   *
   * @param fqdn nom qualifié à vérifier
   * @return true si présent, false sinon
   */
  public boolean containsName(final String fqdn) {
    return byName.containsKey(fqdn.toLowerCase(Locale.ROOT));
  }

  /**
   * Vérifie si une adresse IP est présente dans la base.
   *
   * @param ip adresse IP à vérifier
   * @return true si présente, false sinon
   */
  public boolean containsIp(final String ip) {
    return byIp.containsKey(ip);
  }

  /**
   * Retourne le chemin du fichier de base DNS.
   *
   * @return chemin du fichier de base
   */
  public Path getDbPath() {
    return dbPath;
  }
}

