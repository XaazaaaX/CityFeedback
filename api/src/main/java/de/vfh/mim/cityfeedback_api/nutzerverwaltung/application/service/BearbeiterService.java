package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBearbeiterCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBearbeiterAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BearbeiterNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Abteilung;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Anwendungsservice für Bearbeiter.
 * <p>
 * Steuert die Anwendungsfälle (Transaktion, Laden, Speichern) und prüft die
 * anwendungsweite Regel "E-Mail nur einmal vergeben". Die Regeln einzelner
 * Bearbeiter (Namen, E-Mail-Format, ...) prüft die Domain. Er kennt keine Schnittstellen-Technik:
 * Eingaben kommen als Commands, Ausgaben sind Domain-Objekte.
 */
@Service
@Transactional
public class BearbeiterService {

    private final BearbeiterRepository repository;

    public BearbeiterService(BearbeiterRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Bearbeiter> alleBearbeiter() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Bearbeiter findeBearbeiter(UUID id) {
        return ladeBearbeiterBeiId(id);
    }

    public Bearbeiter legeBearbeiterAn(LegeBearbeiterAnCommand command) {
        Email email = Email.of(command.email());
        if (repository.existsByEmail(email)) {
            throw new EmailBereitsVergebenException(email);
        }

        Bearbeiter bearbeiter = Bearbeiter.erstelleBearbeiter(
                command.nachname(),
                command.vorname(),
                email.value(),
                command.abteilung());
        repository.save(bearbeiter);
        return bearbeiter;
    }

    public Bearbeiter aendereBearbeiter(AendereBearbeiterCommand command) {
        Bearbeiter bearbeiter = ladeBearbeiterBeiId(command.id());

        // erst alle neuen Werte bilden (und damit prüfen), dann ändern
        Vorname vorname = Vorname.of(command.vorname());
        Nachname nachname = Nachname.of(command.nachname());
        Email email = Email.of(command.email());
        Abteilung abteilung = Abteilung.of(command.abteilung());
        // die eigene, unveränderte E-Mail ist kein Konflikt
        if (!email.equals(bearbeiter.getEmail()) && repository.existsByEmail(email)) {
            throw new EmailBereitsVergebenException(email);
        }

        bearbeiter.aendereName(vorname, nachname);
        bearbeiter.aendereEmail(email);
        bearbeiter.wechsleAbteilung(abteilung);
        repository.save(bearbeiter);
        return bearbeiter;
    }

    private Bearbeiter ladeBearbeiterBeiId(UUID id) {
        return repository.findById(Id.of(id))
                .orElseThrow(() -> new BearbeiterNichtGefundenException(id));
    }
}
