package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Email;

/**
 * Eine E-Mail-Adresse darf nur einem Bearbeiter gehören (geprüft im BearbeiterService,
 * zusätzlich abgesichert durch die UNIQUE-Bedingung der Datenbank).
 */
public class EmailBereitsVergebenException extends RuntimeException {

    public EmailBereitsVergebenException(Email email) {
        super("Die E-Mail-Adresse " + email.value() + " ist bereits vergeben.");
    }
}
