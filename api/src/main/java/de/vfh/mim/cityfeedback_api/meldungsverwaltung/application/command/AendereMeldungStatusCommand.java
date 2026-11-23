package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;

import java.util.UUID;

/**
 * Anwendungsfall: Der Status einer Meldung wird geändert (z. B. ERLEDIGT oder ABGELEHNT).
 */
public record AendereMeldungStatusCommand(
        UUID meldungId,
        Status neuerStatus
) {
}
