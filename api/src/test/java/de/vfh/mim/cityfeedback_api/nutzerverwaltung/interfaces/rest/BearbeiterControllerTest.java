package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBearbeiterCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBearbeiterAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BearbeiterNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service.BearbeiterService;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Abteilung;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest.dto.BearbeiterEingabeDto;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit-Test des {@link BearbeiterController} samt {@link BearbeiterExceptionHandler}.
 * <p>
 * Ohne Spring-Kontext und ohne Datenbank: Der {@link BearbeiterService} ist ein
 * Mockito-Mock. Geprüft wird nur, was der Controller selbst leistet: URLs,
 * HTTP-Methoden, Statuscodes, JSON, Eingabeprüfung und Fehlerübersetzung.
 * Das Zusammenspiel aller Schichten testet {@link BearbeiterControllerIntegrationTest}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BearbeiterController (Unit)")
class BearbeiterControllerTest {

    private static final String URL = "/api/bearbeiter";

    private static final UUID ANNA_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID JONAS_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID MAX_ID = UUID.fromString("00000000-0000-0000-0000-000000000007");
    private static final UUID UNBEKANNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    private static final Bearbeiter ANNA = bearbeiter(ANNA_ID, "Schmidt", "Anna", "anna.schmidt@stadt.de", "Tiefbauamt");
    private static final Bearbeiter JONAS = bearbeiter(JONAS_ID, "Müller", "Jonas", "jonas.mueller@stadt.de", "Umweltamt");
    private static final Bearbeiter MAX = bearbeiter(MAX_ID, "Muster", "Max", "max@stadt.de", "Bauamt");

    private static final BearbeiterEingabeDto EINGABE =
            new BearbeiterEingabeDto("Muster", "Max", "max@stadt.de", "Bauamt");

    /** Bearbeiter mit fester Id, damit die Antworten vorhersagbar sind. */
    private static Bearbeiter bearbeiter(UUID id, String nachname, String vorname, String email, String abteilung) {
        return Bearbeiter.rekonstruiere(Id.of(id), Nachname.of(nachname), Vorname.of(vorname),
                Email.of(email), Abteilung.of(abteilung));
    }

