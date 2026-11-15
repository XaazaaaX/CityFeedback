package de.vfh.mim.cityfeedback_api;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architekturregeln nach DDD, automatisch geprüft mit ArchUnit.
 * <p>
 * Schlägt ein Test fehl, wurde eine der Regeln aus der README
 * (Implementierungsstrategie) verletzt.
 */
@AnalyzeClasses(packages = "de.vfh.mim.cityfeedback_api", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitekturTest {

    // --- Domain -------------------------------------------------------------

    /** README, Punkt 6: Domäne frameworkfrei halten. */
    @ArchTest
    static final ArchRule domainIstFrameworkfrei = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "jakarta.validation..", "io.swagger..")
            .as("Die Domain hängt nicht von Spring, JPA, Bean Validation oder Swagger ab");

    @ArchTest
    static final ArchRule domainKenntKeineAnderenSchichten = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application..", "..infrastructure..", "..interfaces..")
            .as("Die Domain kennt weder Application, Infrastructure noch Interfaces");

    // --- Application ----------------------------------------------------------

    @ArchTest
    static final ArchRule applicationKenntKeineAeusserenSchichten = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..infrastructure..", "..interfaces..", "io.swagger..", "jakarta.validation..")
            .as("Die Application-Schicht kennt weder Infrastruktur noch Schnittstellen-Technik");

    // --- Infrastructure -------------------------------------------------------

    @ArchTest
    static final ArchRule infrastrukturKenntKeineSchnittstellen = noClasses()
            .that().resideInAPackage("..infrastructure..")
            .should().dependOnClassesThat().resideInAPackage("..interfaces..")
            .as("Die Infrastruktur kennt die Schnittstellen (REST) nicht");

    // --- Bounded Contexts -----------------------------------------------------

    /**
     * README, Punkt 5: Kontextgrenzen erhalten.
     * Jeder Kontext hat eigene Klassen (auch eine eigene Id); verwiesen wird nur über Werte.
     */
    @ArchTest
    static final ArchRule kontexteSindUnabhaengig = slices()
            .matching("de.vfh.mim.cityfeedback_api.(*)..")
            .should().notDependOnEachOther()
            .as("Bounded Contexts (und config) importieren keine Klassen voneinander");
}
