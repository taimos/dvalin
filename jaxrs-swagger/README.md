## jaxrs-swagger

Add this module as a maven dependency to your POM file for documentation of your JAX-RS web service. 
OpenApi specification will be published as `swagger.{json|yaml}` and `openapi.{json|yaml}` on the root of your API URL. 
A graphical Swagger ui is served under `/swagger-ui`. By providing a bean of type `OpenApiModification` you can alter the API definition manually.
