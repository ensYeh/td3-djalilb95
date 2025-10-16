package fr.uvsq.cprog.collex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;

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

}
