package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Antwort-DTO der REST-Schnittstelle für einen Bürger (Teil des Interfaces-Layers).
 */
@Schema(name = "Buerger", description = "Ein Bürger (Nutzer der Plattform)")
public record BuergerAusgabeDto(
        @Schema(description = "Eindeutige Id (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301")
        UUID id,
        @Schema(example = "Mustermann")
        String nachname,
        @Schema(example = "Max")
        String vorname,
        @Schema(example = "max.mustermann@example.com")
        String email) {

    public static BuergerAusgabeDto von(Buerger buerger) {
        return new BuergerAusgabeDto(
                buerger.getId().value(),
                buerger.getNachname().value(),
                buerger.getVorname().value(),
                buerger.getEmail().value());
    }
}
