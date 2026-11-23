package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;

import java.util.UUID;

/**
 * Anwendungsfall: Ein Bürger reicht eine neue Meldung ein.
 */
public record ReicheMeldungEinCommand(
        UUID melderId,
        Kategorie kategorie,
        String betreff,
        String text
) {
}
