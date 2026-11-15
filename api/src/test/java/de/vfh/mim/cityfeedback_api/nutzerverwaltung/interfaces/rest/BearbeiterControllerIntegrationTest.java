package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integrationstest der REST-Schnittstelle (Controller → Service → Repository → H2).
 * Jeder Test läuft in einer Transaktion, die anschließend zurückgerollt wird.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("REST /api/bearbeiter (Integration)")
class BearbeiterControllerIntegrationTest {

    private static final String URL = "/api/bearbeiter";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static String json(String nachname, String vorname, String email, String abteilung) {
        return """
                {"nachname": %s, "vorname": %s, "email": %s, "abteilung": %s}
                """.formatted(wert(nachname), wert(vorname), wert(email), wert(abteilung));
    }

    private static String wert(String s) {
        return s == null ? "null" : "\"" + s + "\"";
    }

    /** Legt einen Bearbeiter per POST an und liefert seine Id. */
    private String lege(String email) throws Exception {
        String location = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Muster", "Max", email, "Bauamt")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        return location.substring(location.lastIndexOf('/') + 1);
    }

    // =====================================================================
    @Nested
    @DisplayName("GET")
    class Lesen {

        @Test
        @DisplayName("liefert alle angelegten Bearbeiter")
        void alle() throws Exception {
            lege("erster@stadt.de");
            lege("zweiter@stadt.de");

            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[*].email", hasItem("erster@stadt.de")))
                    .andExpect(jsonPath("$[*].email", hasItem("zweiter@stadt.de")));
        }

        @Test
        @DisplayName("liefert leere Liste, wenn es keine Bearbeiter gibt")
        void leer() throws Exception {
            mvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("liefert einen einzelnen Bearbeiter")
        void einzeln() throws Exception {
            String id = lege("einzeln@stadt.de");

            mvc.perform(get(URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.nachname").value("Muster"))
                    .andExpect(jsonPath("$.vorname").value("Max"))
                    .andExpect(jsonPath("$.email").value("einzeln@stadt.de"))
                    .andExpect(jsonPath("$.abteilung").value("Bauamt"));
        }

        @Test
        @DisplayName("404 bei unbekannter Id")
        void unbekannt() throws Exception {
            mvc.perform(get(URL + "/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.title").value("Bearbeiter nicht gefunden"));
        }

        @Test
        @DisplayName("400 bei Id, die keine UUID ist")
        void idKeineUuid() throws Exception {
            mvc.perform(get(URL + "/keine-uuid"))
                    .andExpect(status().isBadRequest());
            mvc.perform(get(URL + "/1"))
                    .andExpect(status().isBadRequest());
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("POST")
    class Anlegen {

        @Test
        @DisplayName("legt Bearbeiter an: 201, Location-Header, bereinigte Werte")
        void anlegen() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(" Muster ", " Max ", " Max.Neu@Stadt.DE ", " Bauamt ")))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(URL + "/")))
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.nachname").value("Muster"))
                    .andExpect(jsonPath("$.vorname").value("Max"))
                    .andExpect(jsonPath("$.email").value("max.neu@stadt.de"))
                    .andExpect(jsonPath("$.abteilung").value("Bauamt"));
        }

        @Test
        @DisplayName("angelegter Bearbeiter ist über den Location-Header abrufbar")
        void anlegenUndAbrufen() throws Exception {
            String id = lege("abrufbar@stadt.de");

            mvc.perform(get(URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("abrufbar@stadt.de"));
        }

        @Test
        @DisplayName("409 bei bereits vergebener E-Mail (auch anders geschrieben)")
        void emailVergeben() throws Exception {
            lege("anna.schmidt@stadt.de");

            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Doppelt", "Anna", "Anna.Schmidt@Stadt.de", "Bauamt")))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.title").value("E-Mail bereits vergeben"));
        }

        @Test
        @DisplayName("400 bei ungültiger E-Mail (Regel der Domain)")
        void ungueltigeEmail() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "keine-email", "Bauamt")))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Ungültige Eingabe"));
        }

        @Test
        @DisplayName("400 bei leerem Vornamen")
        void leererVorname() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "  ", "leer@stadt.de", "Bauamt")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 bei fehlendem Feld")
        void fehlendesFeld() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "fehlt@stadt.de", null)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 bei zu langem Nachnamen")
        void zuLang() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("x".repeat(101), "Max", "lang@stadt.de", "Bauamt")))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("400 bei ungültigem JSON")
        void kaputtesJson() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ das ist kein json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("fehlgeschlagenes Anlegen speichert nichts")
        void fehlerSpeichertNichts() throws Exception {
            mvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "keine-email", "Bauamt")))
                    .andExpect(status().isBadRequest());

            mvc.perform(get(URL))
                    .andExpect(jsonPath("$[*].nachname", org.hamcrest.Matchers.not(hasItem("Muster"))));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("PUT")
    class Aendern {

        @Test
        @DisplayName("ändert alle Werte eines Bearbeiters")
        void aendern() throws Exception {
            String id = lege("vorher@stadt.de");

            mvc.perform(put(URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Schmidt", "Anna-Lena", "nachher@stadt.de", "Umweltamt")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.nachname").value("Schmidt"))
                    .andExpect(jsonPath("$.vorname").value("Anna-Lena"))
                    .andExpect(jsonPath("$.email").value("nachher@stadt.de"))
                    .andExpect(jsonPath("$.abteilung").value("Umweltamt"));

            mvc.perform(get(URL + "/" + id))
                    .andExpect(jsonPath("$.abteilung").value("Umweltamt"));
        }

        @Test
        @DisplayName("eigene, unveränderte E-Mail ist kein Konflikt")
        void eigeneEmailBehalten() throws Exception {
            String id = lege("bleibt@stadt.de");

            mvc.perform(put(URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "Bleibt@Stadt.de", "Umweltamt")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("bleibt@stadt.de"));
        }

        @Test
        @DisplayName("409 bei E-Mail eines anderen Bearbeiters")
        void emailEinesAnderen() throws Exception {
            lege("anna.schmidt@stadt.de");
            String id = lege("eigene@stadt.de");

            mvc.perform(put(URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "anna.schmidt@stadt.de", "Bauamt")))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("404 bei unbekannter Id")
        void unbekannt() throws Exception {
            mvc.perform(put(URL + "/" + UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Muster", "Max", "niemand@stadt.de", "Bauamt")))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("400 bei ungültiger Eingabe, Bearbeiter bleibt unverändert")
        void ungueltigUnveraendert() throws Exception {
            String id = lege("unveraendert@stadt.de");

            mvc.perform(put(URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json("Neu", "Neu", "keine-email", "Neu")))
                    .andExpect(status().isBadRequest());

            mvc.perform(get(URL + "/" + id))
                    .andExpect(jsonPath("$.nachname").value("Muster"))
                    .andExpect(jsonPath("$.email").value("unveraendert@stadt.de"));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("OpenAPI / Swagger")
    class Dokumentation {

        @Test
        @DisplayName("OpenAPI-Beschreibung enthält die Bearbeiter-Endpunkte")
        void apiDocs() throws Exception {
            mvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.info.title").value("CityFeedback API"))
                    .andExpect(jsonPath("$.paths['/api/bearbeiter']").exists())
                    .andExpect(jsonPath("$.paths['/api/bearbeiter/{id}']").exists());
        }
    }
}
