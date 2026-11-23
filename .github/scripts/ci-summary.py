"""Schreibt eine Markdown-Zusammenfassung für den GitHub-Job-Summary.

Aufruf: python3 ci-summary.py <maven-target-verzeichnis> [tests|checkstyle]
  tests      – Testergebnisse (Surefire) und Coverage (JaCoCo)   [Standard]
  checkstyle – Lint-Verstöße (Checkstyle)
"""
import csv
import glob
import os
import sys
import xml.etree.ElementTree as ET

MAX_LIST = 50


def tests_section(target):
    out = []
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    failed = []
    for path in sorted(glob.glob(os.path.join(target, "surefire-reports", "TEST-*.xml"))):
        suite = ET.parse(path).getroot()
        for key in totals:
            totals[key] += int(float(suite.get(key, 0)))
        for case in suite.iter("testcase"):
            if case.find("failure") is not None or case.find("error") is not None:
                failed.append(f"{case.get('classname', '')}.{case.get('name', '')}")

    if totals["tests"] == 0:
        out.append("\n⚠️ Keine Testergebnisse gefunden (Build vermutlich vor den Tests abgebrochen).")
    else:
        passed = totals["tests"] - totals["failures"] - totals["errors"] - totals["skipped"]
        icon = "✅" if not failed else "❌"
        out.append(f"\n### {icon} Tests\n")
        out.append("| Gesamt | Bestanden | Fehlgeschlagen | Übersprungen |")
        out.append("|---:|---:|---:|---:|")
        out.append(f"| {totals['tests']} | {passed} | {totals['failures'] + totals['errors']} | {totals['skipped']} |")
        if failed:
            out.append("\n**Fehlgeschlagene Tests:**\n")
            out.extend(f"- `{name}`" for name in failed[:MAX_LIST])

    csv_path = os.path.join(target, "site", "jacoco", "jacoco.csv")
    if os.path.exists(csv_path):
        sums = {}
        with open(csv_path, newline="", encoding="utf-8") as f:
            for row in csv.DictReader(f):
                for kind in ("INSTRUCTION", "BRANCH", "LINE", "METHOD"):
                    missed, covered = sums.get(kind, (0, 0))
                    sums[kind] = (missed + int(row[f"{kind}_MISSED"]), covered + int(row[f"{kind}_COVERED"]))

        labels = {"LINE": "Zeilen", "BRANCH": "Branches", "METHOD": "Methoden", "INSTRUCTION": "Anweisungen"}

        out.append("\n### 📊 Coverage (JaCoCo)\n")
        out.append("| | Abgedeckt | Nicht abgedeckt | Gesamt | Abdeckung |")
        out.append("|---|---:|---:|---:|---:|")
        for kind, label in labels.items():
            missed, covered = sums[kind]
            total = missed + covered
            percent = f"{covered / total * 100:.1f} %" if total else "–"
            out.append(f"| {label} | {covered} | {missed} | {total} | **{percent}** |")
        out.append("\nDer vollständige HTML-Bericht liegt im Artefakt `test-reports`.")
    return out


def checkstyle_section(target):
    path = os.path.join(target, "checkstyle-result.xml")
    if not os.path.exists(path):
        return ["\n⚠️ Kein Checkstyle-Ergebnis gefunden (Build vermutlich vorher abgebrochen)."]

    violations = []
    for file_el in ET.parse(path).getroot().iter("file"):
        name = file_el.get("name", "")
        short = name.split("/src/", 1)[-1] if "/src/" in name else name
        for err in file_el.iter("error"):
            rule = err.get("source", "").rsplit(".", 1)[-1].removesuffix("Check")
            violations.append((short, err.get("line", "?"), rule, err.get("message", "")))

    if not violations:
        return ["\n### ✅ Checkstyle\n", "Keine Verstöße gefunden."]

    out = [f"\n### ❌ Checkstyle: {len(violations)} Verstöße\n",
           "| Datei | Zeile | Regel | Meldung |", "|---|---:|---|---|"]
    for short, line, rule, msg in violations[:MAX_LIST]:
        msg = msg.replace("|", "\\|")
        out.append(f"| `{short}` | {line} | {rule} | {msg} |")
    if len(violations) > MAX_LIST:
        out.append(f"\n… und {len(violations) - MAX_LIST} weitere (siehe Job-Log).")
    out.append("\nLokal prüfen: `cd api && ./mvnw checkstyle:check`")
    return out


target = sys.argv[1] if len(sys.argv) > 1 else "target"
mode = sys.argv[2] if len(sys.argv) > 2 else "tests"
lines = ["## CityFeedback API"]
lines += checkstyle_section(target) if mode == "checkstyle" else tests_section(target)
print("\n".join(lines))
