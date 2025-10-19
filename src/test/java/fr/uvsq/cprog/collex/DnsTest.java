package fr.uvsq.cprog.collex;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests unitaires et d'intégration pour le projet DNS.
 */
public class DnsTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  /** Copie la base DNS d'origine vers un fichier temporaire pour tests isolés. */
  private Path copieDb() throws IOException {
    Path src = Path.of("src/main/resources/data/dns.txt");
    File f = tmp.newFile("dns.txt");
    Path dst = f.toPath();
    Files.copy(src, dst, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    return dst;
  }

  @Test
  public void testChargementBase() {
    Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));
    assertTrue(dns.size() >= 0);
  }

  @Test
  public void testGetItemByNomAndIp() {
    Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));
    NomMachine nom = new NomMachine("www.uvsq.fr");
    AdresseIP ip = new AdresseIP("193.51.31.90");

    DnsItem fromName = dns.getItem(nom);
    DnsItem fromIp = dns.getItem(ip);

    assertNotNull(fromName);
    assertNotNull(fromIp);
    assertEquals(fromName, fromIp);
  }

  @Test
  public void testAddItem_unique_ok() throws IOException {
    Path db = copieDb();
    Dns dns = new Dns(db);

    String fqdn = "test-" + System.nanoTime() + ".uvsq.fr";
    String ip = "193.51.31." + (100 + (int) (System.nanoTime() % 100));

    dns.addItem(new AdresseIP(ip), new NomMachine(fqdn));

    DnsItem item = dns.getItem(new NomMachine(fqdn));
    assertNotNull(item);
    assertEquals(ip, item.ip().value());

    String contenu = Files.readString(db);
    assertTrue(contenu.contains(fqdn + " " + ip));
  }

  @Test(expected = RuntimeException.class)
  public void testAddItem_doublon_nom_declenche_exception() throws IOException {
    Path db = copieDb();
    Dns dns = new Dns(db);

    String fqdn = "doublon-" + System.nanoTime() + ".uvsq.fr";
    String ip1 = "193.51.31.201";
    String ip2 = "193.51.31.202";

    dns.addItem(new AdresseIP(ip1), new NomMachine(fqdn)); // OK
    dns.addItem(new AdresseIP(ip2), new NomMachine(fqdn)); // Doit lever RuntimeException
  }

  /**
   * 🔄 Test d’intégration complet : simule une vraie session utilisateur
   * (add → lookup → ls → quit)
   */
  @Test
  public void testSessionComplete() throws Exception {
    Path db = copieDb();

    // Données uniques pour éviter les doublons
    String fqdn = "session-" + System.nanoTime() + ".uvsq.fr";
    String ip = "193.51.31." + (100 + (int) (System.nanoTime() % 100));

    // Commandes simulées comme si l'utilisateur tapait au clavier
    String inputCommands = String.join("\n",
        "add " + ip + " " + fqdn,
        fqdn,
        ip,
        "ls uvsq.fr",
        "quit"
    ) + "\n";

    InputStream fakeIn = new ByteArrayInputStream(inputCommands.getBytes());
    ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    InputStream originalIn = System.in;

    // Redirection des flux
    System.setIn(fakeIn);
    System.setOut(new PrintStream(fakeOut));

    try {
      Dns dns = new Dns(db);
      DnsTUI ui = new DnsTUI();

      while (true) {
        Commande cmd = ui.nextCommande();
        String out = cmd.execute(dns);
        ui.affiche(out);
        if (cmd.shouldQuit()) break;
      }

    } finally {
      System.setIn(originalIn);
      System.setOut(originalOut);
    }

    // Vérifications : la session doit contenir ces éléments
    String console = fakeOut.toString();
    System.out.println(console);

    assertTrue(console.contains("OK"));               // ajout réussi
    assertTrue(console.contains(ip));                 // IP affichée
    assertTrue(console.contains(fqdn));               // nom affiché
    assertTrue(console.toLowerCase().contains("bye")); // application terminée
  }
}


