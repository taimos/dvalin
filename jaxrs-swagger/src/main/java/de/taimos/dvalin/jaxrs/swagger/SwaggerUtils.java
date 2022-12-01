package de.taimos.dvalin.jaxrs.swagger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.core.jackson.mixin.ComponentsMixin;
import io.swagger.v3.core.jackson.mixin.DateSchemaMixin;
import io.swagger.v3.core.jackson.mixin.ExampleMixin;
import io.swagger.v3.core.jackson.mixin.ExtensionsMixin;
import io.swagger.v3.core.jackson.mixin.MediaTypeMixin;
import io.swagger.v3.core.jackson.mixin.OpenAPIMixin;
import io.swagger.v3.core.jackson.mixin.OperationMixin;
import io.swagger.v3.core.jackson.mixin.SchemaMixin;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.links.LinkParameter;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.EncodingProperty;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
public final class SwaggerUtils {

    /**
     * Serializer for enums which uses toString() instead of name()
     *
     * @param <E> the enum to serialize
     */
    private static class EnumToStringSerializer<E extends Enum<E>> extends StdSerializer<E> {
        private static final long serialVersionUID = 8616061427277361055L;

        EnumToStringSerializer(Class<E> enumClass) {
            super(enumClass);
        }

        @Override
        public void serialize(E value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.toString());
        }
    }

    private SwaggerUtils() {
        // prevent init
    }

    /**
     * @return swagger module
     */
    public static SimpleModule createSwaggerModule() {
        // workaround to fix broken serialization of swagger enum :/
        return new SimpleModule().addSerializer(SecurityScheme.Type.class, new EnumToStringSerializer<>(SecurityScheme.Type.class));
    }

    /**
     * see <a href="https://github.com/swagger-api/swagger-core/issues/3702">Github Issue 3702</a>
     * @return mixin map
     */
    public static Map<Class<?>, Class<?>> getSwaggerMixInMap() {
        Map<Class<?>, Class<?>> mixins = new LinkedHashMap<>();
        mixins.put(ApiResponses.class, ExtensionsMixin.class);
        mixins.put(ApiResponse.class, ExtensionsMixin.class);
        mixins.put(Callback.class, ExtensionsMixin.class);
        mixins.put(Components.class, ComponentsMixin.class);
        mixins.put(Contact.class, ExtensionsMixin.class);
        mixins.put(Encoding.class, ExtensionsMixin.class);
        mixins.put(EncodingProperty.class, ExtensionsMixin.class);
        mixins.put(Example.class, ExampleMixin.class);
        mixins.put(ExternalDocumentation.class, ExtensionsMixin.class);
        mixins.put(Header.class, ExtensionsMixin.class);
        mixins.put(Info.class, ExtensionsMixin.class);
        mixins.put(License.class, ExtensionsMixin.class);
        mixins.put(Link.class, ExtensionsMixin.class);
        mixins.put(LinkParameter.class, ExtensionsMixin.class);
        mixins.put(io.swagger.v3.oas.models.media.MediaType.class, MediaTypeMixin.class);
        mixins.put(OAuthFlow.class, ExtensionsMixin.class);
        mixins.put(OAuthFlows.class, ExtensionsMixin.class);
        mixins.put(OpenAPI.class, OpenAPIMixin.class);
        mixins.put(Operation.class, OperationMixin.class);
        mixins.put(Parameter.class, ExtensionsMixin.class);
        mixins.put(PathItem.class, ExtensionsMixin.class);
        mixins.put(Paths.class, ExtensionsMixin.class);
        mixins.put(RequestBody.class, ExtensionsMixin.class);
        mixins.put(Scopes.class, ExtensionsMixin.class);
        mixins.put(SecurityScheme.class, ExtensionsMixin.class);
        mixins.put(Server.class, ExtensionsMixin.class);
        mixins.put(ServerVariable.class, ExtensionsMixin.class);
        mixins.put(ServerVariables.class, ExtensionsMixin.class);
        mixins.put(Tag.class, ExtensionsMixin.class);
        mixins.put(XML.class, ExtensionsMixin.class);
        mixins.put(Schema.class, SchemaMixin.class);
        mixins.put(DateSchema.class, DateSchemaMixin.class);
        return mixins;
    }
}
