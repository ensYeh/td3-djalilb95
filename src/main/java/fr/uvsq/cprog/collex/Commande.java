package fr.uvsq.cprog.collex;

@FunctionalInterface
public interface Commande {
  /** Exécute la commande et renvoie le texte à afficher */
  String execute(Dns dns);

  /** true si cette commande doit terminer l'application */
  default boolean shouldQuit() {
    return false;
  }
}

