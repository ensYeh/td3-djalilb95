package fr.uvsq.cprog.collex;

/** Commande : rechercher le nom de machine associé à une adresse IP. */
public class CommandeRechercheNom implements Commande {

  private final AdresseIP ip;

  public CommandeRechercheNom(final String ipStr) {
    this.ip = new AdresseIP(ipStr);
  }

  @Override
  public String execute(final Dns dns) {
    DnsItem item = dns.getItem(ip);
    if (item == null) {
      return "ERREUR : IP inconnue";
    }
    return item.nom().value();
  }
}

