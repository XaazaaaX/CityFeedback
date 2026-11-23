package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("JpaBuergerRepository (H2)")
class JpaBuergerRepositoryTest {

    @Autowired
    private BuergerRepository repository;

    private Buerger neuerBuerger(String email) {
        return Buerger.erstelleBuerger("Muster", "Max", email);
    }

    @Test
    @DisplayName("im Profil test sind keine Testdaten geladen")
    void keineTestdaten() {
        List<Buerger> alle = repository.findAll();

        assertTrue(alle.isEmpty());
        assertFalse(repository.existsByEmail(Email.of("max.mustermann@example.com")));
    }

    @Test
    @DisplayName("die von der Domain erzeugte UUID wird unverändert gespeichert")
    void uuidWirdGespeichert() {
        Buerger b = neuerBuerger("uuid@example.com");

        repository.save(b);

        assertEquals(b.getId(), repository.findById(b.getId()).orElseThrow().getId());
    }

    @Test
    @DisplayName("mehrere Bürger erhalten unterschiedliche Ids")
    void unterschiedlicheIds() {
        Buerger a = neuerBuerger("a@example.com");
        Buerger b = neuerBuerger("b@example.com");
        repository.save(a);
        repository.save(b);

        assertNotEquals(a.getId(), b.getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    @DisplayName("gespeicherter Bürger kann mit allen Werten wieder geladen werden")
    void speichernUndLaden() {
        Buerger b = neuerBuerger("max.muster@example.com");

        repository.save(b);
        Optional<Buerger> geladen = repository.findById(b.getId());

        assertTrue(geladen.isPresent());
        assertEquals(b, geladen.get());
        assertEquals("Muster", geladen.get().getNachname().value());
        assertEquals("Max", geladen.get().getVorname().value());
        assertEquals("max.muster@example.com", geladen.get().getEmail().value());
    }

    @Test
    @DisplayName("Änderungen an einem Bürger werden gespeichert")
    void aenderungWirdGespeichert() {
        Buerger b = neuerBuerger("aenderung@example.com");
        repository.save(b);

        b.aendereName(Vorname.of("Moritz"), Nachname.of("Neumann"));
        repository.save(b);

        assertEquals("Moritz", repository.findById(b.getId()).orElseThrow().getVorname().value());
        assertEquals("Neumann", repository.findById(b.getId()).orElseThrow().getNachname().value());
    }

    @Test
    @DisplayName("gespeicherter Bürger erscheint in findAll")
    void findAllEnthaeltNeuenBuerger() {
        int vorher = repository.findAll().size();
        Buerger b = neuerBuerger("neu@example.com");
        repository.save(b);

        assertEquals(vorher + 1, repository.findAll().size());
        assertTrue(repository.findAll().contains(b));
    }

    @Test
    @DisplayName("existsByEmail liefert true für vorhandene und false für unbekannte E-Mails")
    void existsByEmail() {
        Buerger b = neuerBuerger("vorhanden@example.com");
        repository.save(b);

        assertTrue(repository.existsByEmail(Email.of("vorhanden@example.com")));
        assertTrue(repository.existsByEmail(Email.of(" VORHANDEN@EXAMPLE.COM ")));
        assertFalse(repository.existsByEmail(Email.of("unbekannt@example.com")));
    }
}
