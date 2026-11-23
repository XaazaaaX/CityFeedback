package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.shared.Id;

/**
 * Aggregate Root Meldung (Bounded Context: Meldungsverwaltung).
 * <p>
 * Eine Meldung wird von einem Bürger eingereicht und von einem Bearbeiter
 * bearbeitet. Der Lebenszyklus folgt dem Workflow:
 * {@code EINGEREICHT → IN_BEARBEITUNG → KOMMENTIERT → ERLEDIGT | ABGELEHNT}.
 * <p>
 * Bürger und Bearbeiter werden nur über ihre UUID referenziert, es werden
 * keine Klassen aus der Nutzerverwaltung importiert.
 * <p>
 * Kommentare sind Value Objects innerhalb dieses Aggregats und werden über
 * die Meldung verwaltet. Nur Bearbeiter dürfen kommentieren.
 */
public class Meldung {

    private final Id id;
    private MeldungInhalt inhalt;
    private Kategorie kategorie;
    private Status status;
    private final UUID melderId;
    private UUID bearbeiterId;
    private final List<Kommentar> kommentare;

    private Meldung(Id id, MeldungInhalt inhalt, Kategorie kategorie, Status status,
            UUID melderId, UUID bearbeiterId, List<Kommentar> kommentare) {
        this.id = Objects.requireNonNull(id, "Id darf nicht null sein.");
        this.inhalt = Objects.requireNonNull(inhalt, "Inhalt darf nicht null sein.");
        this.kategorie = Objects.requireNonNull(kategorie, "Kategorie darf nicht null sein.");
        this.status = Objects.requireNonNull(status, "Status darf nicht null sein.");
        this.melderId = Objects.requireNonNull(melderId, "MelderId darf nicht null sein.");
        this.bearbeiterId = bearbeiterId; // darf null sein (noch nicht zugewiesen)
        this.kommentare = new ArrayList<>(Objects.requireNonNull(kommentare, "Kommentarliste darf nicht null sein."));
    }

    // --- Factory -----------------------------------------------------------

    /**
     * Factory-Methode: erzeugt eine neue Meldung mit Status
     * {@link Status#EINGEREICHT}.
     *
     * @param melderId  UUID des meldenden Bürgers
     * @param kategorie Kategorie der Meldung
     * @param betreff   Betreff der Meldung
     * @param text      Text der Meldung
     * @throws NullPointerException     wenn ein Pflichtwert fehlt
     * @throws IllegalArgumentException bei ungültigen Werten
     */
    public static Meldung erstelleMeldung(UUID melderId, Kategorie kategorie,
            String betreff, String text) {
        Objects.requireNonNull(melderId, "MelderId darf nicht null sein.");
        Objects.requireNonNull(kategorie, "Kategorie darf nicht null sein.");
        MeldungInhalt inhalt = MeldungInhalt.of(betreff, text);
        return new Meldung(Id.neu(), inhalt, kategorie, Status.EINGEREICHT,
                melderId, null, new ArrayList<>());
    }

    /**
     * Stellt eine bereits existierende Meldung wieder her (z. B. aus der
     * Datenbank).
     */
    public static Meldung rekonstruiere(Id id, MeldungInhalt inhalt, Kategorie kategorie,
            Status status, UUID melderId, UUID bearbeiterId,
            List<Kommentar> kommentare) {
        return new Meldung(id, inhalt, kategorie, status, melderId, bearbeiterId, kommentare);
    }

    // --- Fachmethoden -------------------------------------------------------

    /**
     * Weist einen Bearbeiter zu und setzt den Status auf
     * {@link Status#IN_BEARBEITUNG}.
     *
     * @param bearbeiterId UUID des zugewiesenen Bearbeiters
     * @throws IllegalStateException wenn der Status den Wechsel nicht erlaubt
     */
    public void bearbeiterZuweisen(UUID bearbeiterId) {
        Objects.requireNonNull(bearbeiterId, "BearbeiterId darf nicht null sein.");
        wechsleStatus(Status.IN_BEARBEITUNG);
        this.bearbeiterId = bearbeiterId;
    }

    /**
     * Fügt einen Kommentar hinzu und setzt den Status auf
     * {@link Status#KOMMENTIERT}.
     * Nur Bearbeiter dürfen kommentieren.
     *
     * @param erstellerId UUID des kommentierenden Bearbeiters
     * @param betreff     Betreff des Kommentars
     * @param text        Text des Kommentars
     * @throws IllegalStateException wenn der Status den Wechsel nicht erlaubt
     */
    public void kommentieren(UUID erstellerId, String betreff, String text) {
        wechsleStatus(Status.KOMMENTIERT);
        Kommentar kommentar = Kommentar.of(erstellerId, betreff, text);
        this.kommentare.add(kommentar);
    }

    /**
     * Markiert die Meldung als erledigt.
     *
     * @throws IllegalStateException wenn der Status den Wechsel nicht erlaubt
     */
    public void erledigen() {
        wechsleStatus(Status.ERLEDIGT);
    }

    /**
     * Lehnt die Meldung ab.
     *
     * @throws IllegalStateException wenn der Status den Wechsel nicht erlaubt
     */
    public void ablehnen() {
        wechsleStatus(Status.ABGELEHNT);
    }

    private void wechsleStatus(Status neuerStatus) {
        if (!this.status.kannWechselnNach(neuerStatus)) {
            throw new IllegalStateException(
                    "Statuswechsel von " + this.status + " nach " + neuerStatus + " ist nicht erlaubt.");
        }
        this.status = neuerStatus;
    }

    // --- Getter -------------------------------------------------------------

    public Id getId() {
        return id;
    }

    public MeldungInhalt getInhalt() {
        return inhalt;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public Status getStatus() {
        return status;
    }

    public UUID getMelderId() {
        return melderId;
    }

    public UUID getBearbeiterId() {
        return bearbeiterId;
    }

    /**
     * Gibt eine unveränderliche Sicht auf die Kommentarliste zurück.
     */
    public List<Kommentar> getKommentare() {
        return Collections.unmodifiableList(kommentare);
    }

    // --- Identität (Entity-Semantik) ----------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Meldung other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Meldung{id=" + id + ", status=" + status + ", kategorie=" + kategorie
                + ", betreff=" + inhalt.betreff() + "}";
    }
}
