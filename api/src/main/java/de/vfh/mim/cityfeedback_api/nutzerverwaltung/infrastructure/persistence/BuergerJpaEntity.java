package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA-Abbildung eines Bürgers auf die Tabelle {@code buerger}.
 * <p>
 * Reines Persistenzmodell ohne fachliche Logik. Die Umwandlung von und in
 * die Domain-Entität übernimmt der {@link BuergerMapper}.
 */
@Entity
@Table(name = "buerger")
public class BuergerJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nachname;

    @Column(nullable = false, length = 100)
    private String vorname;

    @Column(nullable = false, unique = true)
    private String email;

    /** Für JPA. */
    protected BuergerJpaEntity() {
    }

    public BuergerJpaEntity(UUID id, String nachname, String vorname, String email) {
        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getNachname() {
        return nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public String getEmail() {
        return email;
    }
}
