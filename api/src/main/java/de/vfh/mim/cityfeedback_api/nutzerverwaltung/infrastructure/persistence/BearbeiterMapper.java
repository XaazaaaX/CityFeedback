package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Abteilung;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter.Bearbeiter;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;

/**
 * Wandelt zwischen der Domain-Entität {@link Bearbeiter} und dem
 * Persistenzmodell {@link BearbeiterJpaEntity} um.
 */
final class BearbeiterMapper {

    private BearbeiterMapper() {
    }

    static BearbeiterJpaEntity toJpaEntity(Bearbeiter bearbeiter) {
        return new BearbeiterJpaEntity(
                bearbeiter.getId().value(),
                bearbeiter.getNachname().value(),
                bearbeiter.getVorname().value(),
                bearbeiter.getEmail().value(),
                bearbeiter.getAbteilung().name());
    }

    /**
     * Stellt die Domain-Entität aus den gespeicherten Daten wieder her.
     * Die Value Objects prüfen dabei weiterhin ihre Regeln.
     */
    static Bearbeiter toDomain(BearbeiterJpaEntity entity) {
        return Bearbeiter.rekonstruiere(
                Id.of(entity.getId()),
                Nachname.of(entity.getNachname()),
                Vorname.of(entity.getVorname()),
                Email.of(entity.getEmail()),
                Abteilung.of(entity.getAbteilung()));
    }
}
