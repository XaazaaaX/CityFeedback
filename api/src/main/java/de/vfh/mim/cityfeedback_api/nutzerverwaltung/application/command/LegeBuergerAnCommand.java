package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command;

/**
 * Anwendungsfall "Bürger anlegen": Eingabedaten unabhängig von der Schnittstelle
 * (REST, Messaging, Tests, ...). Die Werte werden erst in der Domain geprüft.
 */
public record LegeBuergerAnCommand(String nachname, String vorname, String email) {
}
