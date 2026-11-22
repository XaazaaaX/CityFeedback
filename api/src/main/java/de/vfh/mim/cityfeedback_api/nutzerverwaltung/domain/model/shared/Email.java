package de.vfh.mim.cityfeedback_api.nutzerverwaltung.domain.model.shared;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: gültige E-Mail-Adresse (normalisiert auf Kleinbuchstaben).
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        Objects.requireNonNull(value, "E-Mail darf nicht null sein.");
        value = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Ungültige E-Mail-Adresse: " + value);
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
