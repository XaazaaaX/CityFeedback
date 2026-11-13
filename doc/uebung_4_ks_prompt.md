
# Übung 4

## Prompt

Überprüfe meine bisherige Implementierungsstrategie und die Bounded Contexts kritisch.
<br>*Inhalte wurden als Datei angehängt*

- Identifiziere die wichtigsten Schwachstellen.
- Gib mindestens drei konkrete Verbesserungsvorschläge.Berücksichtige dabei die Struktur der Domain-Events und die Entwicklungsreihenfolge.
- Begründe jeden Vorschlag kurz und nenne mögliche Nachteile.
- Beziehe dich auf meine Angaben und vermeide unnötige Komplexität. Falls wichtige Informationen fehlen, frage nach, statt sie zu erfinden.
- Halte deine Antwort kurz: maximal 500 Wörter, bevorzugt als Stichpunkte oder kompakte Tabelle.
- Warte anschließend auf meine Bewertung, welche Vorschläge ich übernehmen, anpassen oder verwerfen möchte. Überarbeite erst danach die Strategie entsprechend – ebenfalls kurz und übersichtlich.

---

## Antwort der LLM

Moin! Deine Strategie ist grundsätzlich tragfähig. Die größten Schwachstellen sind widersprüchliche Fachregeln, nicht durchgängig eingehaltene Kontextgrenzen und eine möglicherweise zu schichtenorientierte Entwicklungsreihenfolge. Zusätzliche Technologien sind dafür nicht nötig.

### Wichtigste Schwachstellen
   - Uneinheitliches Statusmodell: Im Diagramm stehen OFFEN, GELÖST; im Ablauf EINGEREICHT, KOMMENTIERT, ERLEDIGT. Dadurch bleiben Übergänge und Tests widersprüchlich. Ein Kommentar ist zudem nicht automatisch ein Bearbeitungsstatus.
  -  Kontextgrenzen nur teilweise umgesetzt: Die Trennung in Meldungs-, Nutzer- und Benachrichtigungsverwaltung ist plausibel. Allerdings erhält Meldung vollständige Bürger- und Bearbeiter-Objekte, obwohl ihr ausdrücklich über IDs integrieren wollt. Die globale Paketstruktur macht die fachlichen Grenzen außerdem kaum sichtbar.
   - Events und Commands vermischt: MeldungErstellt steht als Command, beschreibt aber eine bereits eingetretene Tatsache. Das Statusereignis enthält im Detailmodell zu wenig Informationen für eine nachvollziehbare Benachrichtigung.
   - Transaktionsrisiko: „Nach erfolgreichem Speichern“ bedeutet nicht zwingend „nach erfolgreichem Commit“. Benachrichtigungen könnten trotz späterem Rollback entstehen.
   - Offene Konsistenzregel: „Nur ein Bearbeiter gleichzeitig“ ist nicht präzisiert. Unklar ist, ob eine aktuelle Zuständigkeit oder eine exklusive Bearbeitungssperre gemeint ist.
   - Später Integrationsnachweis: Wenn ihr zunächst sämtliche Domänenklassen und anschließend alle weiteren Schichten implementiert, wird der durchgängige Happy-Path erst spät überprüfbar.
###  Konkrete Verbesserungsvorschläge

| Priorität | Vorschlag | Begründung | Mögliche Nachteile |
|---|---|---|---|
| **1** | **Statusmodell vereinheitlichen.** Einen verbindlichen Statuskatalog und erlaubte Übergänge festlegen; Kommentare unabhängig vom Status behandeln. | Behebt konkrete Widersprüche und schafft eindeutige Tests. | Erfordert fachliche Abstimmung; eventuell Anpassung der Diagramme. |
| **1** | **Kontexte als Module einer Anwendung umsetzen.** Pakete `meldung`, `nutzer`, `benachrichtigung`; im Meldungsaggregat nur `MelderId` und `BearbeiterId` referenzieren. | Reduziert Kopplung, ohne Microservices einzuführen. Nutzerverwaltung verantwortet Identitäten, Meldungsverwaltung den Meldungszustand, Benachrichtigungen den Versand. | Zusätzliche Schnittstellen zur Ermittlung benötigter Nutzerinformationen. |
| **1** | **Events präzisieren.** Commands beispielsweise `MeldungEinreichen`; Events als Vorschläge: `MeldungEingereicht`, `MeldungsstatusGeaendert`, `KommentarHinzugefuegt`. Statusereignis mit Meldungs-ID, Melder-ID, altem/neuem Status und Zeitpunkt. | Trennt Absicht und Tatsache; Kommentare erzeugen keine künstlichen Statuswechsel. | Event-Verträge müssen gepflegt werden. Separate Integration-Events sind zunächst nicht zwingend. |
| **1** | **Vertikal entwickeln:** zuerst Einreichen und Anzeigen, danach Bearbeitung mit Benachrichtigung, anschließend Abschluss und Kommentare. Innerhalb jedes Schritts weiter test-first und von innen nach außen arbeiten. | Liefert früh einen überprüfbaren Anwendungsfall und entdeckt Integrationsprobleme. | Kleine iterative Umbauten möglich. |
| **2** | **Versand erst nach Commit**, zunächst mit einfachem Handler und Versandadapter. | Verhindert Nachrichten über zurückgerollte Änderungen. | Prozessabbruch kann Nachrichten verlieren; zuverlässige Wiederholung benötigt gegebenenfalls dauerhafte Versandaufträge. |


### Noch zu klären
   - Bedeutet „ein Bearbeiter gleichzeitig“ Zuordnung oder Sperre?
   - Muss Phase 1 Benachrichtigungen zuverlässig wiederholen, oder genügt zunächst der erfolgreiche Versand im Happy-Path?

---

## Antwort auf die Fragen der LLM

- Bedeutet „ein Bearbeiter gleichzeitig“ Zuordnung oder Sperre? Sie bedeutet Zuordnung, ggf. kann ein Bearbeiter die Bearbeitung übernehmen.
- In Phase 1 genügt die erfolgreiche Versendung einer Benachrichtigung

---

## Antwort der LLM

Moin, damit sind die offenen Punkte geklärt.

| Punkt | Konsequenz für den Entwurf |
|---|---|
| **Eine aktuelle Bearbeiterzuordnung; Übernahme möglich** | Die Meldung hält eine optionale `BearbeiterId`. Eine Übernahme ersetzt die bisherige Zuordnung. Eine exklusive Bearbeitungssperre ist nicht erforderlich. **Wer unter welchen Bedingungen übernehmen darf, bleibt noch festzulegen.** |
| **Phase 1: erfolgreicher Versand im Happy-Path genügt** | Ein einfacher **Nach-Commit-Handler mit Versandadapter** reicht zunächst. Keine Outbox, kein Broker und keine automatische Wiederholung erforderlich. Versandfehler sollten dennoch sichtbar sein; mögliche Nachrichtenverluste bei einem Prozessabbruch bleiben eine bewusst akzeptierte Einschränkung. |