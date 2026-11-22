package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Embeddable für ein Kommentar-Value-Object innerhalb einer Meldung.
 */
@Embeddable
public class KommentarJpaEmbeddable {

    @Column(name = "ersteller_id", nullable = false)
    private UUID erstellerId;

    @Column(nullable = false, length = 255)
    private String betreff;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "erstell_zeitpunkt", nullable = false)
    private LocalDateTime erstellZeitpunkt;

    /** Für JPA. */
    protected KommentarJpaEmbeddable() {
    }

    public KommentarJpaEmbeddable(UUID erstellerId, String betreff, String text, LocalDateTime erstellZeitpunkt) {
        this.erstellerId = erstellerId;
        this.betreff = betreff;
        this.text = text;
        this.erstellZeitpunkt = erstellZeitpunkt;
    }

    public UUID getErstellerId() {
        return erstellerId;
    }

    public String getBetreff() {
        return betreff;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getErstellZeitpunkt() {
        return erstellZeitpunkt;
    }
}
