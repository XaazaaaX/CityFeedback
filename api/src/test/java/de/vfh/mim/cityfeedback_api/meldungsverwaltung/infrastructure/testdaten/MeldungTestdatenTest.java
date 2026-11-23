package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("MeldungTestdaten (dev-Profil)")
class MeldungTestdatenTest {

    @Autowired
    private MeldungRepository repository;

    @Test
    @DisplayName("beim Start im dev-Profil werden Testdaten geladen")
    void testdatenVorhanden() {
        assertFalse(repository.findAll().isEmpty(), "Im Profil 'dev' sollten Meldungen vorhanden sein");
    }
}
