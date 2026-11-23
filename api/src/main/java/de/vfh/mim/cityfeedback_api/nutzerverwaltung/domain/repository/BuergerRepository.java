package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository;

import java.util.List;
import java.util.Optional;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;

/**
 * Repository für Buerger (Domain-Layer).
 * <p>
 * Die Schnittstelle ist frei von Spring- und JPA-Abhängigkeiten und arbeitet
 * ausschließlich mit Domain-Objekten. Die technische Umsetzung liegt im
 * Infrastructure-Layer.
 */
public interface BuergerRepository {

    /**
     * Speichert einen neuen oder geänderten Buerger.
     */
    void save(Buerger buerger);

    Optional<Buerger> findById(Id id);

    List<Buerger> findAll();

    boolean existsByEmail(Email email);
}
