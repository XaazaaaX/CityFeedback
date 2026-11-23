package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBuergerCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBuergerAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BuergerNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BuergerService (Unit, Repository im Speicher)")
class BuergerServiceTest {

    private static class RepositoryImSpeicher implements BuergerRepository {
        private final List<Buerger> buergerList = new ArrayList<>();

        @Override
        public void save(Buerger b) {
            buergerList.remove(b);
            buergerList.add(b);
        }

        @Override
        public Optional<Buerger> findById(Id id) {
            return buergerList.stream().filter(b -> b.getId().equals(id)).findFirst();
        }

        @Override
        public List<Buerger> findAll() {
            return List.copyOf(buergerList);
        }

        @Override
        public boolean existsByEmail(Email email) {
            return buergerList.stream().anyMatch(b -> b.getEmail().equals(email));
        }
    }

    private RepositoryImSpeicher repository;
    private BuergerService service;
    private Buerger max;

    @BeforeEach
    void setUp() {
        repository = new RepositoryImSpeicher();
        service = new BuergerService(repository);
        max = service.legeBuergerAn(new LegeBuergerAnCommand("Mustermann", "Max", "max@example.com"));
    }

    @Test
    @DisplayName("alleBuerger liefert alle gespeicherten Bürger")
    void alleBuerger() {
        List<Buerger> alle = service.alleBuerger();

        assertEquals(1, alle.size());
        assertEquals(max, alle.getFirst());
    }

    @Test
    @DisplayName("findeBuerger findet existierenden Bürger")
    void findeBuergerGefunden() {
        Buerger gefunden = service.findeBuerger(max.getId().value());

        assertEquals(max, gefunden);
    }

    @Test
    @DisplayName("findeBuerger wirft Exception wenn Bürger nicht existiert")
    void findeBuergerNichtGefunden() {
        UUID unbekannt = UUID.randomUUID();

        assertThrows(BuergerNichtGefundenException.class, () -> service.findeBuerger(unbekannt));
    }

    @Test
    @DisplayName("legeBuergerAn speichert neuen Bürger")
    void legeBuergerAnErfolgreich() {
        Buerger erika = service.legeBuergerAn(new LegeBuergerAnCommand("Musterfrau", "Erika", "erika@example.com"));

        assertNotNull(erika.getId());
        assertEquals("Erika", erika.getVorname().value());
        assertEquals("Musterfrau", erika.getNachname().value());
        assertEquals("erika@example.com", erika.getEmail().value());
        assertEquals(2, service.alleBuerger().size());
    }

    @Test
    @DisplayName("legeBuergerAn lehnt bereits vergebene E-Mail ab")
    void legeBuergerAnEmailVergeben() {
        LegeBuergerAnCommand command = new LegeBuergerAnCommand("Anderer", "Name", "max@example.com");

        assertThrows(EmailBereitsVergebenException.class, () -> service.legeBuergerAn(command));
        assertEquals(1, service.alleBuerger().size());
    }

    @Test
    @DisplayName("aendereBuerger ändert Name und E-Mail")
    void aendereBuergerErfolgreich() {
        Buerger geaendert = service.aendereBuerger(new AendereBuergerCommand(
                max.getId().value(), "Neu", "Moritz", "moritz@example.com"));

        assertEquals("Moritz", geaendert.getVorname().value());
        assertEquals("Neu", geaendert.getNachname().value());
        assertEquals("moritz@example.com", geaendert.getEmail().value());
    }

    @Test
    @DisplayName("aendereBuerger mit unveränderter E-Mail wirft keine Exception")
    void aendereBuergerGleicheEmailErlaubt() {
        Buerger geaendert = service.aendereBuerger(new AendereBuergerCommand(
                max.getId().value(), "Neu", "Moritz", "max@example.com"));

        assertEquals("max@example.com", geaendert.getEmail().value());
    }

    @Test
    @DisplayName("aendereBuerger mit E-Mail eines anderen Bürgers wird abgelehnt")
    void aendereBuergerEmailAndererVergeben() {
        service.legeBuergerAn(new LegeBuergerAnCommand("Musterfrau", "Erika", "erika@example.com"));

        AendereBuergerCommand command = new AendereBuergerCommand(
                max.getId().value(), "Mustermann", "Max", "erika@example.com");

        assertThrows(EmailBereitsVergebenException.class, () -> service.aendereBuerger(command));
    }
}
