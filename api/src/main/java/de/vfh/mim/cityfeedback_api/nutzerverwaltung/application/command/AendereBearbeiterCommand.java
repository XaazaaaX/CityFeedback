package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command;

import java.util.UUID;

/**
 * Anwendungsfall "Bearbeiter ändern": ersetzt Name, E-Mail und Abteilung des
 * Bearbeiters mit der angegebenen Id. Die Werte werden erst in der Domain geprüft.
 */
public record AendereBearbeiterCommand(UUID id, String nachname, String vorname, String email, String abteilung) {
}
