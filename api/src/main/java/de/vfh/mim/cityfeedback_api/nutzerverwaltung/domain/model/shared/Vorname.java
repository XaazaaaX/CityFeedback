package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared;

import java.util.Objects;

/**
 * Value Object: Vorname einer Person (ohne Leerzeichen am Rand, nicht leer).
 */
public record Vorname(String value) {

    public Vorname {
        Objects.requireNonNull(value, "Vorname darf nicht null sein.");
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein.");
        }
    }

    public static Vorname of(String value) {
        return new Vorname(value);
    }
}
