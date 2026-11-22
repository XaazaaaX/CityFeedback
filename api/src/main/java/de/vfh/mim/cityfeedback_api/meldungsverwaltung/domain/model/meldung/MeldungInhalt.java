package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object für den Inhalt einer Meldung.
 * <p>
 * Enthält Betreff, Text und den Erstellzeitpunkt.
 * Der Zeitpunkt wird beim Erstellen automatisch gesetzt.
 */
public record MeldungInhalt(String betreff, String text, LocalDateTime erstellZeitpunkt) {

    public MeldungInhalt {
        Objects.requireNonNull(betreff, "Betreff darf nicht null sein.");
        Objects.requireNonNull(text, "Text darf nicht null sein.");
        Objects.requireNonNull(erstellZeitpunkt, "ErstellZeitpunkt darf nicht null sein.");
        if (betreff.isBlank()) {
            throw new IllegalArgumentException("Betreff darf nicht leer sein.");
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("Text darf nicht leer sein.");
        }
    }

    /**
     * Erzeugt einen neuen MeldungInhalt mit dem aktuellen Zeitpunkt.
     */
    public static MeldungInhalt of(String betreff, String text) {
        return new MeldungInhalt(betreff, text, LocalDateTime.now());
    }

    /**
     * Stellt einen MeldungInhalt mit bekanntem Zeitpunkt wieder her (z. B. aus der Datenbank).
     */
    public static MeldungInhalt rekonstruiere(String betreff, String text, LocalDateTime erstellZeitpunkt) {
        return new MeldungInhalt(betreff, text, erstellZeitpunkt);
    }
}
