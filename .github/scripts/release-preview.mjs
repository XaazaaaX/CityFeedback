// Release-Vorschau: führt semantic-release im Dry-Run aus (es wird nichts veröffentlicht)
// und schreibt das Ergebnis in den GitHub-Job-Summary.
// Erwartet, dass der aktuelle Stand bereits mit main zusammengeführt wurde (siehe ci.yml).
import { appendFileSync } from "node:fs";
import { execFileSync } from "node:child_process";
import semanticRelease from "semantic-release";

const summary = (text) => {
  const path = process.env.GITHUB_STEP_SUMMARY;
  if (path) appendFileSync(path, text + "\n");
  else console.log(text);
};

const lastTag = () => {
  try {
    return execFileSync("git", ["describe", "--tags", "--abbrev=0", "--match", "v[0-9]*", "main"], {
      encoding: "utf8",
    }).trim();
  } catch {
    return null;
  }
};

const code = (text) => "`" + text + "`";

const RELEASE_ART = {
  major: "Major (Breaking Change)",
  minor: "Minor (neue Funktion)",
  patch: "Patch (Fehlerbehebung)",
};

summary("## 🔮 Release-Vorschau (Dry-Run)\n");
summary("_Das würde semantic-release veröffentlichen, wenn dieser Stand nach " + code("main") + " gemergt wird._\n");

try {
  const result = await semanticRelease(
    { dryRun: true, ci: false },
    // so tun, als liefe der Job auf main – nur dort wird released (.releaserc.json)
    { env: { ...process.env, GITHUB_EVENT_NAME: "push", GITHUB_REF: "refs/heads/main" } },
  );

  if (!result) {
    const tag = lastTag();
    summary(
      "Kein neues Release: seit " + (tag ? code(tag) : "Projektbeginn") +
        " gibt es keine " + code("feat") + "-, " + code("fix") + "- oder " + code("perf") + "-Commits.",
    );
  } else {
    const { lastRelease, nextRelease, commits } = result;
    summary("| Aktuelle Version | Nächste Version | Art | Commits seit letztem Release |");
    summary("|---|---|---|---:|");
    summary(
      "| " + (lastRelease?.version ? code(lastRelease.version) : "– (erstes Release)") +
        " | **" + code(nextRelease.version) + "**" +
        " | " + (RELEASE_ART[nextRelease.type] ?? nextRelease.type) +
        " | " + commits.length + " |",
    );
    summary("\n<details open><summary>Release Notes für " + nextRelease.gitTag + "</summary>\n");
    summary(nextRelease.notes || "_(keine)_");
    summary("\n</details>");
  }
} catch (error) {
  summary("⚠️ Die Vorschau konnte nicht erstellt werden: " + error.message);
  console.error(error);
}
