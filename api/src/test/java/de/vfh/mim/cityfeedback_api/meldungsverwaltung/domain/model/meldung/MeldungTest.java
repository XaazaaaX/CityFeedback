package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Meldung (Domain)")
class MeldungTest {

    private static final UUID MELDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID BEARBEITER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String BETREFF = "Schlagloch in der Hauptstraße";
    private static final String TEXT = "Großes Schlagloch vor Hausnummer 12.";

    @Nested
    @DisplayName("Factory erstelleMeldung")
    class Factory {

        @Test
        @DisplayName("erzeugt Meldung im Status EINGEREICHT mit neuer Id")
        void erzeugtGueltigeMeldung() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.INFRASTRUKTUR, BETREFF, TEXT);

            assertNotNull(meldung.getId());
            assertEquals(MELDER_ID, meldung.getMelderId());
            assertNull(meldung.getBearbeiterId());
            assertEquals(Kategorie.INFRASTRUKTUR, meldung.getKategorie());
            assertEquals(Status.EINGEREICHT, meldung.getStatus());
            assertEquals(BETREFF, meldung.getInhalt().betreff());
            assertEquals(TEXT, meldung.getInhalt().text());
            assertNotNull(meldung.getInhalt().erstellZeitpunkt());
            assertTrue(meldung.getKommentare().isEmpty());
        }

        @Test
        @DisplayName("vergibt jeder neuen Meldung eine eigene Id")
        void neueIdJeMeldung() {
            Meldung a = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            Meldung b = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);

            assertNotEquals(a.getId(), b.getId());
            assertNotEquals(a, b);
        }

        @Test
        @DisplayName("lehnt null-Werte bei Pflichtfeldern ab")
        void lehntNullWerteAb() {
            assertThrows(NullPointerException.class, () ->
                    Meldung.erstelleMeldung(null, Kategorie.VERKEHR, BETREFF, TEXT));
            assertThrows(NullPointerException.class, () ->
                    Meldung.erstelleMeldung(MELDER_ID, null, BETREFF, TEXT));
            assertThrows(NullPointerException.class, () ->
                    Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, null, TEXT));
            assertThrows(NullPointerException.class, () ->
                    Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, null));
        }

        @Test
        @DisplayName("lehnt leeren Betreff oder Text ab")
        void lehntLeerenInhaltAb() {
            assertThrows(IllegalArgumentException.class, () ->
                    Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, "   ", TEXT));
            assertThrows(IllegalArgumentException.class, () ->
                    Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, ""));
        }
    }

    @Nested
    @DisplayName("Status-Workflow und Fachmethoden")
    class StatusWorkflow {

        @Test
        @DisplayName("bearbeiterZuweisen setzt Status auf IN_BEARBEITUNG und speichert bearbeiterId")
        void bearbeiterZuweisenErfolgreich() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);

            meldung.bearbeiterZuweisen(BEARBEITER_ID);

            assertEquals(Status.IN_BEARBEITUNG, meldung.getStatus());
            assertEquals(BEARBEITER_ID, meldung.getBearbeiterId());
        }

        @Test
        @DisplayName("bearbeiterZuweisen mit null wirft NullPointerException")
        void bearbeiterZuweisenNull() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);

            assertThrows(NullPointerException.class, () -> meldung.bearbeiterZuweisen(null));
        }

        @Test
        @DisplayName("kommentieren setzt Status auf KOMMENTIERT und fügt Kommentar hinzu")
        void kommentierenErfolgreich() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            meldung.bearbeiterZuweisen(BEARBEITER_ID);

            meldung.kommentieren(BEARBEITER_ID, "Bearbeitungsstand", "Wird morgen repariert.");

            assertEquals(Status.KOMMENTIERT, meldung.getStatus());
            assertEquals(1, meldung.getKommentare().size());
            Kommentar k = meldung.getKommentare().getFirst();
            assertEquals("Bearbeitungsstand", k.betreff());
            assertEquals("Wird morgen repariert.", k.text());
            assertEquals(BEARBEITER_ID, k.erstellerId());
            assertNotNull(k.erstellZeitpunkt());
        }

        @Test
        @DisplayName("mehrfaches Kommentieren bleibt im Status KOMMENTIERT")
        void mehrfachKommentieren() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            meldung.bearbeiterZuweisen(BEARBEITER_ID);
            meldung.kommentieren(BEARBEITER_ID, "Stand 1", "Prüfung läuft.");
            meldung.kommentieren(BEARBEITER_ID, "Stand 2", "Material bestellt.");

            assertEquals(Status.KOMMENTIERT, meldung.getStatus());
            assertEquals(2, meldung.getKommentare().size());
        }

        @Test
        @DisplayName("erledigen setzt Status auf ERLEDIGT")
        void erledigenErfolgreich() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            meldung.bearbeiterZuweisen(BEARBEITER_ID);
            meldung.kommentieren(BEARBEITER_ID, "Fertig", "Schlagloch gefüllt.");

            meldung.erledigen();

            assertEquals(Status.ERLEDIGT, meldung.getStatus());
        }

        @Test
        @DisplayName("ablehnen setzt Status auf ABGELEHNT")
        void ablehnenErfolgreich() {
            Meldung meldung = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            meldung.bearbeiterZuweisen(BEARBEITER_ID);
            meldung.kommentieren(BEARBEITER_ID, "Nicht zuständig", "Privatgrundstück.");

            meldung.ablehnen();

            assertEquals(Status.ABGELEHNT, meldung.getStatus());
        }

        @Test
        @DisplayName("ungültige Statuswechsel werfen IllegalStateException")
        void unzulaessigeUebergaenge() {
            Meldung neu = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);

            // Direkt von EINGEREICHT auf KOMMENTIERT, ERLEDIGT oder ABGELEHNT ist unzulässig
            assertThrows(IllegalStateException.class, () ->
                    neu.kommentieren(BEARBEITER_ID, "Zu früh", "Geht nicht."));
            assertThrows(IllegalStateException.class, neu::erledigen);
            assertThrows(IllegalStateException.class, neu::ablehnen);

            // Nach Abschluss kein weiterer Wechsel erlaubt
            neu.bearbeiterZuweisen(BEARBEITER_ID);
            neu.kommentieren(BEARBEITER_ID, "Fertig", "Erledigt.");
            neu.erledigen();

            assertThrows(IllegalStateException.class, () -> neu.bearbeiterZuweisen(BEARBEITER_ID));
            assertThrows(IllegalStateException.class, () ->
                    neu.kommentieren(BEARBEITER_ID, "Nochmal", "Nachtrag"));
            assertThrows(IllegalStateException.class, neu::ablehnen);
        }
    }

    @Nested
    @DisplayName("Rekonstruktion")
    class Rekonstruktion {

        @Test
        @DisplayName("rekonstruiere stellt Meldung mit bestehenden Daten wieder her")
        void rekonstruiertVollstaendig() {
            Id id = Id.neu();
            LocalDateTime zeitpunkt = LocalDateTime.of(2026, 11, 20, 10, 0);
            MeldungInhalt inhalt = MeldungInhalt.rekonstruiere("Titel", "Beschreibung", zeitpunkt);
            Kommentar k = Kommentar.rekonstruiere("Info", "In Arbeit", zeitpunkt, BEARBEITER_ID);

            Meldung meldung = Meldung.rekonstruiere(id, inhalt, Kategorie.UMWELT, Status.KOMMENTIERT,
                    MELDER_ID, BEARBEITER_ID, List.of(k));

            assertEquals(id, meldung.getId());
            assertEquals(inhalt, meldung.getInhalt());
            assertEquals(Kategorie.UMWELT, meldung.getKategorie());
            assertEquals(Status.KOMMENTIERT, meldung.getStatus());
            assertEquals(MELDER_ID, meldung.getMelderId());
            assertEquals(BEARBEITER_ID, meldung.getBearbeiterId());
            assertEquals(1, meldung.getKommentare().size());
            assertEquals(k, meldung.getKommentare().getFirst());
        }
    }

    @Nested
    @DisplayName("Identität (equals / hashCode)")
    class Identitaet {

        @Test
        @DisplayName("Identität basiert nur auf Id")
        void identitaetBasiertAufId() {
            Meldung a = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);
            Meldung b = Meldung.rekonstruiere(a.getId(), a.getInhalt(), Kategorie.SONSTIGES,
                    Status.ABGELEHNT, UUID.randomUUID(), null, new ArrayList<>());

            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("equals Sonderfälle")
        void equalsSonderfaelle() {
            Meldung m = Meldung.erstelleMeldung(MELDER_ID, Kategorie.VERKEHR, BETREFF, TEXT);

            assertEquals(m, m);
            assertNotEquals(null, m);
            assertNotEquals("KeineMeldung", m);
        }
    }
}
