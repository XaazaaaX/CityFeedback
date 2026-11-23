package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.repository.MeldungRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Umsetzung des {@link MeldungRepository} mit Spring Data JPA.
 */
@Repository
public class JpaMeldungRepository implements MeldungRepository {

    private final SpringDataMeldungRepository jpaRepository;

    JpaMeldungRepository(SpringDataMeldungRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Meldung meldung) {
        jpaRepository.save(MeldungMapper.toJpaEntity(meldung));
    }

    @Override
    public Optional<Meldung> findById(Id id) {
        return jpaRepository.findById(id.value()).map(MeldungMapper::toDomain);
    }

    @Override
    public List<Meldung> findAll() {
        return jpaRepository.findAll().stream()
                .map(MeldungMapper::toDomain)
                .toList();
    }

    @Override
    public List<Meldung> findByMelderId(UUID melderId) {
        return jpaRepository.findByMelderId(melderId).stream()
                .map(MeldungMapper::toDomain)
                .toList();
    }
}
