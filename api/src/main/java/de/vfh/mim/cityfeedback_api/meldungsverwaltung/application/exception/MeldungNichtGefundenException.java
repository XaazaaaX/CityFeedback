package de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.exception;

import java.util.UUID;

/**
 * Wird geworfen, wenn eine Meldung mit der angegebenen Id nicht existiert.
 */
public class MeldungNichtGefundenException extends RuntimeException {

    public MeldungNichtGefundenException(UUID id) {
        super("Meldung mit Id " + id + " nicht gefunden.");
    }
}
