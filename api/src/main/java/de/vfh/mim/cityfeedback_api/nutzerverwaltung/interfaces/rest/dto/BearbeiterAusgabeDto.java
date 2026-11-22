package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Antwort-DTO der REST-Schnittstelle für einen Bearbeiter (Teil des Interfaces-Layers).
 */
@Schema(name = "Bearbeiter", description = "Ein Bearbeiter der Stadtverwaltung")
public record BearbeiterAusgabeDto(
        @Schema(description = "Eindeutige Id (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301")
        UUID id,
        @Schema(example = "Schmidt")
        String nachname,
        @Schema(example = "Anna")
        String vorname,
        @Schema(example = "anna.schmidt@stadt.de")
        String email,
        @Schema(example = "Tiefbauamt")
        String abteilung) {

    public static BearbeiterAusgabeDto von(Bearbeiter bearbeiter) {
        return new BearbeiterAusgabeDto(
                bearbeiter.getId().value(),
                bearbeiter.getNachname().value(),
                bearbeiter.getVorname().value(),
                bearbeiter.getEmail().value(),
                bearbeiter.getAbteilung().name());
    }
}
