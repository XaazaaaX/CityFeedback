package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prüft, dass die Testdaten im Profil {@code dev} geladen werden.
 * (Dass sie im Profil {@code test} fehlen, prüft JpaBearbeiterRepositoryTest.)
 */
@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("Testdaten im Profil dev")
class BearbeiterTestdatenTest {

    @Autowired
    private BearbeiterRepository repository;

    @Test
    @DisplayName("fünf Test-Bearbeiter sind vorhanden")
    void testdatenGeladen() {
        assertEquals(5, repository.findAll().size());
        assertTrue(repository.existsByEmail(Email.of("anna.schmidt@stadt.de")));
        assertTrue(repository.existsByEmail(Email.of("sophie.wagner@stadt.de")));
    }

    @Test
    @DisplayName("erneuter Start legt keine doppelten Testdaten an")
    void keineDoppelten(@Autowired BearbeiterTestdaten testdaten) throws Exception {
        testdaten.run(null);

        assertEquals(5, repository.findAll().size());
    }
}
