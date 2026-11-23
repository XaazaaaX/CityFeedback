package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Buerger")
class BuergerTest {

    private static final Id ID = Id.neu();
    private static final String NACHNAME = "Muster";
    private static final String VORNAME = "Max";
    private static final String EMAIL = "max.muster@stadt.de";

    /** Buerger mit fester Id {@link #ID}, z. B. wie aus der Datenbank geladen. */
    private static Buerger gueltigerBuerger() {
        return Buerger.rekonstruiere(ID, Nachname.of(NACHNAME), Vorname.of(VORNAME), Email.of(EMAIL));
    }

    // =====================================================================
    @Nested
    @DisplayName("Factory erstelleBuerger")
    class Factory {

        @Test
        @DisplayName("erzeugt Buerger mit gültigen Werten")
        void erzeugtGueltigenBuerger() {
            Buerger b = Buerger.erstelleBuerger(NACHNAME, VORNAME, EMAIL);

            assertNotNull(b.getId());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Email.of(EMAIL), b.getEmail());
            assertEquals("Max Muster", b.getVollerName());
            assertTrue(b.toString().contains("Max Muster"));
        }

        @Test
        @DisplayName("entfernt Leerzeichen am Rand von Namen und normalisiert E-Mail")
        void trimmtEingaben() {
            Buerger b = Buerger.erstelleBuerger("  Muster ", " Max  ", "  Max.Muster@Stadt.DE ");

            assertEquals("Muster", b.getNachname().value());
            assertEquals("Max", b.getVorname().value());
            assertEquals("max.muster@stadt.de", b.getEmail().value());
        }

        @Test
        @DisplayName("vergibt jedem neuen Buerger eine eigene Id")
        void neueIdJeBuerger() {
            Buerger a = Buerger.erstelleBuerger(NACHNAME, VORNAME, EMAIL);
            Buerger b = Buerger.erstelleBuerger(NACHNAME, VORNAME, EMAIL);

            assertNotEquals(a.getId(), b.getId());
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("lehnt fehlende Pflichtwerte ab")
        void lehntNullWerteAb() {
            assertThrows(NullPointerException.class, () -> Buerger.erstelleBuerger(null, VORNAME, EMAIL));
            assertThrows(NullPointerException.class, () -> Buerger.erstelleBuerger(NACHNAME, null, EMAIL));
            assertThrows(NullPointerException.class, () -> Buerger.erstelleBuerger(NACHNAME, VORNAME, null));
        }

        @Test
        @DisplayName("lehnt ungültige Werte ab")
        void lehntUngueltigeWerteAb() {
            assertThrows(IllegalArgumentException.class, () -> Buerger.erstelleBuerger("", VORNAME, EMAIL));
            assertThrows(IllegalArgumentException.class, () -> Buerger.erstelleBuerger(NACHNAME, "", EMAIL));
            assertThrows(IllegalArgumentException.class, () -> Buerger.erstelleBuerger(NACHNAME, VORNAME, "ungueltig"));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("rekonstruiere")
    class Rekonstruktion {

        @Test
        @DisplayName("stellt Buerger mit den übergebenen Value Objects wieder her")
        void stelltWiederHer() {
            Buerger b = Buerger.rekonstruiere(ID, Nachname.of(NACHNAME), Vorname.of(VORNAME), Email.of(EMAIL));

            assertEquals(ID, b.getId());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Email.of(EMAIL), b.getEmail());
        }

        @Test
        @DisplayName("ist gleich einem über die Factory erzeugten Buerger mit derselben Id")
        void gleichWieFactory() {
            Buerger neu = Buerger.erstelleBuerger(NACHNAME, VORNAME, EMAIL);
            Buerger rekonstruiert = Buerger.rekonstruiere(neu.getId(), Nachname.of(NACHNAME),
                    Vorname.of(VORNAME), Email.of(EMAIL));

            assertEquals(neu, rekonstruiert);
        }

        @Test
        @DisplayName("lehnt fehlende Werte ab")
        void lehntNullAb() {
            assertThrows(NullPointerException.class, () -> Buerger.rekonstruiere(null,
                    Nachname.of(NACHNAME), Vorname.of(VORNAME), Email.of(EMAIL)));
            assertThrows(NullPointerException.class, () -> Buerger.rekonstruiere(ID,
                    null, Vorname.of(VORNAME), Email.of(EMAIL)));
            assertThrows(NullPointerException.class, () -> Buerger.rekonstruiere(ID,
                    Nachname.of(NACHNAME), null, Email.of(EMAIL)));
            assertThrows(NullPointerException.class, () -> Buerger.rekonstruiere(ID,
                    Nachname.of(NACHNAME), Vorname.of(VORNAME), null));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Fachmethoden")
    class Fachmethoden {

        @Test
        @DisplayName("aendereName übernimmt neue Namen")
        void aendereNameErfolgreich() {
            Buerger b = gueltigerBuerger();
            b.aendereName(Vorname.of("Anna"), Nachname.of("Schmidt"));

            assertEquals("Anna", b.getVorname().value());
            assertEquals("Schmidt", b.getNachname().value());
            assertEquals("Anna Schmidt", b.getVollerName());
        }

        @Test
        @DisplayName("aendereName lässt den Zustand bei null unverändert")
        void aendereNameNullAendertNichts() {
            Buerger b = gueltigerBuerger();

            assertThrows(NullPointerException.class, () -> b.aendereName(Vorname.of("Anna"), null));
            assertThrows(NullPointerException.class, () -> b.aendereName(null, Nachname.of("Schmidt")));
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
        }

        @Test
        @DisplayName("aendereEmail übernimmt neue E-Mail und lehnt null ab")
        void aendereEmail() {
            Buerger b = gueltigerBuerger();
            b.aendereEmail(Email.of("neu@stadt.de"));
            assertEquals("neu@stadt.de", b.getEmail().value());

            assertThrows(NullPointerException.class, () -> b.aendereEmail(null));
            assertEquals("neu@stadt.de", b.getEmail().value());
        }

        @Test
        @DisplayName("Änderungen verändern die Identität nicht")
        void aenderungenBehaltenId() {
            Buerger b = gueltigerBuerger();
            Buerger vorher = gueltigerBuerger();

            b.aendereName(Vorname.of("Anna"), Nachname.of("Schmidt"));
            b.aendereEmail(Email.of("anna@stadt.de"));

            assertEquals(ID, b.getId());
            assertEquals(vorher, b);
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Identität (equals / hashCode)")
    class Identitaet {

        @Test
        @DisplayName("Identität basiert ausschließlich auf der Id")
        void identitaetBasiertAufId() {
            Buerger a = gueltigerBuerger();
            Buerger b = Buerger.rekonstruiere(ID, Nachname.of("Schmidt"), Vorname.of("Anna"),
                    Email.of("anna@stadt.de"));
            Buerger c = Buerger.rekonstruiere(Id.neu(), Nachname.of(NACHNAME), Vorname.of(VORNAME),
                    Email.of(EMAIL));

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
        }

        @Test
        @DisplayName("equals prüft Reflexivität, null und fremde Typen")
        void equalsSonderfaelle() {
            Buerger a = gueltigerBuerger();

            assertEquals(a, a);
            assertNotEquals(null, a);
            assertNotEquals("KeinBuerger", a);
        }
    }
}
