package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository-Schnittstelle für das Meldungsaggregat.
 * <p>
 * Lädt und speichert das vollständige Meldungsaggregat einschließlich
 * Inhalt und Kommentaren.
 */
public interface MeldungRepository {

    void save(Meldung meldung);

    Optional<Meldung> findById(Id id);

    List<Meldung> findAll();

    /**
     * Findet alle Meldungen eines Bürgers anhand seiner UUID.
     */
    List<Meldung> findByMelderId(UUID melderId);
}
