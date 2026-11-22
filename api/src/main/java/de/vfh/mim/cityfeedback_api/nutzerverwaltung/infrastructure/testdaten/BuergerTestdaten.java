package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Legt beim Start einige Test-Bürger in der In-Memory-Datenbank an,
 * <b>nur im Profil {@code dev}</b> (Standardprofil, siehe application.properties).
 * In Tests (Profil {@code test}) und später in Produktion werden keine Testdaten geladen.
 * <p>
 * Die Bürger werden über die Domain-Factory erzeugt und über das
 * Domain-Repository gespeichert, durchlaufen also dieselben Regeln wie
 * fachlich angelegte Bürger. Ist die Datenbank nicht leer, passiert nichts.
 */
@Component
@Profile("dev")
class BuergerTestdaten implements ApplicationRunner {

    private final BuergerRepository repository;

    BuergerTestdaten(BuergerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repository.findAll().isEmpty()) {
            return;
        }
        anlegen("Mustermann", "Max", "max.mustermann@example.com");
        anlegen("Musterfrau", "Erika", "erika.musterfrau@example.com");
        anlegen("Schneider", "Felix", "felix.schneider@example.com");
        anlegen("Fischer", "Laura", "laura.fischer@example.com");
        anlegen("Weber", "Simon", "simon.weber@example.com");
    }

    private void anlegen(String nachname, String vorname, String email) {
        repository.save(Buerger.erstelleBuerger(nachname, vorname, email));
    }
}
