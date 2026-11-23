package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.service;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.AendereMeldungStatusCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.FuegeKommentarHinzuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.ReicheMeldungEinCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.command.WeiseBearbeiterZuCommand;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.exception.MeldungNichtGefundenException;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Anwendungsservice für Meldungen (Bounded Context: Meldungsverwaltung).
 * <p>
 * Steuert die Anwendungsfälle Einreichen, Bearbeiter-Zuweisung, Kommentieren
 * und Statuswechsel.
 */
@Service
@Transactional
public class MeldungService {

    private final MeldungRepository repository;

    public MeldungService(MeldungRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Meldung> alleMeldungen() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Meldung findeMeldung(UUID id) {
        return ladeMeldungBeiId(id);
    }

    @Transactional(readOnly = true)
    public List<Meldung> findeMeldungenZuBuerger(UUID melderId) {
        return repository.findByMelderId(melderId);
    }

    public Meldung reicheMeldungEin(ReicheMeldungEinCommand command) {
        Meldung meldung = Meldung.erstelleMeldung(
                command.melderId(),
                command.kategorie(),
                command.betreff(),
                command.text());
        repository.save(meldung);
        return meldung;
    }

    public Meldung weiseBearbeiterZu(WeiseBearbeiterZuCommand command) {
        Meldung meldung = ladeMeldungBeiId(command.meldungId());
        meldung.bearbeiterZuweisen(command.bearbeiterId());
        repository.save(meldung);
        return meldung;
    }

    public Meldung fuegeKommentarHinzu(FuegeKommentarHinzuCommand command) {
        Meldung meldung = ladeMeldungBeiId(command.meldungId());
        meldung.kommentieren(command.bearbeiterId(), command.betreff(), command.text());
        repository.save(meldung);
        return meldung;
    }

    public Meldung aendereStatus(AendereMeldungStatusCommand command) {
        Meldung meldung = ladeMeldungBeiId(command.meldungId());
        if (command.neuerStatus() == Status.ERLEDIGT) {
            meldung.erledigen();
        } else if (command.neuerStatus() == Status.ABGELEHNT) {
            meldung.ablehnen();
        } else {
            throw new IllegalArgumentException("Direkte Statusänderung auf " + command.neuerStatus()
                    + " ist nicht vorgesehen. Bitte entsprechende Fachmethode nutzen.");
        }
        repository.save(meldung);
        return meldung;
    }

    private Meldung ladeMeldungBeiId(UUID id) {
        return repository.findById(Id.of(id))
                .orElseThrow(() -> new MeldungNichtGefundenException(id));
    }
}
