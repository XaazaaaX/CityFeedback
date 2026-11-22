package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.repository.BuergerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Umsetzung des {@link BuergerRepository} mit Spring Data JPA.
 * <p>
 * Aktuell gegen eine H2-In-Memory-Datenbank; ein Wechsel auf eine andere
 * Datenbank erfordert nur Konfiguration. Die Ids erzeugt die Domain (UUID).
 */
@Repository
public class JpaBuergerRepository implements BuergerRepository {

    private final SpringDataBuergerRepository jpaRepository;

    JpaBuergerRepository(SpringDataBuergerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Buerger buerger) {
        jpaRepository.save(BuergerMapper.toJpaEntity(buerger));
    }

    @Override
    public Optional<Buerger> findById(Id id) {
        return jpaRepository.findById(id.value()).map(BuergerMapper::toDomain);
    }

    @Override
    public List<Buerger> findAll() {
        return jpaRepository.findAll().stream()
                .map(BuergerMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
