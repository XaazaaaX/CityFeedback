package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared;

import java.util.Objects;
import java.util.UUID;

/**
 * Zentrale Identität für alle Aggregate der <b>Meldungsverwaltung</b>.
 * <p>
 * Jeder Bounded Context hat seine eigene Id-Klasse. Diese hier gehört zur
 * Meldungsverwaltung und ist unabhängig von der Id der Nutzerverwaltung.
 * Die Id ist eine UUID und wird von der Domain selbst erzeugt ({@link #neu()}),
 * nicht von der Datenbank.
 */
public record Id(UUID value) {

    public Id {
        Objects.requireNonNull(value, "Id darf nicht null sein.");
    }

    /** Erzeugt eine neue, zufällige Id für ein neues Aggregat. */
    public static Id neu() {
        return new Id(UUID.randomUUID());
    }

    public static Id of(UUID value) {
        return new Id(value);
    }

    /**
     * Liest eine Id aus ihrer Textform, z. B. "3f2504e0-4f89-11d3-9a0c-0305e82c3301".
     *
     * @throws IllegalArgumentException wenn der Text keine gültige UUID ist
     */
    public static Id of(String value) {
        Objects.requireNonNull(value, "Id darf nicht null sein.");
        try {
            return new Id(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ungültige Id: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
