package de.vfh.mim.cityfeedback_api.nutzerverwaltung.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA-Abbildung eines Bearbeiters auf die Tabelle {@code bearbeiter}.
 * <p>
 * Reines Persistenzmodell ohne fachliche Logik. Die Umwandlung von und in
 * die Domain-Entität übernimmt der {@link BearbeiterMapper}.
 */
@Entity
@Table(name = "bearbeiter")
public class BearbeiterJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nachname;

    @Column(nullable = false, length = 100)
    private String vorname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String abteilung;

    /** Für JPA. */
    protected BearbeiterJpaEntity() {
    }

    public BearbeiterJpaEntity(UUID id, String nachname, String vorname, String email, String abteilung) {
        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;
        this.email = email;
        this.abteilung = abteilung;
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

    public String getAbteilung() {
        return abteilung;
    }
}
