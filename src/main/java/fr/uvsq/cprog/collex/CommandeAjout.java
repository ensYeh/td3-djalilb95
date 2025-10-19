package fr.uvsq.cprog.collex;

/** Commande : ajoute un nouvel item dans la base DNS. */
public class CommandeAjout implements Commande {

  private final AdresseIP ip;
  private final NomMachine nom;

  public CommandeAjout(final String ipStr, final String fqdn) {
    this.ip = new AdresseIP(ipStr);
    this.nom = new NomMachine(fqdn);
  }

  @Override
  public String execute(final Dns dns) {
    try {
      dns.addItem(ip, nom);
      return "OK";
    } catch (RuntimeException e) {
      return e.getMessage();
    }
  }
}

