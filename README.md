# CityFeedback

Semesteraufgabe im Modul "Moderne Softwareentwicklung"

## Was ist Git und warum sollte es verwendet werden?
Git ist eine Software zur Versionierung von Dateien, die es Teams ermöglicht, zeitgleich an einem Projekt und sogar gleichen Projektdateien zu arbeiten und diese im Anschluss zusammenzuführen.

Werden die Projektdateien über Git verwaltet, können Teammitglieder auf separaten (Feature-)Branches arbeiten und diese anschließend auf einem Haupt-Branch zusammenführen. Die so entstehenden Unterschiede (gelöschte, geänderte, hinzugefügte Zeilen) sind klar erkennbar. Ggf. entstehende Merge-Konflikte können kontrolliert behoben werden.

Da jeder commit eine eindeutige Identifizierung hat, ist es möglich, die Historie eines Projekts nachzuvollziehen und notfalls zu einem früheren Projektstand zurückzukehren.

## Grundlegende Git-Befehle – Spickzettel & Referenz
---
Eine strukturierte Übersicht der wichtigsten Git-Befehle.

### Setup

| Befehl | Beschreibung |
| :--- | :--- |
| `git config --global user.name [name]` | Schreibt den Usernamen in der Git-Konfiguration. |
| `git config --global user.email [email]` | Hiermit wird die Mail-Adresse des Users in der Git-Konfiguration hinterlegt. |


---
### 1. Projektinitialisierung & Hilfe

| Befehl | Beschreibung |
| :--- | :--- |
| `git init` | Initialisiert ein neues Git-Repository im aktuellen Verzeichnis (erstellt `.git`). |
| `git init <ordner>` | Erstellt ein neues Verzeichnis und initialisiert darin ein Repository. |
| `git clone <url>` | Klont ein bestehendes Remote-Repository als lokale Arbeitskopie inklusive Historie. |
| `git help <befehl>` | Öffnet die offizielle Dokumentation des jeweiligen Befehls im Browser. |

---
### 2. Status prüfen & Änderungen analysieren

| Befehl | Beschreibung |
| :--- | :--- |
| `git status` | Zeigt den aktuellen Zustand an (unverfolgte, geänderte und gestagete Dateien). |
| `git diff` | Zeigt ungestagete Änderungen im Arbeitsverzeichnis gegenüber dem Index an. |
| `git diff --cached` | Vergleicht die Staging Area mit dem letzten Commit (`HEAD`). |
| `git diff HEAD` | Zeigt alle lokalen Änderungen (gestaged und ungestaged) gegenüber dem letzten Commit. |

---
### 3. Änderungen stagen & committen

| Befehl | Beschreibung |
| :--- | :--- |
| `git add <datei>` | Fügt eine Datei zur Staging Area hinzu. |
| `git add .` | Nimmt alle geänderten und neuen Dateien des aktuellen Ordners in den Index auf. |
| `git add -i` | Startet den interaktiven Modus zum gezielten Hinzufügen von Änderungen. |
| `git commit -m "<nachricht>"` | Erstellt einen neuen Snapshot aus dem Index mit einer Commit-Nachricht. |
| `git commit -a -m "<nachricht>"` | Überspringt `git add` für bereits versionierte, geänderte Dateien und committet sofort. |
| `git commit --amend` | Ändert den unmittelbar vorherigen Commit (Nachricht oder hinzugefügte Dateien nachträglich korrigieren). |

---
### 4. Branching & Merging

| Befehl | Beschreibung |
| :--- | :--- |
| `git branch` | Listet alle lokalen Zweige auf (der aktive Branch ist markiert). |
| `git branch <name>` | Erstellt einen neuen Branch. |
| `git checkout -b <name>` | Erstellt einen neuen Branch und wechselt sofort dorthin. |
| `git switch <name>` | Wechselt zum angegebenen Branch. |
| `git merge <branch>` | Führt den angegebenen Branch in den aktuell aktiven Zweig zusammen. |
| `git branch -d <name>` | Löscht einen bereits zusammengeführten lokalen Zweig. |

---
### 5. Remote-Synchronisation

