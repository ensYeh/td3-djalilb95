package fr.uvsq.cprog.collex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;



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
    load(); 
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
    * Retourne la liste des entrées DNS appartenant à un domaine donné.
    *
    * @param domaine          le domaine recherché (ex: "uvsq.fr")
    * @param trierParAdresse  true pour trier par IP, false pour trier par nom
    * @return liste triée des entrées correspondant au domaine
    */
    public List<DnsItem> getItems(final String domaine, final boolean trierParAdresse) {
        if (domaine == null || domaine.isBlank()) {
            throw new IllegalArgumentException("Domaine vide ou nul");
        }

        // Normalise le domaine (en minuscules)
        final String domaineLower = domaine.toLowerCase(Locale.ROOT).trim();

        // Filtrage des entrées correspondant au domaine
        List<DnsItem> resultat = byName.values().stream()
            .filter(item -> item.nom().domaine().equals(domaineLower))
            .collect(Collectors.toCollection(ArrayList::new));

        // Tri en fonction du paramètre
        if (trierParAdresse) {
            resultat.sort(Comparator.comparing(i -> i.ip().value()));
        } else {
            resultat.sort(Comparator.comparing(i -> i.nom().value()));
        }

        return resultat;
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
  
  /**
     * Ajoute une nouvelle entrée (nom, IP) à la base DNS et sauvegarde la base.
     *
     * @param ip  l'adresse IP à ajouter
     * @param nom le nom de machine à ajouter
     * @throws RuntimeException si le nom ou l'IP existe déjà, ou en cas d'erreur d'écriture
     */
    public void addItem(final AdresseIP ip, final NomMachine nom) {
        if (ip == null || nom == null) {
            throw new IllegalArgumentException("Nom ou IP nul");
        }

        // Vérifie l’unicité
        if (byName.containsKey(nom.value())) {
            throw new RuntimeException("ERREUR : Le nom de machine existe déjà !");
        }
        if (byIp.containsKey(ip.value())) {
            throw new RuntimeException("ERREUR : L'adresse IP existe déjà !");
        }

        // Ajout en mémoire
        DnsItem item = new DnsItem(nom, ip);
        byName.put(nom.value(), item);
        byIp.put(ip.value(), item);

        
        save();
    }

    /**
     * Sauvegarde toutes les entrées de la base DNS dans le fichier texte.
     */
    private void save() {
        try {
            List<String> lignes = byName.values().stream()
                .sorted(Comparator.comparing(d -> d.nom().value()))
                .map(DnsItem::toString)
                .collect(Collectors.toList());

            Files.createDirectories(dbPath.getParent());
            Files.write(dbPath, lignes);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier " + dbPath, e);
        }
    }

  
   public Path getDbPath() {
    return dbPath;
  }

  
}

