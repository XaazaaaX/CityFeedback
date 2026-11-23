package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.service.MeldungService;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto.BearbeiterZuweisenDto;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto.KommentarEingabeDto;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto.MeldungAusgabeDto;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto.MeldungEinreichenDto;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest.dto.StatusAendernDto;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST-Schnittstelle für Meldungen.
 */
@RestController
@RequestMapping("/api/meldungen")
@Tag(name = "Meldungen", description = "Verwaltung der Meldungen (Meldungsverwaltung)")
public class MeldungController {

    private final MeldungService service;

    public MeldungController(MeldungService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Alle Meldungen abrufen", description = "Gibt alle Meldungen zurück, optional gefiltert nach melderId.")
    @ApiResponse(responseCode = "200", description = "Liste aller Meldungen")
    public List<MeldungAusgabeDto> alleMeldungen(
            @Parameter(description = "Optional: Id des Bürgers zur Filterung")
            @RequestParam(required = false) UUID melderId) {
        List<Meldung> ergebnis = (melderId != null)
                ? service.findeMeldungenZuBuerger(melderId)
                : service.alleMeldungen();
        return ergebnis.stream()
                .map(MeldungAusgabeDto::von)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Eine Meldung abrufen")
    @ApiResponse(responseCode = "200", description = "Meldung gefunden")
    @ApiResponse(responseCode = "400", description = "Ungültige Id (keine UUID)",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Meldung nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public MeldungAusgabeDto findeMeldung(
            @Parameter(description = "Id der Meldung (UUID)", example = "3f2504e0-4f89-11d3-9a0c-0305e82c3301")
            @PathVariable UUID id) {
        return MeldungAusgabeDto.von(service.findeMeldung(id));
    }

    @PostMapping
    @Operation(summary = "Neue Meldung einreichen")
    @ApiResponse(responseCode = "201", description = "Meldung eingereicht")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<MeldungAusgabeDto> reicheMeldungEin(
            @Valid @RequestBody MeldungEinreichenDto eingabe) {
        MeldungAusgabeDto angelegt = MeldungAusgabeDto.von(service.reicheMeldungEin(eingabe.alsCommand()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(angelegt.id())
                .toUri();
        return ResponseEntity.created(location).body(angelegt);
    }

    @PutMapping("/{id}/bearbeiter")
    @Operation(summary = "Bearbeiter zuweisen", description = "Weist einen Bearbeiter zu und ändert den Status auf IN_BEARBEITUNG.")
    @ApiResponse(responseCode = "200", description = "Bearbeiter zugewiesen")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe oder Id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Meldung nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Unzulässiger Statuswechsel",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public MeldungAusgabeDto weiseBearbeiterZu(
            @PathVariable UUID id,
            @Valid @RequestBody BearbeiterZuweisenDto eingabe) {
        return MeldungAusgabeDto.von(service.weiseBearbeiterZu(eingabe.alsCommand(id)));
    }

    @PostMapping("/{id}/kommentare")
    @Operation(summary = "Kommentar hinzufügen", description = "Ein Bearbeiter fügt einen Kommentar hinzu und setzt den Status auf KOMMENTIERT.")
    @ApiResponse(responseCode = "200", description = "Kommentar hinzugefügt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Meldung nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Unzulässiger Statuswechsel",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public MeldungAusgabeDto fuegeKommentarHinzu(
            @PathVariable UUID id,
            @Valid @RequestBody KommentarEingabeDto eingabe) {
        return MeldungAusgabeDto.von(service.fuegeKommentarHinzu(eingabe.alsCommand(id)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Status ändern", description = "Ändert den Status einer kommentierten Meldung (ERLEDIGT oder ABGELEHNT).")
    @ApiResponse(responseCode = "200", description = "Status geändert")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe oder Status",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Meldung nicht gefunden",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Unzulässiger Statuswechsel",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public MeldungAusgabeDto aendereStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusAendernDto eingabe) {
        return MeldungAusgabeDto.von(service.aendereStatus(eingabe.alsCommand(id)));
    }
}
