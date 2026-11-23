package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import java.util.UUID;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.WeiseBearbeiterZuCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Anfrage-DTO zur Zuweisung eines Bearbeiters zu einer Meldung.
 */
@Schema(name = "BearbeiterZuweisen", description = "Zuweisung eines Bearbeiters zu einer Meldung")
public record BearbeiterZuweisenDto(
        @Schema(description = "Id des Bearbeiters (UUID)",
        example = "33333333-3333-3333-3333-333333333333",
        requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID bearbeiterId
) {
    public WeiseBearbeiterZuCommand alsCommand(UUID meldungId) {
        return new WeiseBearbeiterZuCommand(meldungId, bearbeiterId);
    }
}
