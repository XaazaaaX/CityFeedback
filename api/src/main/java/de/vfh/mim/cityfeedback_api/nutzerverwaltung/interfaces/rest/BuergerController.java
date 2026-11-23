package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service.BuergerService;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto.BuergerAusgabeDto;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto.BuergerEingabeDto;
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
 * REST-Schnittstelle für Bürger.
 * <p>
 * Nimmt HTTP-Anfragen entgegen, übersetzt sie in Commands für den
 * {@link BuergerService} und das Ergebnis in Antwort-DTOs.
 * Fehler werden im {@link BuergerExceptionHandler} in HTTP-Antworten übersetzt.
 */
@RestController
@RequestMapping("/api/buerger")
@Tag(name = "Bürger", description = "Verwaltung der Bürger (Nutzerverwaltung)")
public class BuergerController {

    private final BuergerService service;

    public BuergerController(BuergerService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Alle Bürger abrufen")
    @ApiResponse(responseCode = "200", description = "Liste aller Bürger")
    public List<BuergerAusgabeDto> alleBuerger() {
        return service.alleBuerger().stream()
                .map(BuergerAusgabeDto::von)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Einen Bürger abrufen")
    @ApiResponse(responseCode = "200", description = "Bürger gefunden")
    @ApiResponse(responseCode = "400", description = "Ungültige Id (keine UUID)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Bürger nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public BuergerAusgabeDto findeBuerger(
            @Parameter(description = "Id des Bürgers (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301") @PathVariable UUID id) {
        return BuergerAusgabeDto.von(service.findeBuerger(id));
    }

    @PostMapping
    @Operation(summary = "Neuen Bürger anlegen")
    @ApiResponse(responseCode = "201", description = "Bürger angelegt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "E-Mail-Adresse bereits vergeben",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BuergerAusgabeDto> legeBuergerAn(@Valid @RequestBody BuergerEingabeDto eingabe) {
        BuergerAusgabeDto angelegt = BuergerAusgabeDto.von(service.legeBuergerAn(eingabe.alsAnlegenCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(angelegt.id())
                .toUri();
        return ResponseEntity.created(location).body(angelegt);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Bürger ändern", description = "Ersetzt Name und E-Mail eines Bürgers.")
    @ApiResponse(responseCode = "200", description = "Bürger geändert")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe oder Id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Bürger nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "E-Mail-Adresse bereits vergeben",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public BuergerAusgabeDto aendereBuerger(
            @Parameter(description = "Id des Bürgers (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301") @PathVariable UUID id,
            @Valid @RequestBody BuergerEingabeDto eingabe) {
        return BuergerAusgabeDto.von(service.aendereBuerger(eingabe.alsAendernCommand(id)));
    }
}
