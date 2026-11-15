package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception;

import java.util.UUID;

/**
 * Zu einer Id existiert kein Bearbeiter.
 */
public class BearbeiterNichtGefundenException extends RuntimeException {

    public BearbeiterNichtGefundenException(UUID id) {
        super("Bearbeiter mit Id " + id + " wurde nicht gefunden.");
    }
}
