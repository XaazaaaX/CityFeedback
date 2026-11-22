package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("Bürger-Testdaten im Profil dev")
class BuergerTestdatenTest {

    @Autowired
    private BuergerRepository repository;

    @Test
    @DisplayName("fünf Test-Bürger sind vorhanden")
    void testdatenGeladen() {
        assertEquals(5, repository.findAll().size());
        assertTrue(repository.existsByEmail(Email.of("max.mustermann@example.com")));
        assertTrue(repository.existsByEmail(Email.of("simon.weber@example.com")));
    }

    @Test
    @DisplayName("erneuter Start legt keine doppelten Testdaten an")
    void keineDoppelten(@Autowired BuergerTestdaten testdaten) throws Exception {
        testdaten.run(null);

        assertEquals(5, repository.findAll().size());
    }
}
