package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kategorie;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Kommentar;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Meldung;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.MeldungInhalt;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung.Status;
import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;

import java.util.List;

/**
 * Wandelt zwischen der Domain-Entität {@link Meldung} und dem
 * Persistenzmodell {@link MeldungJpaEntity} um.
 */
final class MeldungMapper {

    private MeldungMapper() {
    }

    static MeldungJpaEntity toJpaEntity(Meldung meldung) {
        List<KommentarJpaEmbeddable> kommentarEntities = meldung.getKommentare().stream()
                .map(k -> new KommentarJpaEmbeddable(
                        k.erstellerId(),
                        k.betreff(),
                        k.text(),
                        k.erstellZeitpunkt()))
                .toList();

        return new MeldungJpaEntity(
                meldung.getId().value(),
                meldung.getMelderId(),
                meldung.getBearbeiterId(),
                meldung.getKategorie().name(),
                meldung.getStatus().name(),
                meldung.getInhalt().betreff(),
                meldung.getInhalt().text(),
                meldung.getInhalt().erstellZeitpunkt(),
                kommentarEntities);
    }

    static Meldung toDomain(MeldungJpaEntity entity) {
        MeldungInhalt inhalt = MeldungInhalt.rekonstruiere(
                entity.getBetreff(),
                entity.getText(),
                entity.getErstellZeitpunkt());

        List<Kommentar> kommentare = entity.getKommentare().stream()
                .map(k -> Kommentar.rekonstruiere(
                        k.getBetreff(),
                        k.getText(),
                        k.getErstellZeitpunkt(),
                        k.getErstellerId()))
                .toList();

        return Meldung.rekonstruiere(
                Id.of(entity.getId()),
                inhalt,
                Kategorie.valueOf(entity.getKategorie()),
                Status.valueOf(entity.getStatus()),
                entity.getMelderId(),
                entity.getBearbeiterId(),
                kommentare);
    }
}
