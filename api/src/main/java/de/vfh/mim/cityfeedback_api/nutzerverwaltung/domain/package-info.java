/**
 * Domain-Layer des Bounded Context <b>Nutzerverwaltung</b> (Generic Subdomain).
 * <p>
 * Registrierung und Login, Verwaltung von Bürgern und Bearbeitern.
 * <p>
 * Aggregate: {@code Bearbeiter} (Aggregate Root mit eigenem Repository),
 * später {@code Buerger}. Alle verwenden die zentrale {@code Id} (UUID) aus
 * {@code model.shared}. Andere Bounded Contexts speichern nur deren UUID-Wert.
 */
package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain;
