package de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.command;

/**
 * Anwendungsfall "Bearbeiter anlegen": Eingabedaten unabhängig von der Schnittstelle
 * (REST, Messaging, Tests, ...). Die Werte werden erst in der Domain geprüft.
 */
public record LegeBearbeiterAnCommand(String nachname, String vorname, String email, String abteilung) {
}
