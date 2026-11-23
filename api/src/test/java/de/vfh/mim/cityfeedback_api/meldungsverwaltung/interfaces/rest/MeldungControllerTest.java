package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.AendereMeldungStatusCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.FuegeKommentarHinzuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.ReicheMeldungEinCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.WeiseBearbeiterZuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.exception.MeldungNichtGefundenException;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.service.MeldungService;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kommentar;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.MeldungInhalt;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeldungController (Unit)")
class MeldungControllerTest {

    private static final String URL = "/api/meldungen";

    private static final UUID MELDUNG_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MELDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID BEARBEITER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID UNBEKANNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Mock
    private MeldungService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new MeldungController(service))
                .setControllerAdvice(new MeldungExceptionHandler())
                .build();
    }

    private static Meldung meldung(UUID id, Status status, UUID bearbeiterId, List<Kommentar> kommentare) {
        return Meldung.rekonstruiere(
                Id.of(id),
                MeldungInhalt.rekonstruiere("Schlagloch", "Tiefes Loch", LocalDateTime.now()),
                Kategorie.VERKEHR,
                status,
                MELDER_ID,
                bearbeiterId,
                kommentare != null ? kommentare : new ArrayList<>()
        );
    }

    @Nested
    @DisplayName("GET /api/meldungen")
    class Alle {

        @Test
        @DisplayName("liefert 200 mit Liste aller Meldungen")
        void liefertAlle() throws Exception {
            when(service.alleMeldungen()).thenReturn(List.of(
                    meldung(MELDUNG_ID, Status.EINGEREICHT, null, null)));

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(MELDUNG_ID.toString()))
                    .andExpect(jsonPath("$[0].betreff").value("Schlagloch"))
                    .andExpect(jsonPath("$[0].kategorie").value("VERKEHR"))
                    .andExpect(jsonPath("$[0].status").value("EINGEREICHT"));
        }

        @Test
        @DisplayName("liefert 200 mit gefilterter Liste nach melderId")
        void liefertGefiltertNachMelderId() throws Exception {
            when(service.findeMeldungenZuBuerger(MELDER_ID)).thenReturn(List.of(
                    meldung(MELDUNG_ID, Status.EINGEREICHT, null, null)));

            mvc.perform(get(URL).param("melderId", MELDER_ID.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].melderId").value(MELDER_ID.toString()));
        }
    }

    @Nested
    @DisplayName("GET /api/meldungen/{id}")
    class Einzeln {

        @Test
        @DisplayName("liefert 200 mit Meldungs-Details")
        void gefunden() throws Exception {
            when(service.findeMeldung(MELDUNG_ID)).thenReturn(meldung(MELDUNG_ID, Status.EINGEREICHT, null, null));

            mvc.perform(get(URL + "/" + MELDUNG_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(MELDUNG_ID.toString()))
                    .andExpect(jsonPath("$.betreff").value("Schlagloch"));
        }

        @Test
        @DisplayName("liefert 404 wenn Meldung nicht existiert")
        void nichtGefunden() throws Exception {
            when(service.findeMeldung(UNBEKANNT_ID)).thenThrow(new MeldungNichtGefundenException(UNBEKANNT_ID));

            mvc.perform(get(URL + "/" + UNBEKANNT_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Meldung nicht gefunden"));
        }

        @Test
        @DisplayName("liefert 400 bei ungültiger UUID")
        void ungueltigeUuid() throws Exception {
            mvc.perform(get(URL + "/keine-uuid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("POST /api/meldungen")
    class Einreichen {

        @Test
        @DisplayName("reicht Meldung ein, liefert 201 mit Location-Header")
        void erfolgreich() throws Exception {
            when(service.reicheMeldungEin(new ReicheMeldungEinCommand(
                    MELDER_ID, Kategorie.VERKEHR, "Schlagloch", "Tiefes Loch")))
                    .thenReturn(meldung(MELDUNG_ID, Status.EINGEREICHT, null, null));

            String body = """
                    {
                        "melderId": "%s",
                        "kategorie": "VERKEHR",
                        "betreff": "Schlagloch",
                        "text": "Tiefes Loch"
                    }
                    """.formatted(MELDER_ID);

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + URL + "/" + MELDUNG_ID))
                    .andExpect(jsonPath("$.id").value(MELDUNG_ID.toString()))
                    .andExpect(jsonPath("$.status").value("EINGEREICHT"));
        }

        @Test
        @DisplayName("liefert 400 bei Validierungsfehler")
        void validierungsfehler() throws Exception {
            String body = """
                    {
                        "melderId": null,
                        "kategorie": "VERKEHR",
                        "betreff": "",
                        "text": "Tiefes Loch"
                    }
                    """;

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("PUT /api/meldungen/{id}/bearbeiter")
    class BearbeiterZuweisen {

        @Test
        @DisplayName("weist Bearbeiter zu und liefert 200")
        void erfolgreich() throws Exception {
            when(service.weiseBearbeiterZu(new WeiseBearbeiterZuCommand(MELDUNG_ID, BEARBEITER_ID)))
                    .thenReturn(meldung(MELDUNG_ID, Status.IN_BEARBEITUNG, BEARBEITER_ID, null));

            String body = """
                    {"bearbeiterId": "%s"}
                    """.formatted(BEARBEITER_ID);

            mvc.perform(put(URL + "/" + MELDUNG_ID + "/bearbeiter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_BEARBEITUNG"))
                    .andExpect(jsonPath("$.bearbeiterId").value(BEARBEITER_ID.toString()));
        }

        @Test
        @DisplayName("liefert 404 wenn Meldung nicht existiert")
        void nichtGefunden() throws Exception {
            when(service.weiseBearbeiterZu(any()))
                    .thenThrow(new MeldungNichtGefundenException(UNBEKANNT_ID));

            String body = """
                    {"bearbeiterId": "%s"}
                    """.formatted(BEARBEITER_ID);

            mvc.perform(put(URL + "/" + UNBEKANNT_ID + "/bearbeiter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("liefert 409 bei unzulässigem Statuswechsel")
        void unzulaessigerStatuswechsel() throws Exception {
            when(service.weiseBearbeiterZu(any()))
                    .thenThrow(new IllegalStateException("Statuswechsel nicht erlaubt."));

            String body = """
                    {"bearbeiterId": "%s"}
                    """.formatted(BEARBEITER_ID);

            mvc.perform(put(URL + "/" + MELDUNG_ID + "/bearbeiter")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Unzulässiger Statuswechsel"));
        }
    }

    @Nested
    @DisplayName("POST /api/meldungen/{id}/kommentare")
    class Kommentieren {

        @Test
        @DisplayName("fügt Kommentar hinzu und liefert 200")
        void erfolgreich() throws Exception {
            Kommentar k = Kommentar.rekonstruiere("Info", "Wird repariert", LocalDateTime.now(), BEARBEITER_ID);
            when(service.fuegeKommentarHinzu(new FuegeKommentarHinzuCommand(
                    MELDUNG_ID, BEARBEITER_ID, "Info", "Wird repariert")))
                    .thenReturn(meldung(MELDUNG_ID, Status.KOMMENTIERT, BEARBEITER_ID, List.of(k)));

            String body = """
                    {
                        "bearbeiterId": "%s",
                        "betreff": "Info",
                        "text": "Wird repariert"
                    }
                    """.formatted(BEARBEITER_ID);

            mvc.perform(post(URL + "/" + MELDUNG_ID + "/kommentare")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("KOMMENTIERT"))
                    .andExpect(jsonPath("$.kommentare.length()").value(1))
                    .andExpect(jsonPath("$.kommentare[0].betreff").value("Info"));
        }

        @Test
        @DisplayName("liefert 409 bei unzulässigem Statuswechsel (z. B. Meldung noch EINGEREICHT)")
        void unzulaessigerStatuswechsel() throws Exception {
            when(service.fuegeKommentarHinzu(any()))
                    .thenThrow(new IllegalStateException("Statuswechsel nicht erlaubt."));

            String body = """
                    {
                        "bearbeiterId": "%s",
                        "betreff": "Info",
                        "text": "Wird repariert"
                    }
                    """.formatted(BEARBEITER_ID);

            mvc.perform(post(URL + "/" + MELDUNG_ID + "/kommentare")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/meldungen/{id}/status")
    class StatusAendern {

        @Test
        @DisplayName("ändert Status und liefert 200")
        void erfolgreich() throws Exception {
            when(service.aendereStatus(new AendereMeldungStatusCommand(MELDUNG_ID, Status.ERLEDIGT)))
                    .thenReturn(meldung(MELDUNG_ID, Status.ERLEDIGT, BEARBEITER_ID, null));

            String body = """
                    {"status": "ERLEDIGT"}
                    """;

            mvc.perform(put(URL + "/" + MELDUNG_ID + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("ERLEDIGT"));
        }

        @Test
        @DisplayName("liefert 409 bei unzulässigem Statuswechsel")
        void unzulaessigerStatuswechsel() throws Exception {
            when(service.aendereStatus(any()))
                    .thenThrow(new IllegalStateException("Statuswechsel nicht erlaubt."));

            String body = """
                    {"status": "ERLEDIGT"}
                    """;

            mvc.perform(put(URL + "/" + MELDUNG_ID + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }
}
