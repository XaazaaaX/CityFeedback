package de.vfh.mim.cityfeedback_api.nutzerverwaltung.interfaces.rest;

import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.BearbeiterNichtGefundenException;
import de.vfh.mim.cityfeedback_api.nutzerverwaltung.application.exception.EmailBereitsVergebenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Übersetzt Fehler der Bearbeiter-Schnittstelle in HTTP-Antworten
 * im Format "Problem Details" (RFC 9457).
 */
@RestControllerAdvice(basePackageClasses = BearbeiterController.class)
public class BearbeiterExceptionHandler {

    @ExceptionHandler(BearbeiterNichtGefundenException.class)
    public ProblemDetail nichtGefunden(BearbeiterNichtGefundenException ex) {
        return problem(HttpStatus.NOT_FOUND, "Bearbeiter nicht gefunden", ex.getMessage());
    }

    @ExceptionHandler(EmailBereitsVergebenException.class)
    public ProblemDetail emailVergeben(EmailBereitsVergebenException ex) {
        return problem(HttpStatus.CONFLICT, "E-Mail bereits vergeben", ex.getMessage());
    }

    /**
     * Die Datenbank lehnt das Speichern ab (z. B. UNIQUE-Bedingung der E-Mail), etwa wenn
     * zwei Anfragen gleichzeitig dieselbe E-Mail anlegen und beide die Prüfung im Service bestehen.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail datenkonflikt(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Datenkonflikt",
                "Die Daten widersprechen einem bereits gespeicherten Datensatz (z. B. E-Mail bereits vergeben).");
    }

    /** Verletzte Regeln der Value Objects, z. B. ungültige E-Mail oder Id. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail ungueltigeEingabe(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Ungültige Eingabe", ex.getMessage());
    }

    private static ProblemDetail problem(HttpStatus status, String titel, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(titel);
        return problem;
    }
}
