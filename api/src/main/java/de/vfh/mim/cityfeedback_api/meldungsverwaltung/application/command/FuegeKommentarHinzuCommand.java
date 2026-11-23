package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command;

import java.util.UUID;

/**
 * Anwendungsfall: Ein Bearbeiter fügt einer Meldung einen Kommentar hinzu.
 */
public record FuegeKommentarHinzuCommand(
        UUID meldungId,
        UUID bearbeiterId,
        String betreff,
        String text
) {
}
