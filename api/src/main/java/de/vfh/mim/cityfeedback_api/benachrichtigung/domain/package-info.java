/**
 * Domain-Layer des Bounded Context <b>Benachrichtigung</b> (Supporting Subdomain).
 * <p>
 * Benachrichtigung der Bürger über Änderungen an ihren Meldungen.
 * <p>
 * <b>Kontextgrenze:</b> Reagiert auf Domain Events der Meldungsverwaltung und
 * kennt Bürger nur über ihre Id. Klassen anderer Kontexte werden nicht importiert
 * (geprüft durch den ArchitekturTest).
 */
package de.vfh.mim.cityfeedback_api.benachrichtigung.domain;
