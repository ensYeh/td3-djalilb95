package fr.uvsq.cprog.collex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.util.List;

public class DnsTest {

    @Test
    public void testChargementBase() {
        Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));
        System.out.println("Entrées chargées : " + dns.size());
        System.out.println("Contient www.uvsq.fr ? " + dns.containsName("www.uvsq.fr"));
    }

    @Test
    public void testRechercheNomEtIp() {
        Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));

        NomMachine nom = new NomMachine("www.uvsq.fr");
        AdresseIP ip = new AdresseIP("193.51.31.90");

        DnsItem item1 = dns.getItem(nom);
        DnsItem item2 = dns.getItem(ip);

        assertNotNull(item1);
        assertNotNull(item2);
        assertEquals(item1, item2);
        assertEquals("www.uvsq.fr", item1.nom().value());
        assertEquals("193.51.31.90", item1.ip().value());
    }

    @Test
    public void testGetItemsParDomaine() {
        Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));

        // Domaine à tester
        String domaine = "uvsq.fr";

        // Liste triée par nom
        List<DnsItem> parNom = dns.getItems(domaine, false);
        assertFalse(parNom.isEmpty());
        System.out.println("---- Tri par nom ----");
        parNom.forEach(System.out::println);

        // Liste triée par adresse
        List<DnsItem> parAdresse = dns.getItems(domaine, true);
        assertFalse(parAdresse.isEmpty());
        System.out.println("---- Tri par adresse ----");
        parAdresse.forEach(System.out::println);
    }

    @Test
    public void testAddItem() {
        // Copie temporaire du fichier original
        Path fichier = Path.of("src/main/resources/data/dns.txt");
        Dns dns = new Dns(fichier);

        NomMachine nouveauNom = new NomMachine("test.uvsq.fr");
        AdresseIP nouvelleIp = new AdresseIP("193.51.31.200");

        dns.addItem(nouvelleIp, nouveauNom);

        // Vérifie que la nouvelle entrée est bien ajoutée
        DnsItem item = dns.getItem(nouveauNom);
        assertNotNull(item);
        assertEquals("193.51.31.200", item.ip().value());
    }



}
