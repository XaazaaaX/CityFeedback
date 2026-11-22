package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBearbeiterCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBearbeiterAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BearbeiterNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BearbeiterService (Unit, Repository im Speicher)")
class BearbeiterServiceTest {

    /** Einfaches Repository im Speicher, nur für diesen Test. */
    private static class RepositoryImSpeicher implements BearbeiterRepository {
        private final List<Bearbeiter> bearbeiter = new ArrayList<>();

        @Override
        public void save(Bearbeiter b) {
            bearbeiter.remove(b);
            bearbeiter.add(b);
        }

        @Override
        public Optional<Bearbeiter> findById(Id id) {
            return bearbeiter.stream().filter(b -> b.getId().equals(id)).findFirst();
        }

        @Override
        public List<Bearbeiter> findAll() {
            return List.copyOf(bearbeiter);
        }

        @Override
        public boolean existsByEmail(Email email) {
            return bearbeiter.stream().anyMatch(b -> b.getEmail().equals(email));
        }
    }

    private RepositoryImSpeicher repository;
    private BearbeiterService service;
    private Bearbeiter anna;

    @BeforeEach
    void setUp() {
        repository = new RepositoryImSpeicher();
        service = new BearbeiterService(repository);
        anna = service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Schmidt", "Anna", "anna@stadt.de", "Tiefbauamt"));
    }

    private static AendereBearbeiterCommand aendern(Bearbeiter b, String email) {
        return new AendereBearbeiterCommand(b.getId().value(), "Neu", "Name", email, "Umweltamt");
    }

    // --- Anlegen --------------------------------------------------------------

    @Test
    @DisplayName("legt Bearbeiter an und speichert ihn")
    void anlegen() {
        Bearbeiter b = service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Muster", "Max", " Max@Stadt.DE ", "Bauamt"));

        assertEquals("max@stadt.de", b.getEmail().value());
        assertEquals(b, repository.findById(b.getId()).orElseThrow());
        assertEquals(2, service.alleBearbeiter().size());
    }

    @Test
    @DisplayName("neuer Bearbeiter mit vergebener E-Mail wird abgelehnt (auch anders geschrieben)")
    void anlegenEmailVergeben() {
        EmailBereitsVergebenException ex = assertThrows(EmailBereitsVergebenException.class,
                () -> service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Doppelt", "Anna", " ANNA@Stadt.de ", "Bauamt")));

        assertTrue(ex.getMessage().contains("anna@stadt.de"));
        assertEquals(1, service.alleBearbeiter().size());
    }

    @Test
    @DisplayName("ungültige Eingabe wird von der Domain abgelehnt, nichts wird gespeichert")
    void anlegenUngueltig() {
        assertThrows(IllegalArgumentException.class,
                () -> service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Muster", "Max", "keine-email", "Bauamt")));

        assertEquals(1, service.alleBearbeiter().size());
    }

    // --- Ändern ---------------------------------------------------------------

    @Test
    @DisplayName("eigene, unveränderte E-Mail ist bei Änderung kein Konflikt")
    void aendernEigeneEmail() {
        Bearbeiter geaendert = assertDoesNotThrow(() -> service.aendereBearbeiter(aendern(anna, "Anna@Stadt.de")));

        assertEquals("Neu", geaendert.getNachname().value());
        assertEquals("anna@stadt.de", geaendert.getEmail().value());
    }

    @Test
    @DisplayName("Änderung auf freie E-Mail ist erlaubt")
    void aendernFreieEmail() {
        Bearbeiter geaendert = service.aendereBearbeiter(aendern(anna, "anna.neu@stadt.de"));

        assertEquals("anna.neu@stadt.de", geaendert.getEmail().value());
        assertFalse(repository.existsByEmail(Email.of("anna@stadt.de")));
    }

    @Test
    @DisplayName("Änderung auf E-Mail eines anderen Bearbeiters wird abgelehnt, nichts wird geändert")
    void aendernEmailVergeben() {
        Bearbeiter jonas = service.legeBearbeiterAn(new LegeBearbeiterAnCommand("Müller", "Jonas", "jonas@stadt.de", "Umweltamt"));

        assertThrows(EmailBereitsVergebenException.class, () -> service.aendereBearbeiter(aendern(jonas, "anna@stadt.de")));

        Bearbeiter unveraendert = service.findeBearbeiter(jonas.getId().value());
        assertEquals("Müller", unveraendert.getNachname().value());
        assertEquals("jonas@stadt.de", unveraendert.getEmail().value());
    }

    @Test
    @DisplayName("ungültige neue Werte ändern nichts")
    void aendernUngueltig() {
        assertThrows(IllegalArgumentException.class, () -> service.aendereBearbeiter(aendern(anna, "keine-email")));

        Bearbeiter unveraendert = service.findeBearbeiter(anna.getId().value());
        assertEquals("Schmidt", unveraendert.getNachname().value());
        assertEquals("Tiefbauamt", unveraendert.getAbteilung().name());
    }

    // --- Lesen ----------------------------------------------------------------

    @Test
    @DisplayName("unbekannte Id führt zu BearbeiterNichtGefundenException")
    void unbekannt() {
        UUID unbekannt = UUID.randomUUID();

        assertThrows(BearbeiterNichtGefundenException.class, () -> service.findeBearbeiter(unbekannt));
        assertThrows(BearbeiterNichtGefundenException.class,
                () -> service.aendereBearbeiter(new AendereBearbeiterCommand(unbekannt, "A", "B", "c@stadt.de", "D")));
    }
}
