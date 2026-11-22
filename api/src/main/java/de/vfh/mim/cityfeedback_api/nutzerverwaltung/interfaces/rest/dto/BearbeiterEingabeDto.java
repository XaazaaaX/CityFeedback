package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBearbeiterCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBearbeiterAnCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Anfrage-DTO der REST-Schnittstelle zum Anlegen und Ändern eines Bearbeiters
 * (Teil des Interfaces-Layers).
 * <p>
 * Die Annotationen prüfen nur die Form der Anfrage. Die fachlichen Regeln
 * (z. B. das E-Mail-Format) prüfen die Value Objects der Domain.
 */
@Schema(name = "BearbeiterEingabe", description = "Daten zum Anlegen oder Ändern eines Bearbeiters")
public record BearbeiterEingabeDto(
        @Schema(example = "Muster", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String nachname,
        @Schema(example = "Max", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String vorname,
        @Schema(example = "max.muster@stadt.de", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 255)
        String email,
        @Schema(example = "Bauamt", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(max = 100)
        String abteilung) {

    public LegeBearbeiterAnCommand alsAnlegenCommand() {
        return new LegeBearbeiterAnCommand(nachname, vorname, email, abteilung);
    }

    public AendereBearbeiterCommand alsAendernCommand(UUID id) {
        return new AendereBearbeiterCommand(id, nachname, vorname, email, abteilung);
    }
}
