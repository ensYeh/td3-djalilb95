package fr.uvsq.cprog.collex;

import org.junit.Test;
import java.nio.file.Path;

public class DnsTest {

    @Test
    public void testChargementBase() {
        Dns dns = new Dns(Path.of("src/main/resources/data/dns.txt"));
        System.out.println("Entrées chargées : " + dns.size());
        System.out.println("Contient www.uvsq.fr ? " + dns.containsName("www.uvsq.fr"));
    }
}
