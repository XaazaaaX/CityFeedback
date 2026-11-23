package de.vfh.mim.cityfeedback_api.meldungsverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.meldungsverwaltung.application.exception.MeldungNichtGefundenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Übersetzt Fehler der Meldungs-Schnittstelle in HTTP-Antworten
 * im Format "Problem Details" (RFC 9457).
 */
@RestControllerAdvice(basePackageClasses = MeldungController.class)
public class MeldungExceptionHandler {

    @ExceptionHandler(MeldungNichtGefundenException.class)
    public ProblemDetail nichtGefunden(MeldungNichtGefundenException ex) {
        return problem(HttpStatus.NOT_FOUND, "Meldung nicht gefunden", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail unzulaessigerStatuswechsel(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT, "Unzulässiger Statuswechsel", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail ungueltigeEingabe(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Ungültige Eingabe", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail datenkonflikt(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Datenkonflikt",
                "Die Daten widersprechen einem bereits gespeicherten Datensatz.");
    }

    private static ProblemDetail problem(HttpStatus status, String titel, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(titel);
        return problem;
    }
}
