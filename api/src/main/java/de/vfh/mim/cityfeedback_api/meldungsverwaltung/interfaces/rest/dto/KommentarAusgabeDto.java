package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kommentar;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Antwort-DTO für einen Kommentar zu einer Meldung.
 */
@Schema(name = "Kommentar", description = "Ein Kommentar zu einer Meldung")
public record KommentarAusgabeDto(
        @Schema(example = "Bearbeitungsstand")
        String betreff,
        @Schema(example = "Die Reparatur wurde beauftragt.")
        String text,
        LocalDateTime erstellZeitpunkt,
        @Schema(description = "Id des Bearbeiters (UUID)", example = "33333333-3333-3333-3333-333333333333")
        UUID erstellerId
) {
    public static KommentarAusgabeDto von(Kommentar kommentar) {
        return new KommentarAusgabeDto(
                kommentar.betreff(),
                kommentar.text(),
                kommentar.erstellZeitpunkt(),
                kommentar.erstellerId()
        );
    }
}
