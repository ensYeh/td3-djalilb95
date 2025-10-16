package fr.uvsq.cprog.collex;

import java.util.Locale;
import java.util.Objects;


public final class NomMachine {
    private final String fqdn;

    public NomMachine(final String fqdn) {
        if (!isValidFqdn(fqdn)) {
            throw new IllegalArgumentException("Nom de machine invalide : " + fqdn);
        }
        this.fqdn = fqdn.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isValidFqdn(final String s) {
        if (s == null) {
            return false;
        }
        final String t = s.trim();
        if (t.isEmpty() || t.startsWith(".") || t.endsWith(".") || !t.contains(".") || t.contains("..")) {
            return false;
        }
        final String[] labels = t.split("\\.");
        for (String label : labels) {
            if (label.isEmpty()
                || label.startsWith("-")
                || label.endsWith("-")
                || !label.matches("[A-Za-z0-9-]{1,63}")) {
                return false;
            }
        }
        return true;
    }

    public String value() {
        return fqdn;
    }

    
    public String domaine() {
        final int i = fqdn.indexOf('.');
        return fqdn.substring(i + 1);
    }

    @Override
    public String toString() {
        return fqdn;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof NomMachine other) && fqdn.equals(other.fqdn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqdn);
    }
}

