package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger.Buerger;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;

/**
 * Wandelt zwischen der Domain-Entität {@link Buerger} und dem
 * Persistenzmodell {@link BuergerJpaEntity} um.
 */
final class BuergerMapper {

    private BuergerMapper() {
    }

    static BuergerJpaEntity toJpaEntity(Buerger buerger) {
        return new BuergerJpaEntity(
                buerger.getId().value(),
                buerger.getNachname().value(),
                buerger.getVorname().value(),
                buerger.getEmail().value());
    }

    /**
     * Stellt die Domain-Entität aus den gespeicherten Daten wieder her.
     * Die Value Objects prüfen dabei weiterhin ihre Regeln.
     */
    static Buerger toDomain(BuergerJpaEntity entity) {
        return Buerger.rekonstruiere(
                Id.of(entity.getId()),
                Nachname.of(entity.getNachname()),
                Vorname.of(entity.getVorname()),
                Email.of(entity.getEmail()));
    }
}
