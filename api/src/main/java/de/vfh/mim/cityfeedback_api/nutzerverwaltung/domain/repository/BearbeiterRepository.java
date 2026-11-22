package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;

import java.util.List;
import java.util.Optional;

/**
 * Repository für Bearbeiter (Domain-Layer).
 * <p>
 * Die Schnittstelle ist frei von Spring- und JPA-Abhängigkeiten und arbeitet
 * ausschließlich mit Domain-Objekten. Die technische Umsetzung liegt im
 * Infrastructure-Layer.
 */
public interface BearbeiterRepository {

    /**
     * Speichert einen neuen oder geänderten Bearbeiter.
     */
    void save(Bearbeiter bearbeiter);

    Optional<Bearbeiter> findById(Id id);

    List<Bearbeiter> findAll();

    boolean existsByEmail(Email email);
}
