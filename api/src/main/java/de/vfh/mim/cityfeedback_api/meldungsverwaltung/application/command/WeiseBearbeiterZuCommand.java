package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command;

import java.util.UUID;

/**
 * Anwendungsfall: Ein Bearbeiter wird einer Meldung zugewiesen.
 */
public record WeiseBearbeiterZuCommand(
        UUID meldungId,
        UUID bearbeiterId
) {
}
