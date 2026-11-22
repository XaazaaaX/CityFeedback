package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Legt beim Start einige Test-Meldungen in der In-Memory-Datenbank an,
 * <b>nur im Profil {@code dev}</b>.
 */
@Component
@Profile("dev")
class MeldungTestdaten implements ApplicationRunner {

    private final MeldungRepository repository;

    // Feste UUIDs für reproduzierbare Testdaten
    public static final UUID TEST_BUERGER_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID TEST_BUERGER_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID TEST_BEARBEITER = UUID.fromString("33333333-3333-3333-3333-333333333333");

    MeldungTestdaten(MeldungRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repository.findAll().isEmpty()) {
            return;
        }

        // 1. Offene Meldung
        Meldung m1 = Meldung.erstelleMeldung(
                TEST_BUERGER_1,
                Kategorie.INFRASTRUKTUR,
                "Straßenlaterne defekt",
                "Laterne vor Haus Nr. 12 flackert und geht immer wieder aus.");
        repository.save(m1);

        // 2. Meldung in Bearbeitung
        Meldung m2 = Meldung.erstelleMeldung(
                TEST_BUERGER_1,
                Kategorie.VERKEHR,
                "Schlagloch in der Hauptstraße",
                "Tiefes Schlagloch auf Höhe der Bushaltestelle.");
        m2.bearbeiterZuweisen(TEST_BEARBEITER);
        repository.save(m2);

        // 3. Kommentierte Meldung
        Meldung m3 = Meldung.erstelleMeldung(
                TEST_BUERGER_2,
                Kategorie.UMWELT,
                "Wilder Müll im Stadtpark",
                "Mehrere Säcke Bauschutt am Waldrand abgeladen.");
        m3.bearbeiterZuweisen(TEST_BEARBEITER);
        m3.kommentieren(TEST_BEARBEITER, "Auftrag erteilt", "Bauhof wurde zur Entsorgung beauftragt.");
        repository.save(m3);
    }
}
