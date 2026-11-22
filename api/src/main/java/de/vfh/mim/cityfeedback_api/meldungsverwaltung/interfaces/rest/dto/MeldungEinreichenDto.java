package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.ReicheMeldungEinCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Anfrage-DTO zum Einreichen einer neuen Meldung.
 */
@Schema(name = "MeldungEinreichen", description = "Daten zum Einreichen einer neuen Meldung")
public record MeldungEinreichenDto(
        @Schema(description = "Id des meldenden Bürgers (UUID)", example = "11111111-1111-1111-1111-111111111111", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID melderId,

        @Schema(description = "Kategorie der Meldung", example = "VERKEHR", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        Kategorie kategorie,

        @Schema(description = "Betreff der Meldung", example = "Schlagloch auf der Hauptstraße", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String betreff,

        @Schema(description = "Beschreibung der Meldung", example = "Vor der Schule befindet sich ein tiefes Schlagloch.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String text
) {
    public ReicheMeldungEinCommand alsCommand() {
        return new ReicheMeldungEinCommand(melderId, kategorie, betreff, text);
    }
}
