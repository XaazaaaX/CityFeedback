package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Antwort-DTO für eine Meldung.
 */
@Schema(name = "Meldung", description = "Eine Meldung im System")
public record MeldungAusgabeDto(
        @Schema(description = "Eindeutige Id (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301")
        UUID id,
        @Schema(description = "Id des meldenden Bürgers (UUID)", example = "11111111-1111-1111-1111-111111111111")
        UUID melderId,
        @Schema(description = "Id des zugewiesenen Bearbeiters (UUID, null wenn noch nicht zugewiesen)", example = "33333333-3333-3333-3333-333333333333")
        UUID bearbeiterId,
        @Schema(example = "VERKEHR")
        Kategorie kategorie,
        @Schema(example = "EINGEREICHT")
        Status status,
        @Schema(example = "Schlagloch auf der Hauptstraße")
        String betreff,
        @Schema(example = "Vor der Schule befindet sich ein tiefes Schlagloch.")
        String text,
        LocalDateTime erstellZeitpunkt,
        List<KommentarAusgabeDto> kommentare
) {
    public static MeldungAusgabeDto von(Meldung meldung) {
        List<KommentarAusgabeDto> kommentarDtos = meldung.getKommentare().stream()
                .map(KommentarAusgabeDto::von)
                .toList();
        return new MeldungAusgabeDto(
                meldung.getId().value(),
                meldung.getMelderId(),
                meldung.getBearbeiterId(),
                meldung.getKategorie(),
                meldung.getStatus(),
                meldung.getInhalt().betreff(),
                meldung.getInhalt().text(),
                meldung.getInhalt().erstellZeitpunkt(),
                kommentarDtos
        );
    }
}
