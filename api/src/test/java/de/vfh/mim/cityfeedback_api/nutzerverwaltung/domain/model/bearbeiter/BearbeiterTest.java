package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bearbeiter")
class BearbeiterTest {

    private static final Id ID = Id.neu();
    private static final String NACHNAME = "Muster";
    private static final String VORNAME = "Max";
    private static final String EMAIL = "max.muster@stadt.de";
    private static final String ABTEILUNG = "Tiefbauamt";

    /** Bearbeiter mit fester Id {@link #ID}, z. B. wie aus der Datenbank geladen. */
    private static Bearbeiter gueltigerBearbeiter() {
        return Bearbeiter.rekonstruiere(ID, Nachname.of(NACHNAME), Vorname.of(VORNAME),
                Email.of(EMAIL), Abteilung.of(ABTEILUNG));
    }

    // =====================================================================
    @Nested
    @DisplayName("Factory erstelleBearbeiter")
    class Factory {

        @Test
        @DisplayName("erzeugt Bearbeiter mit gültigen Werten")
        void erzeugtGueltigenBearbeiter() {
            Bearbeiter b = Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, ABTEILUNG);

            assertNotNull(b.getId());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Email.of(EMAIL), b.getEmail());
            assertEquals(Abteilung.of(ABTEILUNG), b.getAbteilung());
            assertEquals("Max Muster", b.getVollerName());
        }

        @Test
        @DisplayName("entfernt Leerzeichen am Rand von Namen und Abteilung")
        void trimmtEingaben() {
            Bearbeiter b = Bearbeiter.erstelleBearbeiter("  Muster ", " Max  ",
                    "  Max.Muster@Stadt.DE ", "  Tiefbauamt  ");

            assertEquals("Muster", b.getNachname().value());
            assertEquals("Max", b.getVorname().value());
            assertEquals("max.muster@stadt.de", b.getEmail().value());
            assertEquals("Tiefbauamt", b.getAbteilung().name());
        }

        @Test
        @DisplayName("vergibt jedem neuen Bearbeiter eine eigene Id")
        void neueIdJeBearbeiter() {
            Bearbeiter a = Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, ABTEILUNG);
            Bearbeiter b = Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, ABTEILUNG);

            assertNotEquals(a.getId(), b.getId());
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("lehnt fehlenden Nachnamen ab")
        void lehntNullNachnameAb() {
            assertThrows(NullPointerException.class,
                    () -> Bearbeiter.erstelleBearbeiter(null, VORNAME, EMAIL, ABTEILUNG));
        }

        @Test
        @DisplayName("lehnt fehlenden Vornamen ab")
        void lehntNullVornameAb() {
            assertThrows(NullPointerException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, null, EMAIL, ABTEILUNG));
        }

        @Test
        @DisplayName("lehnt fehlende E-Mail ab")
        void lehntNullEmailAb() {
            assertThrows(NullPointerException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, null, ABTEILUNG));
        }

        @Test
        @DisplayName("lehnt fehlende Abteilung ab")
        void lehntNullAbteilungAb() {
            assertThrows(NullPointerException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, null));
        }

        @ParameterizedTest(name = "Nachname \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("lehnt leeren Nachnamen ab")
        void lehntLeerenNachnamenAb(String nachname) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Bearbeiter.erstelleBearbeiter(nachname, VORNAME, EMAIL, ABTEILUNG));
            assertTrue(ex.getMessage().contains("Nachname"));
        }

        @ParameterizedTest(name = "Vorname \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("lehnt leeren Vornamen ab")
        void lehntLeerenVornamenAb(String vorname) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, vorname, EMAIL, ABTEILUNG));
            assertTrue(ex.getMessage().contains("Vorname"));
        }

        @ParameterizedTest(name = "E-Mail \"{0}\"")
        @ValueSource(strings = {"", "   ", "keine-email", "max@", "@stadt.de", "max@stadt",
                "max@@stadt.de", "max muster@stadt.de", "max@stadt.d"})
        @DisplayName("lehnt ungültige E-Mail ab")
        void lehntUngueltigeEmailAb(String email) {
            assertThrows(IllegalArgumentException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, email, ABTEILUNG));
        }

        @ParameterizedTest(name = "Abteilung \"{0}\"")
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("lehnt leere Abteilung ab")
        void lehntLeereAbteilungAb(String abteilung) {
            assertThrows(IllegalArgumentException.class,
                    () -> Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, abteilung));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("rekonstruiere")
    class Rekonstruktion {

        @Test
        @DisplayName("stellt Bearbeiter mit den übergebenen Value Objects wieder her")
        void stelltWiederHer() {
            Bearbeiter b = Bearbeiter.rekonstruiere(ID, Nachname.of(NACHNAME), Vorname.of(VORNAME),
                    Email.of(EMAIL), Abteilung.of(ABTEILUNG));

            assertEquals(ID, b.getId());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Email.of(EMAIL), b.getEmail());
            assertEquals(Abteilung.of(ABTEILUNG), b.getAbteilung());
        }

        @Test
        @DisplayName("ist gleich einem über die Factory erzeugten Bearbeiter mit derselben Id")
        void gleichWieFactory() {
            Bearbeiter neu = Bearbeiter.erstelleBearbeiter(NACHNAME, VORNAME, EMAIL, ABTEILUNG);
            Bearbeiter rekonstruiert = Bearbeiter.rekonstruiere(neu.getId(), Nachname.of(NACHNAME),
                    Vorname.of(VORNAME), Email.of(EMAIL), Abteilung.of(ABTEILUNG));

            assertEquals(neu, rekonstruiert);
        }

        @Test
        @DisplayName("lehnt fehlende Werte ab")
        void lehntNullAb() {
            assertThrows(NullPointerException.class, () -> Bearbeiter.rekonstruiere(null,
                    Nachname.of(NACHNAME), Vorname.of(VORNAME), Email.of(EMAIL), Abteilung.of(ABTEILUNG)));
            assertThrows(NullPointerException.class, () -> Bearbeiter.rekonstruiere(ID,
                    null, Vorname.of(VORNAME), Email.of(EMAIL), Abteilung.of(ABTEILUNG)));
            assertThrows(NullPointerException.class, () -> Bearbeiter.rekonstruiere(ID,
                    Nachname.of(NACHNAME), null, Email.of(EMAIL), Abteilung.of(ABTEILUNG)));
            assertThrows(NullPointerException.class, () -> Bearbeiter.rekonstruiere(ID,
                    Nachname.of(NACHNAME), Vorname.of(VORNAME), null, Abteilung.of(ABTEILUNG)));
            assertThrows(NullPointerException.class, () -> Bearbeiter.rekonstruiere(ID,
                    Nachname.of(NACHNAME), Vorname.of(VORNAME), Email.of(EMAIL), null));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Fachmethoden")
    class Fachmethoden {

        @Test
        @DisplayName("aendereName übernimmt neue Namen")
        void aendereNameErfolgreich() {
            Bearbeiter b = gueltigerBearbeiter();
            b.aendereName(Vorname.of(" Anna "), Nachname.of(" Schmidt "));

            assertEquals("Anna", b.getVorname().value());
            assertEquals("Schmidt", b.getNachname().value());
            assertEquals("Anna Schmidt", b.getVollerName());
        }

        @Test
        @DisplayName("aendereName lässt den Zustand bei fehlendem Nachnamen unverändert")
        void aendereNameFehlenderNachnameAendertNichts() {
            Bearbeiter b = gueltigerBearbeiter();

            assertThrows(NullPointerException.class, () -> b.aendereName(Vorname.of("Anna"), null));
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
        }

        @Test
        @DisplayName("aendereName lässt den Zustand bei fehlendem Vornamen unverändert")
        void aendereNameFehlenderVornameAendertNichts() {
            Bearbeiter b = gueltigerBearbeiter();

            assertThrows(NullPointerException.class, () -> b.aendereName(null, Nachname.of("Schmidt")));
            assertEquals(Vorname.of(VORNAME), b.getVorname());
            assertEquals(Nachname.of(NACHNAME), b.getNachname());
        }

        @Test
        @DisplayName("aendereEmail übernimmt neue E-Mail")
        void aendereEmailErfolgreich() {
            Bearbeiter b = gueltigerBearbeiter();
            b.aendereEmail(Email.of("neu@stadt.de"));

            assertEquals("neu@stadt.de", b.getEmail().value());
        }

        @Test
        @DisplayName("aendereEmail lehnt null ab und behält alte E-Mail")
        void aendereEmailNull() {
            Bearbeiter b = gueltigerBearbeiter();

            assertThrows(NullPointerException.class, () -> b.aendereEmail(null));
            assertEquals(Email.of(EMAIL), b.getEmail());
        }

        @Test
        @DisplayName("wechsleAbteilung übernimmt neue Abteilung")
        void wechsleAbteilungErfolgreich() {
            Bearbeiter b = gueltigerBearbeiter();
            b.wechsleAbteilung(Abteilung.of("Umweltamt"));

            assertEquals("Umweltamt", b.getAbteilung().name());
        }

        @Test
        @DisplayName("wechsleAbteilung lehnt null ab und behält alte Abteilung")
        void wechsleAbteilungNull() {
            Bearbeiter b = gueltigerBearbeiter();

            assertThrows(NullPointerException.class, () -> b.wechsleAbteilung(null));
            assertEquals(Abteilung.of(ABTEILUNG), b.getAbteilung());
        }

        @Test
        @DisplayName("Änderungen verändern die Identität nicht")
        void aenderungenBehaltenId() {
            Bearbeiter b = gueltigerBearbeiter();
            Bearbeiter vorher = gueltigerBearbeiter();

            b.aendereName(Vorname.of("Anna"), Nachname.of("Schmidt"));
            b.aendereEmail(Email.of("anna@stadt.de"));
            b.wechsleAbteilung(Abteilung.of("Umweltamt"));

            assertEquals(ID, b.getId());
            assertEquals(vorher, b);
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Identität (equals / hashCode)")
    class Identitaet {

        @Test
        @DisplayName("gleiche Id bedeutet gleicher Bearbeiter, auch bei anderen Attributen")
        void gleicheIdIstGleich() {
            Bearbeiter a = gueltigerBearbeiter();
            Bearbeiter b = Bearbeiter.rekonstruiere(ID, Nachname.of("Schmidt"), Vorname.of("Anna"),
                    Email.of("anna@stadt.de"), Abteilung.of("Umweltamt"));

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("unterschiedliche Id bedeutet unterschiedlicher Bearbeiter, auch bei gleichen Attributen")
        void andereIdIstUngleich() {
            Bearbeiter a = gueltigerBearbeiter();
            Bearbeiter b = Bearbeiter.rekonstruiere(Id.neu(), Nachname.of(NACHNAME), Vorname.of(VORNAME),
                    Email.of(EMAIL), Abteilung.of(ABTEILUNG));

            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("ist nicht gleich null oder einem anderen Typ")
        void nichtGleichNullOderAnderemTyp() {
            Bearbeiter a = gueltigerBearbeiter();

            assertNotEquals(null, a);
            assertNotEquals("Bearbeiter", a);
            assertNotEquals(ID, a);
        }

        @Test
        @DisplayName("ist reflexiv")
        void reflexiv() {
            Bearbeiter a = gueltigerBearbeiter();
            assertEquals(a, a);
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Value Objects")
    class ValueObjects {

        // --- Vorname --------------------------------------------------------

        @Test
        @DisplayName("Vorname entfernt Leerzeichen am Rand")
        void vornameTrimmt() {
            assertEquals("Max", Vorname.of("  Max ").value());
        }

        @Test
        @DisplayName("Vorname behält Leerzeichen und Bindestriche im Inneren")
        void vornameMitMehrerenTeilen() {
            assertEquals("Anna-Lena Marie", Vorname.of(" Anna-Lena Marie ").value());
        }

        @Test
        @DisplayName("Vorname lehnt null ab")
        void vornameNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () -> Vorname.of(null));
            assertTrue(ex.getMessage().contains("Vorname"));
        }

        @ParameterizedTest(name = "Vorname \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("Vorname lehnt leere Werte ab")
        void vornameLeer(String wert) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Vorname.of(wert));
            assertTrue(ex.getMessage().contains("Vorname"));
        }

        @Test
        @DisplayName("Vorname ist über den bereinigten Wert gleich")
        void vornameWertgleichheit() {
            assertEquals(Vorname.of("Max"), Vorname.of(" Max "));
            assertEquals(Vorname.of("Max").hashCode(), Vorname.of(" Max ").hashCode());
            assertNotEquals(Vorname.of("Max"), Vorname.of("Moritz"));
        }

        // --- Nachname -------------------------------------------------------

        @Test
        @DisplayName("Nachname entfernt Leerzeichen am Rand")
        void nachnameTrimmt() {
            assertEquals("Muster", Nachname.of(" Muster  ").value());
        }

        @Test
        @DisplayName("Nachname behält Leerzeichen und Bindestriche im Inneren")
        void nachnameMitMehrerenTeilen() {
            assertEquals("von Müller-Lüdenscheidt", Nachname.of("von Müller-Lüdenscheidt").value());
        }

        @Test
        @DisplayName("Nachname lehnt null ab")
        void nachnameNull() {
            NullPointerException ex = assertThrows(NullPointerException.class, () -> Nachname.of(null));
            assertTrue(ex.getMessage().contains("Nachname"));
        }

        @ParameterizedTest(name = "Nachname \"{0}\"")
        @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
        @DisplayName("Nachname lehnt leere Werte ab")
        void nachnameLeer(String wert) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Nachname.of(wert));
            assertTrue(ex.getMessage().contains("Nachname"));
        }

        @Test
        @DisplayName("Nachname ist über den bereinigten Wert gleich")
        void nachnameWertgleichheit() {
            assertEquals(Nachname.of("Muster"), Nachname.of(" Muster "));
            assertEquals(Nachname.of("Muster").hashCode(), Nachname.of(" Muster ").hashCode());
            assertNotEquals(Nachname.of("Muster"), Nachname.of("Schmidt"));
        }

        @Test
        @DisplayName("Vorname und Nachname mit gleichem Text sind nicht gleich (unterschiedliche Typen)")
        void vornameUngleichNachname() {
            assertNotEquals(Vorname.of("Max"), Nachname.of("Max"));
        }

        // --- Email / Abteilung ----------------------------------------------

        @Test
        @DisplayName("Email ist über den normalisierten Wert gleich")
        void emailWertgleichheit() {
            assertEquals(Email.of("max@stadt.de"), Email.of(" MAX@Stadt.de "));
        }

        @Test
        @DisplayName("Abteilung ist über den Namen gleich")
        void abteilungWertgleichheit() {
            assertEquals(Abteilung.of("Tiefbauamt"), Abteilung.of(" Tiefbauamt "));
            assertNotEquals(Abteilung.of("Tiefbauamt"), Abteilung.of("Umweltamt"));
        }
    }
}
