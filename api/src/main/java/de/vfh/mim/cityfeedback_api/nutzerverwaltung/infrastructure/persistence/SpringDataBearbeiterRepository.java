package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring-Data-Repository für {@link BearbeiterJpaEntity}.
 * <p>
 * Wird nur innerhalb des Infrastructure-Layers verwendet.
 */
interface SpringDataBearbeiterRepository extends JpaRepository<BearbeiterJpaEntity, UUID> {

    boolean existsByEmail(String email);
}
