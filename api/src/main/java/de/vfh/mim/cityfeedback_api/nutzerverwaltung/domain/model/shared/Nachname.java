package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared;

import java.util.Objects;

/**
 * Value Object: Nachname einer Person (ohne Leerzeichen am Rand, nicht leer).
 */
public record Nachname(String value) {

    public Nachname {
        Objects.requireNonNull(value, "Nachname darf nicht null sein.");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein.");
        }
    }

    public static Nachname of(String value) {
        return new Nachname(value);
    }
}
