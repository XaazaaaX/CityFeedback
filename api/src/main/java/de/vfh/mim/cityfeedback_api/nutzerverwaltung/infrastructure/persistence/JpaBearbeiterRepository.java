package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BearbeiterRepository;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Umsetzung des {@link BearbeiterRepository} mit Spring Data JPA.
 * <p>
 * Aktuell gegen eine H2-In-Memory-Datenbank; ein Wechsel auf eine andere
 * Datenbank erfordert nur Konfiguration. Die Ids erzeugt die Domain (UUID).
 */
@Repository
public class JpaBearbeiterRepository implements BearbeiterRepository {

    private final SpringDataBearbeiterRepository jpaRepository;

    JpaBearbeiterRepository(SpringDataBearbeiterRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Bearbeiter bearbeiter) {
        jpaRepository.save(BearbeiterMapper.toJpaEntity(bearbeiter));
    }

    @Override
    public Optional<Bearbeiter> findById(Id id) {
        return jpaRepository.findById(id.value()).map(BearbeiterMapper::toDomain);
    }

    @Override
    public List<Bearbeiter> findAll() {
        return jpaRepository.findAll().stream()
                .map(BearbeiterMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
