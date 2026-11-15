package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Abteilung;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstest: Domain-Repository gegen die H2-In-Memory-Datenbank.
 * Jeder Test läuft in einer Transaktion, die anschließend zurückgerollt wird.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("JpaBearbeiterRepository (H2)")
class JpaBearbeiterRepositoryTest {

    @Autowired
    private BearbeiterRepository repository;

    private Bearbeiter neuerBearbeiter(String email) {
        return Bearbeiter.erstelleBearbeiter("Muster", "Max", email, "Bauamt");
    }

    @Test
    @DisplayName("im Profil test sind keine Testdaten geladen")
    void keineTestdaten() {
        List<Bearbeiter> alle = repository.findAll();

        assertTrue(alle.isEmpty());
        assertFalse(repository.existsByEmail(Email.of("anna.schmidt@stadt.de")));
    }

    @Test
    @DisplayName("die von der Domain erzeugte UUID wird unverändert gespeichert")
    void uuidWirdGespeichert() {
        Bearbeiter b = neuerBearbeiter("uuid@stadt.de");

        repository.save(b);

        assertEquals(b.getId(), repository.findById(b.getId()).orElseThrow().getId());
    }

    @Test
    @DisplayName("mehrere Bearbeiter erhalten unterschiedliche Ids")
    void unterschiedlicheIds() {
        Bearbeiter a = neuerBearbeiter("a@stadt.de");
        Bearbeiter b = neuerBearbeiter("b@stadt.de");
        repository.save(a);
        repository.save(b);

        assertNotEquals(a.getId(), b.getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    @DisplayName("gespeicherter Bearbeiter kann mit allen Werten wieder geladen werden")
    void speichernUndLaden() {
        Bearbeiter b = neuerBearbeiter("max.muster@stadt.de");

        repository.save(b);
        Optional<Bearbeiter> geladen = repository.findById(b.getId());

        assertTrue(geladen.isPresent());
        assertEquals(b, geladen.get());
        assertEquals("Muster", geladen.get().getNachname().value());
        assertEquals("Max", geladen.get().getVorname().value());
        assertEquals("max.muster@stadt.de", geladen.get().getEmail().value());
        assertEquals("Bauamt", geladen.get().getAbteilung().name());
    }

    @Test
    @DisplayName("Änderungen an einem Bearbeiter werden gespeichert")
    void aenderungWirdGespeichert() {
        Bearbeiter b = neuerBearbeiter("aenderung@stadt.de");
        repository.save(b);

        b.wechsleAbteilung(Abteilung.of("Umweltamt"));
        repository.save(b);

        assertEquals("Umweltamt", repository.findById(b.getId()).orElseThrow().getAbteilung().name());
    }

    @Test
    @DisplayName("gespeicherter Bearbeiter erscheint in findAll")
    void findAllEnthaeltNeuenBearbeiter() {
        int vorher = repository.findAll().size();
        Bearbeiter b = neuerBearbeiter("neu@stadt.de");

        repository.save(b);

        List<Bearbeiter> nachher = repository.findAll();
        assertEquals(vorher + 1, nachher.size());
        assertTrue(nachher.contains(b));
    }

    @Test
    @DisplayName("findById liefert leeres Optional für unbekannte Id")
    void findByIdUnbekannt() {
        assertTrue(repository.findById(Id.neu()).isEmpty());
    }

    @Test
    @DisplayName("existsByEmail erkennt vorhandene und unbekannte Adressen")
    void existsByEmail() {
        repository.save(neuerBearbeiter("vorhanden@stadt.de"));

        assertTrue(repository.existsByEmail(Email.of("vorhanden@stadt.de")));
        assertTrue(repository.existsByEmail(Email.of(" Vorhanden@Stadt.DE ")));
        assertFalse(repository.existsByEmail(Email.of("unbekannt@stadt.de")));
    }
}
