package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.bearbeiter;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Nachname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Vorname;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id;
import java.util.Objects;

/**
 * Entität Bearbeiter (Bounded Context: Nutzerverwaltung).
 * <p>
 * Die Identität ergibt sich ausschließlich aus der {@link Id}, die beim
 * Anlegen von der Domain erzeugt wird.
 * Alle Attribute sind Value Objects, die ihre eigenen Regeln prüfen;
 * die Entität stellt sicher, dass kein Attribut fehlt.
 * Neue Bearbeiter werden ausschließlich über die Factory-Methode
 * {@link #erstelleBearbeiter} erzeugt.
 * Die Klasse ist bewusst frei von Spring- und JPA-Abhängigkeiten.
 */
public class Bearbeiter {

    private final Id id;
    private Nachname nachname;
    private Vorname vorname;
    private Email email;
    private Abteilung abteilung;

    private Bearbeiter(Id id, Nachname nachname, Vorname vorname, Email email, Abteilung abteilung) {
        this.id = Objects.requireNonNull(id, "Id darf nicht null sein.");
        this.nachname = Objects.requireNonNull(nachname, "Nachname darf nicht null sein.");
        this.vorname = Objects.requireNonNull(vorname, "Vorname darf nicht null sein.");
        this.email = Objects.requireNonNull(email, "E-Mail darf nicht null sein.");
        this.abteilung = Objects.requireNonNull(abteilung, "Abteilung darf nicht null sein.");
    }

    // --- Factory -----------------------------------------------------------

    /**
     * Factory-Methode: erzeugt einen neuen, gültigen Bearbeiter mit neuer Id.
     * Die Rohwerte werden in Value Objects überführt und dabei validiert.
     *
     * @throws IllegalArgumentException bei ungültigen Werten
     * @throws NullPointerException     wenn ein Pflichtwert fehlt
     */
    public static Bearbeiter erstelleBearbeiter(String nachname, String vorname,
                                                String email, String abteilung) {
        return new Bearbeiter(Id.neu(), Nachname.of(nachname), Vorname.of(vorname),
                Email.of(email), Abteilung.of(abteilung));
    }

    /**
     * Stellt einen bereits existierenden Bearbeiter wieder her, z. B. beim Laden
     * aus der Datenbank.
     * <p>
     * Im Unterschied zu {@link #erstelleBearbeiter} ist das fachlich kein
     * "Anlegen": Hier sollen später auch keine Domain Events (z. B.
     * "BearbeiterAngelegt") ausgelöst werden.
     *
     * @throws NullPointerException wenn ein Wert fehlt
     */
    public static Bearbeiter rekonstruiere(Id id, Nachname nachname, Vorname vorname,
                                           Email email, Abteilung abteilung) {
        return new Bearbeiter(id, nachname, vorname, email, abteilung);
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

    public void wechsleAbteilung(Abteilung neueAbteilung) {
        this.abteilung = Objects.requireNonNull(neueAbteilung, "Abteilung darf nicht null sein.");
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

    public Abteilung getAbteilung() {
        return abteilung;
    }

    public String getVollerName() {
        return vorname.value() + " " + nachname.value();
    }

    // --- Identität (Entity-Semantik) ----------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bearbeiter other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Bearbeiter{id=" + id + ", name=" + getVollerName()
                + ", email=" + email.value() + ", abteilung=" + abteilung.name() + "}";
    }
}
