package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain.model.meldung;

/**
 * Lebenszyklus-Status einer Meldung.
 * <p>
 * Erlaubte Übergänge:
 * <pre>
 *   EINGEREICHT → IN_BEARBEITUNG → KOMMENTIERT → ERLEDIGT
 *                                              → ABGELEHNT
 * </pre>
 * Ein erneuter Statuswechsel aus {@code ERLEDIGT} oder {@code ABGELEHNT}
 * ist nicht vorgesehen. Aus {@code KOMMENTIERT} darf erneut kommentiert
 * werden (Status bleibt {@code KOMMENTIERT}).
 */
public enum Status {

    EINGEREICHT,
    IN_BEARBEITUNG,
    KOMMENTIERT,
    ERLEDIGT,
    ABGELEHNT;

    /**
     * Prüft, ob ein Übergang von diesem Status zum Zielstatus erlaubt ist.
     */
    public boolean kannWechselnNach(Status ziel) {
        return switch (this) {
            case EINGEREICHT -> ziel == IN_BEARBEITUNG;
            case IN_BEARBEITUNG -> ziel == KOMMENTIERT;
            case KOMMENTIERT -> ziel == ERLEDIGT || ziel == ABGELEHNT || ziel == KOMMENTIERT;
            case ERLEDIGT, ABGELEHNT -> false;
        };
    }
}
