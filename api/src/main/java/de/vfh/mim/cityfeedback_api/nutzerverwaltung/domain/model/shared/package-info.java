/**
 * Value Objects, die von mehreren Aggregaten <b>innerhalb</b> der Nutzerverwaltung
 * genutzt werden (z. B. von Bearbeiter und Bürger).
 * <p>
 * Dazu gehört auch die zentrale {@link de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared.Id}
 * aller Aggregate der Nutzerverwaltung.
 * <p>
 * Dies ist kein kontextübergreifender Shared Kernel: Andere Bounded Contexts
 * (Meldungsverwaltung, Benachrichtigung) verwenden diese Klassen nicht direkt,
 * sondern haben ihre eigenen.
 */
package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared;
