package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBuergerCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBuergerAnCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Anfrage-DTO der REST-Schnittstelle zum Anlegen und Ändern eines Bürgers
 * (Teil des Interfaces-Layers).
 * <p>
 * Die Annotationen prüfen nur die Form der Anfrage. Die fachlichen Regeln
 * (z. B. das E-Mail-Format) prüfen die Value Objects der Domain.
 */
@Schema(name = "BuergerEingabe", description = "Daten zum Anlegen oder Ändern eines Bürgers")
public record BuergerEingabeDto(
        @Schema(example = "Mustermann", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String nachname,
        @Schema(example = "Max", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String vorname,
        @Schema(example = "max.mustermann@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String email) {

    public LegeBuergerAnCommand alsAnlegenCommand() {
        return new LegeBuergerAnCommand(nachname, vorname, email);
    }

    public AendereBuergerCommand alsAendernCommand(UUID id) {
        return new AendereBuergerCommand(id, nachname, vorname, email);
    }
}
