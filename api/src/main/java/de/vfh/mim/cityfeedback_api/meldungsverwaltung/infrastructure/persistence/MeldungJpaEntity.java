package de.vfh.mim.cityfeedback_api.meldungsverwaltung.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA-Abbildung einer Meldung auf die Tabelle {@code meldung}.
 */
@Entity
@Table(name = "meldung")
public class MeldungJpaEntity {

    @Id
    private UUID id;

    @Column(name = "melder_id", nullable = false)
    private UUID melderId;

    @Column(name = "bearbeiter_id")
    private UUID bearbeiterId;

    @Column(nullable = false, length = 50)
    private String kategorie;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false, length = 255)
    private String betreff;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "erstell_zeitpunkt", nullable = false)
    private LocalDateTime erstellZeitpunkt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meldung_kommentare", joinColumns = @JoinColumn(name = "meldung_id"))
    private List<KommentarJpaEmbeddable> kommentare = new ArrayList<>();

    /** Für JPA. */
    protected MeldungJpaEntity() {
    }

    public MeldungJpaEntity(UUID id, UUID melderId, UUID bearbeiterId, String kategorie,
                            String status, String betreff, String text,
                            LocalDateTime erstellZeitpunkt, List<KommentarJpaEmbeddable> kommentare) {
        this.id = id;
        this.melderId = melderId;
        this.bearbeiterId = bearbeiterId;
        this.kategorie = kategorie;
        this.status = status;
        this.betreff = betreff;
        this.text = text;
        this.erstellZeitpunkt = erstellZeitpunkt;
        this.kommentare = kommentare != null ? new ArrayList<>(kommentare) : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public UUID getMelderId() {
        return melderId;
    }

    public UUID getBearbeiterId() {
        return bearbeiterId;
    }

    public String getKategorie() {
        return kategorie;
    }

    public String getStatus() {
        return status;
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

    public List<KommentarJpaEmbeddable> getKommentare() {
        return kommentare;
    }
}
