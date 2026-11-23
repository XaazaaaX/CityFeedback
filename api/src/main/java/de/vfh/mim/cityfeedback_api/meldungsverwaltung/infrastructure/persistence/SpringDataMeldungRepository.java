package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring-Data-Repository für {@link MeldungJpaEntity}.
 */
interface SpringDataMeldungRepository extends JpaRepository<MeldungJpaEntity, UUID> {

    List<MeldungJpaEntity> findByMelderId(UUID melderId);
}
