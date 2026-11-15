package de.vfh.mim.cityfeedback_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Allgemeine Angaben für die OpenAPI-/Swagger-Dokumentation.
 * <p>
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * OpenAPI (JSON): http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cityFeedbackOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CityFeedback API")
                        .version("0.0.1")
                        .description("REST-Schnittstelle des CityFeedback-Meldungssystems "
                                + "(Semesteraufgabe \"Moderne Softwareentwicklung\" WS 26/27)."));
    }
}
