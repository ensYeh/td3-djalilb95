package fr.uvsq.cprog.collex;

import java.util.Objects;
import java.util.regex.Pattern;


public final class AdresseIP {
    private static final Pattern IPV4 =
        Pattern.compile("^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$");

    private final String value;

    public AdresseIP(final String ip) {
        if (ip == null || !IPV4.matcher(ip.trim()).matches()) {
            throw new IllegalArgumentException("Adresse IP invalide : " + ip);
        }
        this.value = ip.trim();
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof AdresseIP other) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

