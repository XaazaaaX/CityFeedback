package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.FuegeKommentarHinzuCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Anfrage-DTO zum Hinzufügen eines Kommentars durch einen Bearbeiter.
 */
@Schema(name = "KommentarEingabe", description = "Neuer Kommentar zu einer Meldung")
public record KommentarEingabeDto(
        @Schema(description = "Id des kommentierenden Bearbeiters (UUID)", example = "33333333-3333-3333-3333-333333333333", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID bearbeiterId,

        @Schema(description = "Betreff des Kommentars", example = "Bearbeitungsstand", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String betreff,

        @Schema(description = "Text des Kommentars", example = "Die Reparatur wurde beauftragt.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String text
) {
    public FuegeKommentarHinzuCommand alsCommand(UUID meldungId) {
        return new FuegeKommentarHinzuCommand(meldungId, bearbeiterId, betreff, text);
    }
}
