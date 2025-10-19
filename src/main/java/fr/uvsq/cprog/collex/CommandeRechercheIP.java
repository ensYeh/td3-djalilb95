package fr.uvsq.cprog.collex;

/** Commande : rechercher l'adresse IP d'une machine donnée */
public class CommandeRechercheIP implements Commande {

  private final NomMachine nom;

  public CommandeRechercheIP(final String fqdn) {
    this.nom = new NomMachine(fqdn);
  }

  @Override
  public String execute(final Dns dns) {
    DnsItem item = dns.getItem(nom);
    if (item == null) {
      return "ERREUR : nom inconnu";
    }
    return item.ip().value();
  }
}

