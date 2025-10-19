package fr.uvsq.cprog.collex;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * IHM texte (Text User Interface) pour dialoguer avec l'utilisateur.
 * - nextCommande() lit et analyse une ligne saisie, et renvoie une Commande.
 * - affiche(String) affiche un message à l'utilisateur.
 *
 * Commandes gérées :
 *   - "quit" | "exit"               -> termine l'application
 *   - "ls [-a] <domaine>"           -> liste les entrées du domaine (tri par nom ou par IP si -a)
 *   - "add <ip> <fqdn>"             -> ajoute une entrée
 *   - "<fqdn>"                      -> affiche l'IP associée
 *   - "<ip>"                        -> affiche le nom associé
 */
public class DnsTUI {

  private static final Pattern IPV4 =
      Pattern.compile("^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$");

  private final Scanner in;

  /** Construit la TUI en lisant sur l'entrée standard. */
  public DnsTUI() {
    this.in = new Scanner(System.in);
  }

  /**
   * Lit une ligne utilisateur, l'analyse et renvoie une Commande prête à exécuter.
   */
  public Commande nextCommande() {
    System.out.print("> ");
    final String line = in.nextLine().trim();
    if (line.isEmpty()) {
      return msg("Commande vide");
    }

    final String lower = line.toLowerCase(Locale.ROOT);

    // Quitter
    if ("quit".equals(lower) || "exit".equals(lower)) {
      return new Commande() {
        @Override
        public String execute(final Dns dns) {
          return "Fermeture de l'application dns.";
        }
        @Override
        public boolean shouldQuit() {
          return true;
        }
      };
    }

    
    final String[] t = line.split("\\s+");

    // ls [-a] domaine
    if ("ls".equals(t[0])) {
      final boolean triAdresse;
      final int idxDomaine;

      if (t.length >= 3 && "-a".equals(t[1])) {
        triAdresse = true;
        idxDomaine = 2;
      } else if (t.length >= 2) {
        triAdresse = false;
        idxDomaine = 1;
      } else {
        return msg("Usage : ls [-a] <domaine>");
      }

      final String domaine = t[idxDomaine];
      return dns -> {
        final List<DnsItem> items = dns.getItems(domaine, triAdresse);
        if (items.isEmpty()) {
          return "(aucune entrée pour le domaine " + domaine + ")";
        }
        final StringBuilder sb = new StringBuilder();
        for (DnsItem it : items) {
          if (triAdresse) {
            sb.append(it.ip().value()).append(' ').append(it.nom().value()).append('\n');
          } else {
            sb.append(it.nom().value()).append(' ').append(it.ip().value()).append('\n');
          }
        }
        return sb.toString().stripTrailing();
      };
    }

    // add <ip> <fqdn>
    if ("add".equals(t[0])) {
      if (t.length != 3) {
        return msg("Usage : add <ip> <fqdn>");
      }
      final String ipStr = t[1];
      final String fqdnStr = t[2];
      return dns -> {
        try {
          final AdresseIP ip = new AdresseIP(ipStr);
          final NomMachine nom = new NomMachine(fqdnStr);
          dns.addItem(ip, nom);
          return "OK";
        } catch (RuntimeException e) {
          return e.getMessage();
        }
      };
    }
    
    
    if (t.length == 1) {
      final String token = t[0];
      if (IPV4.matcher(token).matches()) {
        
        return dns -> {
          final DnsItem it = dns.getItem(new AdresseIP(token));
          return (it == null) ? "ERREUR : IP inconnue" : it.nom().value();
        };
      } else {
        
        return dns -> {
          final DnsItem it = dns.getItem(new NomMachine(token));
          return (it == null) ? "ERREUR : Nom inconnu" : it.ip().value();
        };
      }
    }

    
    return msg("Commande inconnue");
  }

  /**
   * Affiche un message, si non vide.
   */
  public void affiche(final String s) {
    if (s != null && !s.isBlank()) {
      System.out.println(s);
    }
  }

  /** Petite commande utilitaire qui renvoie un message fixe. */
  private static Commande msg(final String message) {
    return dns -> message;
  }
}

