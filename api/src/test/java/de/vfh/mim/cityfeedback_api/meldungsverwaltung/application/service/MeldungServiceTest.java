package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.AendereMeldungStatusCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.FuegeKommentarHinzuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.ReicheMeldungEinCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.WeiseBearbeiterZuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.exception.MeldungNichtGefundenException;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeldungService (Unit, Repository im Speicher)")
class MeldungServiceTest {

    private static class RepositoryImSpeicher implements MeldungRepository {
        private final List<Meldung> meldungen = new ArrayList<>();

        @Override
        public void save(Meldung m) {
            meldungen.remove(m);
            meldungen.add(m);
        }

        @Override
        public Optional<Meldung> findById(Id id) {
            return meldungen.stream().filter(m -> m.getId().equals(id)).findFirst();
        }

        @Override
        public List<Meldung> findAll() {
            return List.copyOf(meldungen);
        }

        @Override
        public List<Meldung> findByMelderId(UUID melderId) {
            return meldungen.stream().filter(m -> m.getMelderId().equals(melderId)).toList();
        }
    }

    private RepositoryImSpeicher repository;
    private MeldungService service;

    private static final UUID MELDER_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MELDER_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID BEARBEITER_1 = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Meldung basisMeldung;

    @BeforeEach
    void setUp() {
        repository = new RepositoryImSpeicher();
        service = new MeldungService(repository);
        basisMeldung = service.reicheMeldungEin(new ReicheMeldungEinCommand(
                MELDER_1, Kategorie.VERKEHR, "Schlagloch", "Vor der Schule"));
    }

    @Test
    @DisplayName("alleMeldungen liefert alle eingereichten Meldungen")
    void alleMeldungen() {
        List<Meldung> alle = service.alleMeldungen();

        assertEquals(1, alle.size());
        assertEquals(basisMeldung, alle.getFirst());
    }

    @Test
    @DisplayName("findeMeldung findet existierende Meldung")
    void findeMeldungGefunden() {
        Meldung gefunden = service.findeMeldung(basisMeldung.getId().value());

        assertEquals(basisMeldung, gefunden);
    }

    @Test
    @DisplayName("findeMeldung wirft Exception wenn nicht vorhanden")
    void findeMeldungNichtGefunden() {
        UUID unbekannt = UUID.randomUUID();

        assertThrows(MeldungNichtGefundenException.class, () -> service.findeMeldung(unbekannt));
    }

    @Test
    @DisplayName("findeMeldungenZuBuerger filtert nach melderId")
    void findeMeldungenZuBuerger() {
        service.reicheMeldungEin(new ReicheMeldungEinCommand(
                MELDER_2, Kategorie.UMWELT, "Müllablagerung", "Im Stadtpark"));

        List<Meldung> buerger1Meldungen = service.findeMeldungenZuBuerger(MELDER_1);
        List<Meldung> buerger2Meldungen = service.findeMeldungenZuBuerger(MELDER_2);

        assertEquals(1, buerger1Meldungen.size());
        assertEquals(basisMeldung, buerger1Meldungen.getFirst());
        assertEquals(1, buerger2Meldungen.size());
        assertEquals("Müllablagerung", buerger2Meldungen.getFirst().getInhalt().betreff());
    }

    @Test
    @DisplayName("reicheMeldungEin speichert neue Meldung im Status EINGEREICHT")
    void reicheMeldungEinErfolgreich() {
        Meldung m = service.reicheMeldungEin(new ReicheMeldungEinCommand(
                MELDER_1, Kategorie.INFRASTRUKTUR, "Laterne defekt", "Flackert nachts"));

        assertNotNull(m.getId());
        assertEquals(Status.EINGEREICHT, m.getStatus());
        assertEquals(2, service.alleMeldungen().size());
    }

    @Test
    @DisplayName("weiseBearbeiterZu weist Bearbeiter zu und ändert Status auf IN_BEARBEITUNG")
    void weiseBearbeiterZuErfolgreich() {
        Meldung aktualisiert = service.weiseBearbeiterZu(new WeiseBearbeiterZuCommand(
                basisMeldung.getId().value(), BEARBEITER_1));

        assertEquals(Status.IN_BEARBEITUNG, aktualisiert.getStatus());
        assertEquals(BEARBEITER_1, aktualisiert.getBearbeiterId());
    }

    @Test
    @DisplayName("fuegeKommentarHinzu fügt Kommentar hinzu und ändert Status auf KOMMENTIERT")
    void fuegeKommentarHinzuErfolgreich() {
        service.weiseBearbeiterZu(new WeiseBearbeiterZuCommand(basisMeldung.getId().value(), BEARBEITER_1));

        Meldung kommentiert = service.fuegeKommentarHinzu(new FuegeKommentarHinzuCommand(
                basisMeldung.getId().value(), BEARBEITER_1, "Rückfrage", "Welche Laternen-Nummer?"));

        assertEquals(Status.KOMMENTIERT, kommentiert.getStatus());
        assertEquals(1, kommentiert.getKommentare().size());
        assertEquals("Rückfrage", kommentiert.getKommentare().getFirst().betreff());
    }

    @Test
    @DisplayName("aendereStatus auf ERLEDIGT oder ABGELEHNT funktioniert nach Kommentierung")
    void aendereStatusErledigt() {
        service.weiseBearbeiterZu(new WeiseBearbeiterZuCommand(basisMeldung.getId().value(), BEARBEITER_1));
        service.fuegeKommentarHinzu(new FuegeKommentarHinzuCommand(
                basisMeldung.getId().value(), BEARBEITER_1, "Erledigt", "Laterne repariert."));

        Meldung erledigt = service.aendereStatus(new AendereMeldungStatusCommand(
                basisMeldung.getId().value(), Status.ERLEDIGT));

        assertEquals(Status.ERLEDIGT, erledigt.getStatus());
    }

    @Test
    @DisplayName("aendereStatus auf ungültige Zielstati wirft IllegalArgumentException")
    void aendereStatusUngueltig() {
        assertThrows(IllegalArgumentException.class, () ->
                service.aendereStatus(new AendereMeldungStatusCommand(
                        basisMeldung.getId().value(), Status.EINGEREICHT)));
    }
}
