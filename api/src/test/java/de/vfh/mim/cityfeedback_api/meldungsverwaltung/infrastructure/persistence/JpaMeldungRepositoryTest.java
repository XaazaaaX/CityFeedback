package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("JpaMeldungRepository (H2)")
class JpaMeldungRepositoryTest {

    @Autowired
    private MeldungRepository repository;

    private static final UUID MELDER_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID MELDER_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID BEARBEITER = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private Meldung neueMeldung(UUID melderId, String betreff) {
        return Meldung.erstelleMeldung(melderId, Kategorie.VERKEHR, betreff, "Detailtext zu " + betreff);
    }

    @Test
    @DisplayName("im Profil test sind keine Testdaten geladen")
    void keineTestdaten() {
        List<Meldung> alle = repository.findAll();
        assertTrue(alle.isEmpty());
    }

    @Test
    @DisplayName("gespeicherte Meldung kann mit allen Werten wieder geladen werden")
    void speichernUndLaden() {
        Meldung m = neueMeldung(MELDER_1, "Schlagloch");
        repository.save(m);

        Optional<Meldung> geladen = repository.findById(m.getId());

        assertTrue(geladen.isPresent());
        Meldung entity = geladen.get();
        assertEquals(m.getId(), entity.getId());
        assertEquals(MELDER_1, entity.getMelderId());
        assertNull(entity.getBearbeiterId());
        assertEquals(Kategorie.VERKEHR, entity.getKategorie());
        assertEquals(Status.EINGEREICHT, entity.getStatus());
        assertEquals("Schlagloch", entity.getInhalt().betreff());
        assertEquals("Detailtext zu Schlagloch", entity.getInhalt().text());
        assertNotNull(entity.getInhalt().erstellZeitpunkt());
        assertTrue(entity.getKommentare().isEmpty());
    }

    @Test
    @DisplayName("Statuswechsel und Bearbeiterzuweisung werden persistiert")
    void statuswechselPersistiert() {
        Meldung m = neueMeldung(MELDER_1, "Ampelausfall");
        repository.save(m);

        m.bearbeiterZuweisen(BEARBEITER);
        repository.save(m);

        Meldung inBearbeitung = repository.findById(m.getId()).orElseThrow();
        assertEquals(Status.IN_BEARBEITUNG, inBearbeitung.getStatus());
        assertEquals(BEARBEITER, inBearbeitung.getBearbeiterId());
    }

    @Test
    @DisplayName("Kommentare werden mit der Meldung gespeichert und geladen")
    void kommentarePersistiert() {
        Meldung m = neueMeldung(MELDER_1, "Baum umgestürzt");
        m.bearbeiterZuweisen(BEARBEITER);
        m.kommentieren(BEARBEITER, "Forstamt informiert", "Team rückt mit Motorsäge aus.");
        repository.save(m);

        Meldung geladen = repository.findById(m.getId()).orElseThrow();
        assertEquals(Status.KOMMENTIERT, geladen.getStatus());
        assertEquals(1, geladen.getKommentare().size());
        assertEquals("Forstamt informiert", geladen.getKommentare().getFirst().betreff());
        assertEquals("Team rückt mit Motorsäge aus.", geladen.getKommentare().getFirst().text());
        assertEquals(BEARBEITER, geladen.getKommentare().getFirst().erstellerId());
    }

    @Test
    @DisplayName("findByMelderId liefert alle Meldungen eines Bürgers")
    void findByMelderId() {
        Meldung m1 = neueMeldung(MELDER_1, "Meldung 1");
        Meldung m2 = neueMeldung(MELDER_1, "Meldung 2");
        Meldung m3 = neueMeldung(MELDER_2, "Meldung 3");

        repository.save(m1);
        repository.save(m2);
        repository.save(m3);

        List<Meldung> buerger1Meldungen = repository.findByMelderId(MELDER_1);
        List<Meldung> buerger2Meldungen = repository.findByMelderId(MELDER_2);

        assertEquals(2, buerger1Meldungen.size());
        assertEquals(1, buerger2Meldungen.size());
        assertTrue(buerger1Meldungen.contains(m1));
        assertTrue(buerger1Meldungen.contains(m2));
        assertTrue(buerger2Meldungen.contains(m3));
    }

    @Test
    @DisplayName("gespeicherte Meldung erscheint in findAll")
    void findAllEnthaeltMeldungen() {
        Meldung m = neueMeldung(MELDER_1, "Laterne kaputt");
        repository.save(m);

        List<Meldung> alle = repository.findAll();
        assertEquals(1, alle.size());
        assertTrue(alle.contains(m));
    }
}
