package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter;

import java.util.Objects;

/**
 * Value Object: Abteilung, der ein Bearbeiter zugeordnet ist.
 */
public record Abteilung(String name) {

    public Abteilung {
        Objects.requireNonNull(name, "Abteilung darf nicht null sein.");
        name = name.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Abteilung darf nicht leer sein.");
        }
    }

    public static Abteilung of(String name) {
        return new Abteilung(name);
    }
}
