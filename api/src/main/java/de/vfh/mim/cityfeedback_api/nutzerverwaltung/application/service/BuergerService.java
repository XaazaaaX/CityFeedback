package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.AendereBuergerCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command.LegeBuergerAnCommand;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BuergerNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Anwendungsservice für Bürger.
 * <p>
 * Steuert die Anwendungsfälle (Transaktion, Laden, Speichern) und prüft die
 * anwendungsweite Regel "E-Mail nur einmal vergeben". Die Regeln einzelner
 * Bürger (Namen, E-Mail-Format, ...) prüft die Domain. Er kennt keine Schnittstellen-Technik:
 * Eingaben kommen als Commands, Ausgaben sind Domain-Objekte.
 */
@Service
@Transactional
public class BuergerService {

    private final BuergerRepository repository;

    public BuergerService(BuergerRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Buerger> alleBuerger() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Buerger findeBuerger(UUID id) {
        return ladeBuergerBeiId(id);
    }

    public Buerger legeBuergerAn(LegeBuergerAnCommand command) {
        Email email = Email.of(command.email());
        if (repository.existsByEmail(email)) {
            throw new EmailBereitsVergebenException(email);
        }

        Buerger buerger = Buerger.erstelleBuerger(
                command.nachname(),
                command.vorname(),
                email.value());
        repository.save(buerger);
        return buerger;
    }

    public Buerger aendereBuerger(AendereBuergerCommand command) {
        Buerger buerger = ladeBuergerBeiId(command.id());

        // erst alle neuen Werte bilden (und damit prüfen), dann ändern
        Vorname vorname = Vorname.of(command.vorname());
        Nachname nachname = Nachname.of(command.nachname());
        Email email = Email.of(command.email());

        // die eigene, unveränderte E-Mail ist kein Konflikt
        if (!email.equals(buerger.getEmail()) && repository.existsByEmail(email)) {
            throw new EmailBereitsVergebenException(email);
        }

        buerger.aendereName(vorname, nachname);
        buerger.aendereEmail(email);
        repository.save(buerger);
        return buerger;
    }

    private Buerger ladeBuergerBeiId(UUID id) {
        return repository.findById(Id.of(id))
                .orElseThrow(() -> new BuergerNichtGefundenException(id));
    }
}
