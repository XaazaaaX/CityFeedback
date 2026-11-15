-- Nutzerverwaltung: Bearbeiter
-- Die Id (UUID) wird von der Domain erzeugt, nicht von der Datenbank.

CREATE TABLE IF NOT EXISTS bearbeiter (
    id         UUID         NOT NULL PRIMARY KEY,
    nachname   VARCHAR(100) NOT NULL,
    vorname    VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    abteilung  VARCHAR(100) NOT NULL
);
