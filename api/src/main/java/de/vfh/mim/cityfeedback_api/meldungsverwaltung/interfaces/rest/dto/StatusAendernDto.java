package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.AendereMeldungStatusCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Anfrage-DTO zum Ändern des Status einer Meldung (z. B. auf ERLEDIGT oder ABGELEHNT).
 */
@Schema(name = "StatusAendern", description = "Neuer Status für eine Meldung")
public record StatusAendernDto(
        @Schema(description = "Neuer Status (ERLEDIGT oder ABGELEHNT)", example = "ERLEDIGT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Status status
) {
    public AendereMeldungStatusCommand alsCommand(UUID meldungId) {
        return new AendereMeldungStatusCommand(meldungId, status);
    }
}
