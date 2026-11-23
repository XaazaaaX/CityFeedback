package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception;

import java.util.UUID;

/**
 * Zu einer Id existiert kein Bürger.
 */
public class BuergerNichtGefundenException extends RuntimeException {

    public BuergerNichtGefundenException(UUID id) {
        super("Bürger mit Id " + id + " wurde nicht gefunden.");
    }
}
