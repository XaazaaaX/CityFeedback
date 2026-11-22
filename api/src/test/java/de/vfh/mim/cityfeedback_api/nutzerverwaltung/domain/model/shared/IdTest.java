package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Id (Nutzerverwaltung)")
class IdTest {

    private static final String TEXT = "3f2504e0-4f89-11d3-9a0c-0305e82c3301";

    @Test
    @DisplayName("neu() erzeugt bei jedem Aufruf eine andere Id")
    void neuIstEindeutig() {
        assertNotEquals(Id.neu(), Id.neu());
    }

    @Test
    @DisplayName("of(UUID) übernimmt den Wert")
    void ausUuid() {
        UUID uuid = UUID.randomUUID();
        assertEquals(uuid, Id.of(uuid).value());
    }

    @Test
    @DisplayName("of(String) liest die Textform, auch mit Leerzeichen am Rand")
    void ausText() {
        assertEquals(UUID.fromString(TEXT), Id.of("  " + TEXT + " ").value());
    }

    @Test
    @DisplayName("toString liefert die Textform der UUID")
    void textform() {
        assertEquals(TEXT, Id.of(TEXT).toString());
    }

    @Test
    @DisplayName("Ids mit gleichem Wert sind gleich")
    void wertgleichheit() {
        assertEquals(Id.of(TEXT), Id.of(UUID.fromString(TEXT)));
        assertEquals(Id.of(TEXT).hashCode(), Id.of(UUID.fromString(TEXT)).hashCode());
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {"", "  ", "1", "keine-uuid", "3f2504e0-4f89-11d3-9a0c"})
    @DisplayName("of(String) lehnt ungültige Texte ab")
    void ungueltigerText(String text) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Id.of(text));
        assertTrue(ex.getMessage().startsWith("Ungültige Id"));
    }

    @Test
    @DisplayName("lehnt null ab")
    void nullAbgelehnt() {
        assertThrows(NullPointerException.class, () -> Id.of((UUID) null));
        assertThrows(NullPointerException.class, () -> Id.of((String) null));
    }
}
