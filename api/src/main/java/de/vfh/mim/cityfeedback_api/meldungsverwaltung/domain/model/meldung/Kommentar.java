package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object für einen Kommentar innerhalb einer Meldung.
 * <p>
 * Kommentare werden ausschließlich von Bearbeitern erstellt.
 * Der {@code erstellerId} ist die UUID des Bearbeiters (aus der Nutzerverwaltung),
 * es wird keine Klasse aus dem anderen Bounded Context importiert.
 */
public record Kommentar(String betreff, String text, LocalDateTime erstellZeitpunkt, UUID erstellerId) {

    public Kommentar {
        Objects.requireNonNull(betreff, "Betreff darf nicht null sein.");
        Objects.requireNonNull(text, "Text darf nicht null sein.");
        Objects.requireNonNull(erstellZeitpunkt, "ErstellZeitpunkt darf nicht null sein.");
        Objects.requireNonNull(erstellerId, "ErstellerId darf nicht null sein.");
        if (betreff.isBlank()) {
            throw new IllegalArgumentException("Betreff darf nicht leer sein.");
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("Text darf nicht leer sein.");
        }
    }

    /**
     * Erzeugt einen neuen Kommentar mit dem aktuellen Zeitpunkt.
     */
    public static Kommentar of(UUID erstellerId, String betreff, String text) {
        return new Kommentar(betreff, text, LocalDateTime.now(), erstellerId);
    }

    /**
     * Stellt einen Kommentar mit bekanntem Zeitpunkt wieder her (z. B. aus der Datenbank).
     */
    public static Kommentar rekonstruiere(String betreff, String text,
                                          LocalDateTime erstellZeitpunkt, UUID erstellerId) {
        return new Kommentar(betreff, text, erstellZeitpunkt, erstellerId);
    }
}
