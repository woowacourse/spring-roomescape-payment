package roomescape.global.document;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import roomescape.auth.Authenticated;
import org.springframework.core.MethodParameter;

@Component
@Order(1)
public class AuthenticatedSwaggerPlugin implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Arrays.stream(handlerMethod.getMethodParameters())
                .filter(parameter -> parameter.hasParameterAnnotation(Authenticated.class))
                .forEach(parameter -> {
                    Parameter tokenParameter = new Parameter()
                            .in(In.HEADER.toString())
                            .name("token")
                            .description("member login token")
                            .required(true)
                            .schema(new StringSchema());
                    operation.addParametersItem(tokenParameter);
                    operation.getParameters().removeIf(p -> p.getName().equals(parameter.getParameter().getName()));
                    Optional.ofNullable(operation.getRequestBody())
                            .map(RequestBody::getContent)
                            .map(content -> content.get("application/json"))
                            .map(MediaType::getSchema)
                            .map(Schema::getProperties)
                            .ifPresent(properties -> properties.remove(parameter.getParameter().getName()));
                });
        return operation;
    }
}

