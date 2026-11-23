-- Nutzerverwaltung: Bearbeiter
-- Die Id (UUID) wird von der Domain erzeugt, nicht von der Datenbank.

CREATE TABLE IF NOT EXISTS bearbeiter (
    id         UUID         NOT NULL PRIMARY KEY,
    nachname   VARCHAR(100) NOT NULL,
    vorname    VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    abteilung  VARCHAR(100) NOT NULL
);

-- Nutzerverwaltung: Bürger
CREATE TABLE IF NOT EXISTS buerger (
    id         UUID         NOT NULL PRIMARY KEY,
    nachname   VARCHAR(100) NOT NULL,
    vorname    VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE
);

-- Meldungsverwaltung: Meldung
CREATE TABLE IF NOT EXISTS meldung (
    id                UUID         NOT NULL PRIMARY KEY,
    melder_id         UUID         NOT NULL,
    bearbeiter_id     UUID,
    kategorie         VARCHAR(50)  NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    betreff           VARCHAR(255) NOT NULL,
    text              TEXT         NOT NULL,
    erstell_zeitpunkt TIMESTAMP    NOT NULL
);

-- Meldungsverwaltung: Kommentare (Value Object List)
CREATE TABLE IF NOT EXISTS meldung_kommentare (
    meldung_id        UUID         NOT NULL,
    ersteller_id      UUID         NOT NULL,
    betreff           VARCHAR(255) NOT NULL,
    text              TEXT         NOT NULL,
    erstell_zeitpunkt TIMESTAMP    NOT NULL,
    CONSTRAINT fk_kommentare_meldung FOREIGN KEY (meldung_id) REFERENCES meldung(id) ON DELETE CASCADE
);
