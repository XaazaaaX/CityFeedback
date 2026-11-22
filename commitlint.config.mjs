// Regeln für Commit-Messages (Conventional Commits), geprüft in .github/workflows/commitlint.yml
// Format: <typ>(<optionaler scope>): <beschreibung>
// Beispiel: feat(nutzerverwaltung): bearbeiter können ihre abteilung wechseln
export default {
  extends: ['@commitlint/config-conventional'],
  rules: {
    // Deutsche Substantive werden großgeschrieben -> Groß-/Kleinschreibung nicht prüfen
    'subject-case': [0],
    // Lange Zeilen im Commit-Body nur als Warnung
    'body-max-line-length': [1, 'always', 100],
    'footer-max-line-length': [1, 'always', 100],
  },
};
