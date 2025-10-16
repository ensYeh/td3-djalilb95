package fr.uvsq.cprog.collex;

import java.util.Objects;



public final class DnsItem {
    private final NomMachine nom;
    private final AdresseIP ip;

    public DnsItem(final NomMachine nom, final AdresseIP ip) {
        this.nom = Objects.requireNonNull(nom, "nom");
        this.ip = Objects.requireNonNull(ip, "ip");
    }

    public NomMachine nom() {
        return nom;
    }

    public AdresseIP ip() {
        return ip;
    }

    @Override
    public String toString() {
        return nom.value() + " " + ip.value();
    }
}

