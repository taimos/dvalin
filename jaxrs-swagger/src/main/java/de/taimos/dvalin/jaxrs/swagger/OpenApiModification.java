package de.taimos.dvalin.jaxrs.swagger;

import io.swagger.v3.oas.models.OpenAPI;

@FunctionalInterface
public interface OpenApiModification {

    void reconfigure(OpenAPI openAPI);

}
