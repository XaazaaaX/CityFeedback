package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

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

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBuergerCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBuergerAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BuergerNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service.BuergerService;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuergerController (Unit)")
class BuergerControllerTest {

    private static final String URL = "/api/buerger";

    private static final UUID MAX_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ERIKA_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID UNBEKANNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Mock
    private BuergerService service;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new BuergerController(service))
                .setControllerAdvice(new BuergerExceptionHandler())
                .build();
    }

    private static Buerger buerger(UUID id, String nachname, String vorname, String email) {
        return Buerger.rekonstruiere(Id.of(id), Nachname.of(nachname), Vorname.of(vorname), Email.of(email));
    }

    private static String json(String nachname, String vorname, String email) {
        return """
                {"nachname": %s, "vorname": %s, "email": %s}
                """.formatted(wert(nachname), wert(vorname), wert(email));
    }

    private static String wert(String s) {
        return s == null ? "null" : "\"" + s + "\"";
    }

    @Nested
    @DisplayName("GET /api/buerger")
    class Alle {

        @Test
        @DisplayName("liefert 200 mit Liste aller Bürger")
        void liefertAlle() throws Exception {
            when(service.alleBuerger()).thenReturn(List.of(
                    buerger(MAX_ID, "Mustermann", "Max", "max@example.com"),
                    buerger(ERIKA_ID, "Musterfrau", "Erika", "erika@example.com")));

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(MAX_ID.toString()))
                    .andExpect(jsonPath("$[0].nachname").value("Mustermann"))
                    .andExpect(jsonPath("$[0].vorname").value("Max"))
                    .andExpect(jsonPath("$[0].email").value("max@example.com"))
                    .andExpect(jsonPath("$[1].id").value(ERIKA_ID.toString()));
        }

        @Test
        @DisplayName("liefert 200 mit leerer Liste wenn keine Bürger existieren")
        void liefertLeereListe() throws Exception {
            when(service.alleBuerger()).thenReturn(List.of());

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/buerger/{id}")
    class Einzeln {

        @Test
        @DisplayName("liefert 200 mit Bürger-Details")
        void gefunden() throws Exception {
            when(service.findeBuerger(MAX_ID)).thenReturn(buerger(MAX_ID, "Mustermann", "Max", "max@example.com"));

            mvc.perform(get(URL + "/" + MAX_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(MAX_ID.toString()))
                    .andExpect(jsonPath("$.nachname").value("Mustermann"))
                    .andExpect(jsonPath("$.vorname").value("Max"))
                    .andExpect(jsonPath("$.email").value("max@example.com"));
        }

        @Test
        @DisplayName("liefert 404 wenn Bürger nicht existiert")
        void nichtGefunden() throws Exception {
            when(service.findeBuerger(UNBEKANNT_ID)).thenThrow(new BuergerNichtGefundenException(UNBEKANNT_ID));

            mvc.perform(get(URL + "/" + UNBEKANNT_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Bürger nicht gefunden"));
        }

        @Test
        @DisplayName("liefert 400 bei syntaktisch ungültiger UUID")
        void unguealtigeUuid() throws Exception {
            mvc.perform(get(URL + "/keine-uuid"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("POST /api/buerger")
    class Anlegen {

        @Test
        @DisplayName("legt Bürger an, liefert 201 mit Location-Header")
        void erfolgreich() throws Exception {
            when(service.legeBuergerAn(new LegeBuergerAnCommand("Mustermann", "Max", "max@example.com")))
                    .thenReturn(buerger(MAX_ID, "Mustermann", "Max", "max@example.com"));

            mvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Mustermann", "Max", "max@example.com")))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + URL + "/" + MAX_ID))
                    .andExpect(jsonPath("$.id").value(MAX_ID.toString()))
                    .andExpect(jsonPath("$.nachname").value("Mustermann"));
        }

        @Test
        @DisplayName("liefert 409 bei doppelter E-Mail")
        void emailVergeben() throws Exception {
            when(service.legeBuergerAn(any()))
                    .thenThrow(new EmailBereitsVergebenException(Email.of("max@example.com")));

            mvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Mustermann", "Max", "max@example.com")))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("E-Mail bereits vergeben"));
        }

        @Test
        @DisplayName("liefert 409 bei DataIntegrityViolationException")
        void datenkonflikt() throws Exception {
            when(service.legeBuergerAn(any())).thenThrow(new DataIntegrityViolationException("unique constraint"));

            mvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Mustermann", "Max", "max@example.com")))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Datenkonflikt"));
        }

        @Test
        @DisplayName("liefert 400 bei Validierungsfehler im Request-Body")
        void validierungsfehler() throws Exception {
            mvc.perform(post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("", "Max", "max@example.com")))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("PUT /api/buerger/{id}")
    class Aendern {

        @Test
        @DisplayName("ändert Bürger und liefert 200")
        void erfolgreich() throws Exception {
            when(service.aendereBuerger(new AendereBuergerCommand(MAX_ID, "Neumann", "Moritz", "moritz@example.com")))
                    .thenReturn(buerger(MAX_ID, "Neumann", "Moritz", "moritz@example.com"));

            mvc.perform(put(URL + "/" + MAX_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Neumann", "Moritz", "moritz@example.com")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vorname").value("Moritz"))
                    .andExpect(jsonPath("$.nachname").value("Neumann"));
        }

        @Test
        @DisplayName("liefert 404 wenn Bürger nicht existiert")
        void nichtGefunden() throws Exception {
            when(service.aendereBuerger(any())).thenThrow(new BuergerNichtGefundenException(UNBEKANNT_ID));

            mvc.perform(put(URL + "/" + UNBEKANNT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Neumann", "Moritz", "moritz@example.com")))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Bürger nicht gefunden"));
        }

        @Test
        @DisplayName("liefert 409 wenn neue E-Mail bereits vergeben ist")
        void emailVergeben() throws Exception {
            when(service.aendereBuerger(any()))
                    .thenThrow(new EmailBereitsVergebenException(Email.of("besetzt@example.com")));

            mvc.perform(put(URL + "/" + MAX_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json("Neumann", "Moritz", "besetzt@example.com")))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("E-Mail bereits vergeben"));
        }
    }
}
