package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service.BearbeiterService;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto.BearbeiterAusgabeDto;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto.BearbeiterEingabeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST-Schnittstelle für Bearbeiter.
 * <p>
 * Nimmt HTTP-Anfragen entgegen, übersetzt sie in Commands für den
 * {@link BearbeiterService} und das Ergebnis in Antwort-DTOs.
 * Fehler werden im {@link BearbeiterExceptionHandler} in HTTP-Antworten übersetzt.
 */
@RestController
@RequestMapping("/api/bearbeiter")
@Tag(name = "Bearbeiter", description = "Verwaltung der Bearbeiter (Nutzerverwaltung)")
public class BearbeiterController {

    private final BearbeiterService service;

    public BearbeiterController(BearbeiterService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Alle Bearbeiter abrufen")
    @ApiResponse(responseCode = "200", description = "Liste aller Bearbeiter")
    public List<BearbeiterAusgabeDto> alleBearbeiter() {
        return service.alleBearbeiter().stream()
                .map(BearbeiterAusgabeDto::von)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Einen Bearbeiter abrufen")
    @ApiResponse(responseCode = "200", description = "Bearbeiter gefunden")
    @ApiResponse(responseCode = "400", description = "Ungültige Id (keine UUID)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Bearbeiter nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public BearbeiterAusgabeDto findeBearbeiter(
            @Parameter(description = "Id des Bearbeiters (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301") @PathVariable UUID id) {
        return BearbeiterAusgabeDto.von(service.findeBearbeiter(id));
    }

    @PostMapping
    @Operation(summary = "Neuen Bearbeiter anlegen")
    @ApiResponse(responseCode = "201", description = "Bearbeiter angelegt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "E-Mail-Adresse bereits vergeben",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BearbeiterAusgabeDto> legeBearbeiterAn(@Valid @RequestBody BearbeiterEingabeDto eingabe) {
        BearbeiterAusgabeDto angelegt = BearbeiterAusgabeDto.von(service.legeBearbeiterAn(eingabe.alsAnlegenCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(angelegt.id())
                .toUri();
        return ResponseEntity.created(location).body(angelegt);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Bearbeiter ändern", description = "Ersetzt Name, E-Mail und Abteilung eines Bearbeiters.")
    @ApiResponse(responseCode = "200", description = "Bearbeiter geändert")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe oder Id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Bearbeiter nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "E-Mail-Adresse bereits vergeben",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public BearbeiterAusgabeDto aendereBearbeiter(
            @Parameter(description = "Id des Bearbeiters (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301") @PathVariable UUID id,
            @Valid @RequestBody BearbeiterEingabeDto eingabe) {
        return BearbeiterAusgabeDto.von(service.aendereBearbeiter(eingabe.alsAendernCommand(id)));
    }
}
