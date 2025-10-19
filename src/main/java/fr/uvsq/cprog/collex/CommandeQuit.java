package fr.uvsq.cprog.collex;

/** Commande : quitter l'application DNS */
public class CommandeQuit implements Commande {

  @Override
  public String execute(final Dns dns) {
    return "Fermeture de l'application DNS.";
  }

  @Override
  public boolean shouldQuit() {
    return true;
  }
}

