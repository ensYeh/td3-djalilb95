package fr.uvsq.cprog.collex;

import java.util.List;
import java.util.stream.Collectors;

/** Commande : liste les machines appartenant à un domaine donné */
public class CommandeListeDomaine implements Commande {

  private final String domaine;
  private final boolean trierParAdresse;

  public CommandeListeDomaine(final String domaine, final boolean trierParAdresse) {
    this.domaine = domaine;
    this.trierParAdresse = trierParAdresse;
  }

  @Override
  public String execute(final Dns dns) {
    List<DnsItem> items = dns.getItems(domaine, trierParAdresse);
    if (items.isEmpty()) {
      return "(aucune entrée pour le domaine " + domaine + ")";
    }

    return items.stream()
        .map(it -> trierParAdresse
            ? it.ip().value() + " " + it.nom().value()
            : it.nom().value() + " " + it.ip().value())
        .collect(Collectors.joining("\n"));
  }
}

