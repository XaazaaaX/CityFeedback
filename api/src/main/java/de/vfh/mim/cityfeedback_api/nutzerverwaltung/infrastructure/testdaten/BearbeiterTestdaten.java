package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.testdaten;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Legt beim Start einige Test-Bearbeiter in der In-Memory-Datenbank an,
 * <b>nur im Profil {@code dev}</b> (Standardprofil, siehe application.properties).
 * In Tests (Profil {@code test}) und später in Produktion werden keine Testdaten geladen.
 * <p>
 * Die Bearbeiter werden über die Domain-Factory erzeugt und über das
 * Domain-Repository gespeichert, durchlaufen also dieselben Regeln wie
 * fachlich angelegte Bearbeiter. Ist die Datenbank nicht leer, passiert nichts.
 */
@Component
@Profile("dev")
class BearbeiterTestdaten implements ApplicationRunner {

    private final BearbeiterRepository repository;

    BearbeiterTestdaten(BearbeiterRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!repository.findAll().isEmpty()) {
            return;
        }
        anlegen("Schmidt", "Anna", "anna.schmidt@stadt.de", "Tiefbauamt");
        anlegen("Müller", "Jonas", "jonas.mueller@stadt.de", "Umweltamt");
        anlegen("Yilmaz", "Elif", "elif.yilmaz@stadt.de", "Straßenverkehrsamt");
        anlegen("Becker", "Lukas", "lukas.becker@stadt.de", "Grünflächenamt");
        anlegen("Wagner", "Sophie", "sophie.wagner@stadt.de", "Ordnungsamt");
    }

    private void anlegen(String nachname, String vorname, String email, String abteilung) {
        repository.save(Bearbeiter.erstelleBearbeiter(nachname, vorname, email, abteilung));
    }
}
