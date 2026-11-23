# Mitarbeit an CityFeedback

## Branches

- `main` – stabiler Stand; jeder Merge hierher kann ein Release auslösen
- `dev` – Integrationsbranch; Feature-Branches werden per Pull Request nach `dev` gemergt
- Feature-Branches, z. B. `feature/meldung-anlegen` oder `fix/email-validierung`

## Commit-Messages (Conventional Commits)

Commit-Messages folgen den [Conventional Commits](https://www.conventionalcommits.org/de/v1.0.0/).
Das wird in Pull Requests automatisch geprüft (commitlint), und semantic-release berechnet daraus die nächste Version.

```
<typ>(<optionaler bereich>): <kurze beschreibung>
```

| Typ | Wofür | Auswirkung auf die Version |
|---|---|---|
| `feat` | neue Funktion | Minor (1.**2**.0) |
| `fix` | Fehlerbehebung | Patch (1.1.**1**) |
| `perf` | Performance-Verbesserung | Patch |
| `refactor`, `test`, `docs`, `style`, `build`, `ci`, `chore` | alles andere | kein Release |

Breaking Changes werden mit `!` markiert (`feat!: ...`) oder mit `BREAKING CHANGE:` im Commit-Text → Major (**2**.0.0).

Beispiele:

```
feat(meldungsverwaltung): bürger können meldungen mit foto anlegen
fix(nutzerverwaltung): e-mail-adressen werden ohne beachtung der groß-/kleinschreibung verglichen
test(nutzerverwaltung): tests für bearbeiter-controller ergänzt
docs: übung 5 in readme ergänzt
```

Lokal prüfen (Node.js nötig):

```bash
npm install --no-save @commitlint/cli@20 @commitlint/config-conventional@20
npx commitlint --last
```

## Code-Stil (Checkstyle)

Checkstyle läuft bei jedem Maven-Build automatisch mit (Regeln: `api/config/checkstyle/checkstyle.xml`).
Einzeln prüfen:

```bash
cd api
./mvnw checkstyle:check
```

In begründeten Einzelfällen kann eine Regel mit `@SuppressWarnings("checkstyle:<regelname>")` ausgenommen werden.

## CI/CD im Überblick

| Wann | Was passiert |
|---|---|
| Pull Request | Checkstyle, Build & Tests mit Coverage, Commit-Lint, Docker-Image wird testweise gebaut, **Release-Vorschau** (welche Version beim Merge nach `main` entstehen würde, inkl. Release Notes) |
| Push auf `dev` | wie oben (inkl. Release-Vorschau), Image `ghcr.io/xaazaaax/cityfeedback-api:dev` wird veröffentlicht |
| Push auf `main` | zusätzlich **semantic-release**: Version bestimmen, Tag `vX.Y.Z` und GitHub Release mit Release Notes und JAR erstellen, Image mit `X.Y.Z`, `X.Y` und `latest` veröffentlichen |