| Befehl | Beschreibung |
| :--- | :--- |
| `git remote add <name> <url>` | Verknüpft das lokale Repository mit einem Remote-Server (üblicherweise `origin`). |
| `git push -u origin <branch>` | Überträgt lokale Commits auf den Server und setzt den Upstream-Zweig. |
| `git push` | Sendet neue Commits an das verknüpfte Remote-Repository. |
| `git fetch` | Ruft neue Commits vom Remote-Server ab, ohne sie in den Arbeitszweig zu mergen. |
| `git pull` | Lädt Server-Änderungen herunter und führt sie direkt im aktuellen Branch zusammen (`fetch` + `merge`). |

---
### 6. Verlauf & Historie einsehen

| Befehl | Beschreibung |
| :--- | :--- |
| `git log` | Zeigt die vollständige Commit-Historie (Autor, Datum, Hash, Nachricht). |
| `git log --pretty=oneline` | Kompakte Darstellung: Jeder Commit wird in einer einzigen Zeile ausgegeben. |
| `git log --stat` | Zeigt zusätzlich Statistiken über geänderte Zeilen und Dateien je Commit an. |
| `git log -p -2` | Zeigt die Diffs (Patch) der letzten 2 Commits detailliert an. |

---
### 7. Korrekturen & Dateioperationen

| Befehl | Beschreibung |
| :--- | :--- |
| `git reset HEAD <datei>` | Entfernt eine Datei aus der Staging Area (Inhalt bleibt im Arbeitsverzeichnis erhalten). |
| `git rm <datei>` | Löscht eine Datei aus dem Dateisystem und aus dem Index. |
| `git rm --cached <datei>` | Entfernt die Datei nur aus dem Git-Tracking, behält sie aber lokal auf der Festplatte. |
| `git mv <alt> <neu>` | Verschiebt oder benennt eine Datei um und aktualisiert den Index automatisch. |

Quellen:
* [git_cheat_sheet](https://education.github.com/git-cheat-sheet-education.pdf)
* Edlich, Stefan (2026): "Softwaretechnik". VFH-Online-Modul. Version 2026.
---


Nützliche Git-Tools und Plattformen
---
Dieser Bereich zeigt eine Übersicht verschiedenen Tools und Plattformen, die im Kontext von Git betrachtet werden können.

### Plattformen

| Bezeichung | Beschreibung | Link |
| :--- | :--- | :--- | 
| `GitHub` | Der weltweite Marktführer mit riesiger Community, integriertem Issue-Tracking, Pull Requests und GitHub Actions für CI/CD | [Link](https://github.com/) |
| `GitLab` | Eine umfassende DevSecOps-Plattform mit starkem Fokus auf integriertes CI/CD, Wikis und Projektmanagement (auch als Self-Hosting-Variante beliebt) | [Link](https://about.gitlab.com/de-de/) |
| `Bitbucket` | Die Plattform von Atlassian, die sich nahtlos mit Jira und Confluence verknüpft. | [Link](https://bitbucket.org/) |
| `Gitea` |  Sehr leichte, quelloffene Git-Hosting-Lösungen, die sich hervorragend auf einem eigenen Server (Self-Hosting) betreiben lassen. | [Link](https://about.gitea.com/) |

### Tools

| Bezeichung | Beschreibung | Link |
| :--- | :--- | :--- | 
| `GitKraken` | Ein sehr eleganter, plattformübergreifender Client mit starker visueller Historie, integriertem Merge-Konflikt-Editor und guter Issue-Anbindung. | [Link](https://gitkraken.com/) |
| `Sourcetree` | Ein kostenloser Client von Atlassian mit guter Bitbucket- und Jira-Integration sowie übersichtlichen Diff-Ansichten. | [Link](https://www.sourcetreeapp.com/) |
| `GitHub Desktop` | Einsteigerfreundlicher, kostenloser Offizieller Client von GitHub für einfache Workflows. | [Link](https://github.com/apps/desktop) |

Weiter gibt es für die verschiedenen IDEs Erweiterungen, oder bereits bestehende Integrationen, die gut genutzt werden können.
