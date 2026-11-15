/**
 * Domain-Layer des Bounded Context <b>Meldungsverwaltung</b> (Core Domain).
 * <p>
 * Geschäftsprozesse rund um die Meldung: Erstellung, Statusänderung, Kommentierung.
 * <p>
 * <b>Kontextgrenze:</b> Bürger und Bearbeiter gehören zur Nutzerverwaltung. Die
 * Meldungsverwaltung importiert deren Klassen nicht, sondern speichert nur deren
 * UUID-Werte in ihrer eigenen {@code Id}-Klasse. Ein Bearbeiter ist hier
 * fachlich "der Zuständige für eine Meldung", nicht das Benutzerkonto.
 * Die Regel wird durch den ArchitekturTest geprüft.
 */
package de.vfh.mim.cityfeedback_api.meldungsverwaltung.domain;
