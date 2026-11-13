package de.vfh.mim.cityfeedback_api.model;

import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuergerTest {

    //private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZäöüÄÖÜßéèêáàâíìîóòôúùû]([-' ]?[a-zA-ZäöüÄÖÜßéèêáàâíìîóòôúùû]+)*$");
    /**
     * \p{Lu} Unicode Großbuchstabe
     * \p{Ll} Unicode Kleinbuchstabe
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{Lu}|\\p{Ll}]([-'\\s]?[\\p{Lu}|\\p{Ll}]{1})[\\p{Ll}]*$");
    private static final Pattern MAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    static class Buerger {

        public Buerger(String vorname, String nachname, String email) {
            if (vorname == null || !NAME_PATTERN.matcher(vorname).matches()) {
                throw new IllegalArgumentException("Inkorrekter Vorname");
            }
            if (nachname == null || !NAME_PATTERN.matcher(nachname).matches()) {
                throw new IllegalArgumentException("Inkorrekter Nachname");
            }
            if (email == null || !MAIL_PATTERN.matcher(email).matches()) {
                throw new IllegalArgumentException("Inkorrekte Mailadresse");
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Max", "Anna-Maria", "Peter Klaus", "Björn", "René"})
    @DisplayName("Erstellt Bürger mit gültigem Vornamen")
    void akzeptiertGueltigeVornamen(String name) {
        assertDoesNotThrow(() -> new Buerger(name, "Mustermann", "max@example.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Mustermann", "O'Connor", "d'Link", "von der schönen Au", "Meyer-Schulze", "Charlés"})
    @DisplayName("Erstellt Bürger mit gültigem Nachnamen")
    void akzeptiertGueltigeNachnamen(String name) {
        assertDoesNotThrow(() -> new Buerger("Peter", name, "max@example.com"));
    }

    /**
     * An dieser Stelle genügt der Test auf den Vornamen, da das identische Pattern für den Nachnamen genutzt wird!
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "Max123", "<script>","//Emmet", "IchBinKeinName", "ichAuchNicht"})
    @DisplayName("Blockiert fehlerhafte Namen")
    void blockiertFalscheNamen(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Buerger(name, "Mustermann", "max@example.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"max@example.com", "anna@domain.co.uk", "a@b.com", "test+filter@example.com"})
    @DisplayName("Erstellt Buerger bei korrekten Mailadressen")
    void akzeptiertGueltigeMails(String mail) {
        assertDoesNotThrow(() -> new Buerger("Max", "Mustermann", mail));
    }

    @ParameterizedTest
    @ValueSource(strings = {"max.example.com", "max@.com", "@example.com"})
    @DisplayName("Blockiert fehlerhafte Mailadressen")
    void blockiertFalscheMails(String mail) {
        assertThrows(IllegalArgumentException.class, () -> new Buerger("Max", "Mustermann", mail));
    }
}