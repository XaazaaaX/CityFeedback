package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command;

import java.util.UUID;

/**
 * Anwendungsfall "Bürger ändern": ersetzt Name und E-Mail des
 * Bürgers mit der angegebenen Id. Die Werte werden erst in der Domain geprüft.
 */
public record AendereBuergerCommand(UUID id, String nachname, String vorname, String email) {
}