    @Mock
    private BearbeiterService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BearbeiterController(service))
                .setControllerAdvice(new BearbeiterExceptionHandler())
                .build();
    }

    private static String json(String nachname, String vorname, String email, String abteilung) {
        return """
                {"nachname": %s, "vorname": %s, "email": %s, "abteilung": %s}
                """.formatted(wert(nachname), wert(vorname), wert(email), wert(abteilung));
    }

    private static String json(BearbeiterEingabeDto e) {
        return json(e.nachname(), e.vorname(), e.email(), e.abteilung());
    }

    private static String wert(String s) {
        return s == null ? "null" : "\"" + s + "\"";
    }

    // =====================================================================
    @Nested
    @DisplayName("GET /api/bearbeiter")
    class Alle {

        @Test
        @DisplayName("liefert die Liste des Service als JSON")
        void liste() throws Exception {
            when(service.alleBearbeiter()).thenReturn(List.of(ANNA, JONAS));

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(ANNA_ID.toString()))
                    .andExpect(jsonPath("$[0].nachname").value("Schmidt"))
                    .andExpect(jsonPath("$[1].email").value("jonas.mueller@stadt.de"));
        }

        @Test
        @DisplayName("liefert leere Liste, wenn es keine Bearbeiter gibt")
        void leer() throws Exception {
            when(service.alleBearbeiter()).thenReturn(List.of());

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("GET /api/bearbeiter/{id}")
    class Einzeln {

        @Test
        @DisplayName("liefert den Bearbeiter mit allen Feldern")
        void gefunden() throws Exception {
            when(service.findeBearbeiter(ANNA_ID)).thenReturn(ANNA);

            mvc.perform(get(URL + "/" + ANNA_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ANNA_ID.toString()))
                    .andExpect(jsonPath("$.nachname").value("Schmidt"))
                    .andExpect(jsonPath("$.vorname").value("Anna"))
                    .andExpect(jsonPath("$.email").value("anna.schmidt@stadt.de"))
                    .andExpect(jsonPath("$.abteilung").value("Tiefbauamt"));
        }

        @Test
        @DisplayName("404 mit Problem Details, wenn der Service nichts findet")
        void nichtGefunden() throws Exception {
            when(service.findeBearbeiter(UNBEKANNT_ID)).thenThrow(new BearbeiterNichtGefundenException(UNBEKANNT_ID));

            mvc.perform(get(URL + "/" + UNBEKANNT_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Bearbeiter nicht gefunden"))
                    .andExpect(jsonPath("$.detail").value("Bearbeiter mit Id " + UNBEKANNT_ID + " wurde nicht gefunden."));
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf, wenn die Id keine UUID ist")
        void idKeineZahl() throws Exception {
            mvc.perform(get(URL + "/keine-uuid"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("POST /api/bearbeiter")
    class Anlegen {

        @Test
        @DisplayName("201 mit Location-Header und angelegtem Bearbeiter")
        void angelegt() throws Exception {
            when(service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Muster", "Max", "max@stadt.de", "Bauamt")))
                    .thenReturn(MAX);

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/bearbeiter/" + MAX_ID))
                    .andExpect(jsonPath("$.id").value(MAX_ID.toString()))
                    .andExpect(jsonPath("$.email").value("max@stadt.de"));
        }

        @Test
        @DisplayName("gibt die Eingabe unverändert als Command an den Service weiter")
        void eingabeWeitergegeben() throws Exception {
            BearbeiterEingabeDto roh = new BearbeiterEingabeDto(" Muster ", " Max ", " Max@Stadt.DE ", " Bauamt ");
            when(service.legeBearbeiterAn(any())).thenReturn(MAX);

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(roh)))
                    .andExpect(status().isCreated());

            verify(service).legeBearbeiterAn(
                    new LegeBearbeiterAnCommand(" Muster ", " Max ", " Max@Stadt.DE ", " Bauamt "));
        }

        @Test
        @DisplayName("409, wenn die E-Mail vergeben ist")
        void emailVergeben() throws Exception {
            when(service.legeBearbeiterAn(any()))
                    .thenThrow(new EmailBereitsVergebenException(Email.of("max@stadt.de")));

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.title").value("E-Mail bereits vergeben"));
        }

        @Test
        @DisplayName("409, wenn die Datenbank die E-Mail ablehnt (gleichzeitige Anfragen)")
        void datenbankKonflikt() throws Exception {
            when(service.legeBearbeiterAn(any()))
                    .thenThrow(new DataIntegrityViolationException("UNIQUE constraint verletzt"));

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.title").value("Datenkonflikt"));
        }

        @Test
        @DisplayName("400, wenn die Domain einen Wert ablehnt")
        void domainLehntAb() throws Exception {
            when(service.legeBearbeiterAn(any()))
                    .thenThrow(new IllegalArgumentException("Ungültige E-Mail-Adresse: keine-email"));

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "keine-email", "Bauamt")))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Ungültige Eingabe"));
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei leerem Feld")
        void leeresFeld() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "  ", "max@stadt.de", "Bauamt")))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei fehlendem Feld")
        void fehlendesFeld() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", null, "Bauamt")))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei zu langem Wert")
        void zuLang() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "max@stadt.de", "x".repeat(101))))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei ungültigem JSON")
        void kaputtesJson() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ das ist kein json"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei leerem Body")
        void ohneBody() throws Exception {
            mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("415 ohne Service-Aufruf, wenn kein JSON gesendet wird")
        void keinJson() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("Muster;Max;max@stadt.de;Bauamt"))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(service);
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("PUT /api/bearbeiter/{id}")
    class Aendern {

        @Test
        @DisplayName("200 mit geändertem Bearbeiter, Id und Eingabe gehen als Command an den Service")
        void geaendert() throws Exception {
            AendereBearbeiterCommand command = new AendereBearbeiterCommand(ANNA_ID, "Muster", "Max", "max@stadt.de", "Bauamt");
            when(service.aendereBearbeiter(command)).thenReturn(bearbeiter(ANNA_ID, "Muster", "Max", "max@stadt.de", "Bauamt"));

            mvc.perform(put(URL + "/" + ANNA_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ANNA_ID.toString()))
                    .andExpect(jsonPath("$.nachname").value("Muster"));

            verify(service).aendereBearbeiter(command);
        }

        @Test
        @DisplayName("404, wenn der Bearbeiter nicht existiert")
        void nichtGefunden() throws Exception {
            when(service.aendereBearbeiter(any()))
                    .thenThrow(new BearbeiterNichtGefundenException(UNBEKANNT_ID));

            mvc.perform(put(URL + "/" + UNBEKANNT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("409, wenn die E-Mail einem anderen gehört")
        void emailVergeben() throws Exception {
            when(service.aendereBearbeiter(any()))
                    .thenThrow(new EmailBereitsVergebenException(Email.of("max@stadt.de")));

            mvc.perform(put(URL + "/" + ANNA_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf bei leerem Feld")
        void leeresFeld() throws Exception {
            mvc.perform(put(URL + "/" + ANNA_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("", "Max", "max@stadt.de", "Bauamt")))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("400 ohne Service-Aufruf, wenn die Id keine UUID ist")
        void idKeineZahl() throws Exception {
            mvc.perform(put(URL + "/keine-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(EINGABE)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    // =====================================================================
    @Test
    @DisplayName("405 für nicht unterstützte Methode (DELETE)")
    void deleteNichtUnterstuetzt() throws Exception {
        mvc.perform(delete(URL + "/" + ANNA_ID))
                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(service);
    }
}
