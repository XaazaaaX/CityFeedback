package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.buerger;

import java.util.Objects;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;

/**
 * Entität Buerger (Bounded Context: Nutzerverwaltung).
 * <p>
 * Die Identität ergibt sich ausschließlich aus der {@link Id}, die beim
 * Anlegen von der Domain erzeugt wird.
 * Alle Attribute sind Value Objects, die ihre eigenen Regeln prüfen;
 * die Entität stellt sicher, dass kein Attribut fehlt.
 * Neue Buerger werden ausschließlich über die Factory-Methode
 * {@link #erstelleBuerger} erzeugt.
 * Die Klasse ist bewusst frei von Spring- und JPA-Abhängigkeiten.
 */
public class Buerger {

    private final Id id;
    private Nachname nachname;
    private Vorname vorname;
    private Email email;

    private Buerger(Id id, Nachname nachname, Vorname vorname, Email email) {
        this.id = Objects.requireNonNull(id, "Id darf nicht null sein.");
        this.nachname = Objects.requireNonNull(nachname, "Nachname darf nicht null sein.");
        this.vorname = Objects.requireNonNull(vorname, "Vorname darf nicht null sein.");
        this.email = Objects.requireNonNull(email, "E-Mail darf nicht null sein.");
    }

    // --- Factory -----------------------------------------------------------

    /**
     * Factory-Methode: erzeugt einen neuen, gültigen Buerger mit neuer Id.
     * Die Rohwerte werden in Value Objects überführt und dabei validiert.
     *
     * @throws IllegalArgumentException bei ungültigen Werten
     * @throws NullPointerException     wenn ein Pflichtwert fehlt
     */
    public static Buerger erstelleBuerger(String nachname, String vorname, String email) {
        return new Buerger(Id.neu(), Nachname.of(nachname), Vorname.of(vorname), Email.of(email));
    }

    /**
     * Stellt einen bereits existierenden Buerger wieder her, z. B. beim Laden
     * aus der Datenbank.
     * <p>
     * Im Unterschied zu {@link #erstelleBuerger} ist das fachlich kein
     * "Anlegen": Hier sollen später auch keine Domain Events (z. B.
     * "BuergerAngelegt") ausgelöst werden.
     *
     * @throws NullPointerException wenn ein Wert fehlt
     */
    public static Buerger rekonstruiere(Id id, Nachname nachname, Vorname vorname, Email email) {
        return new Buerger(id, nachname, vorname, email);
    }

    // --- Fachmethoden -------------------------------------------------------

    public void aendereName(Vorname neuerVorname, Nachname neuerNachname) {
        // erst beide Werte prüfen, dann zuweisen: kein halb geänderter Zustand
        Objects.requireNonNull(neuerVorname, "Vorname darf nicht null sein.");
        Objects.requireNonNull(neuerNachname, "Nachname darf nicht null sein.");
        this.vorname = neuerVorname;
        this.nachname = neuerNachname;
    }

    public void aendereEmail(Email neueEmail) {
        this.email = Objects.requireNonNull(neueEmail, "E-Mail darf nicht null sein.");
    }

    // --- Getter -------------------------------------------------------------

    public Id getId() {
        return id;
    }

    public Nachname getNachname() {
        return nachname;
    }

    public Vorname getVorname() {
        return vorname;
    }

    public Email getEmail() {
        return email;
    }

    public String getVollerName() {
        return vorname.value() + " " + nachname.value();
    }

    // --- Identität (Entity-Semantik) ----------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Buerger other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Buerger{id=" + id + ", name=" + getVollerName()
                + ", email=" + email.value() + "}";
    }
}
