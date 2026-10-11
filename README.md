# CityFeedback

Semesteraufgabe im Modul "Moderne Softwareentwicklung"

## Git mit IntelliJ benutzen:

IntelliJ IDEA bietet eine grafische Benutzeroberfläche, mit der sich Git-Repositories einfach und mit wenigen Klicks verwalten lassen. Dadurch können viele Git-Funktionen genutzt werden, ohne Befehle über das Terminal ausführen zu müssen.


### 1. Git-Repository erstellen und verbinden

Wenn ein neues Projekt angelegt wurde, kann dieses über den oberen Menüpunkt "Git" → "GitHub" → "Share Project on GitHub" direkt auf GitHub veröffentlicht werden. Alternativ lässt sich über "Git" → "GitLab" → "Share Project" on GitLab ein Repository auf GitLab erstellen.

Über die Option "Create Git Repository..." besteht außerdem die Möglichkeit, zunächst ein lokales Git-Repository anzulegen, ohne dieses direkt auf einer Plattform zu veröffentlichen.

<img width="1482" height="978" alt="Image" src="https://github.com/user-attachments/assets/63049953-928e-4295-973a-fd35dea7e934" />


### 2. Änderungen einsehen

Nachdem das Projekt mit einem Git-Repository verbunden wurde, können über den linken Menüpunkt Commit alle noch nicht committeten Änderungen eingesehen werden.

Hier werden sowohl veränderte als auch neu hinzugefügte Dateien angezeigt.

<img width="1465" height="1003" alt="Image" src="https://github.com/user-attachments/assets/3c0835d5-5ccc-434f-bffa-39733a1ad8f5" />


### 3. Dateiänderungen vergleichen

Möchte man sich die konkreten Änderungen an einer Datei ansehen, genügt ein Klick auf die entsprechende Datei in der Commit-Ansicht.

Daraufhin öffnet sich eine Vergleichsansicht, in der die aktuelle Version der Datei der vorherigen Version gegenübergestellt wird. Hinzugefügte, entfernte und veränderte Codezeilen werden dabei farblich hervorgehoben.

<img width="1625" height="1003" alt="Image" src="https://github.com/user-attachments/assets/63111d47-67a9-4381-91ab-95fca102d912" />


### 4. Änderungen committen und pushen

Sollen die Änderungen gespeichert werden, können zunächst die gewünschten Dateien in der Commit-Ansicht ausgewählt werden.

Anschließend wird im Textfeld eine Commit-Message eingetragen, die die vorgenommenen Änderungen kurz beschreibt.

Danach stehen zwei Möglichkeiten zur Verfügung:

Commit: Die Änderungen werden zunächst nur im lokalen Git-Repository gespeichert.

Commit and Push: Die Änderungen werden lokal committet und anschließend direkt in das Remote-Repository (z. B. GitHub oder GitLab) hochgeladen.

<img width="1625" height="1003" alt="Image" src="https://github.com/user-attachments/assets/1149db02-1877-4770-b499-bb575d64808c" />


### 5. Änderungen nachträglich pushen

Wurde zunächst nur die Option Commit ausgewählt, können die Änderungen auch zu einem späteren Zeitpunkt in das Remote-Repository hochgeladen werden.

Dazu klickt man im oberen Menü auf den aktuellen Branch (in diesem Beispiel be_uebung1) und wählt anschließend die Option Push aus.

IntelliJ zeigt daraufhin die zu übertragenden Commits an. Mit einem weiteren Klick auf Push werden diese in das Remote-Repository übertragen.

Wichtig: Ein Push führt nicht automatisch einen Pull aus. Falls zwischenzeitlich Änderungen von anderen Personen in das Remote-Repository hochgeladen wurden, kann es notwendig sein, diese zunächst über Pull abzurufen und in den eigenen Branch zu integrieren. IntelliJ unterstützt diesen Vorgang ebenfalls über die grafische Benutzeroberfläche.

<img width="1628" height="991" alt="Image" src="https://github.com/user-attachments/assets/1d3af5c3-10db-46d1-a9f9-de45bfba6f35" />
