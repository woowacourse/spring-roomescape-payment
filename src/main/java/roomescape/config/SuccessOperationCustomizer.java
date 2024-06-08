package roomescape.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import roomescape.annotation.ApiSuccessResponse;

@Component
public class SuccessOperationCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiSuccessResponse methodAnnotation = handlerMethod.getMethodAnnotation(ApiSuccessResponse.class);
        if (methodAnnotation != null) {
            HttpStatus status = methodAnnotation.status();
            String body = methodAnnotation.body();
            Class<?> bodyType = methodAnnotation.bodyType();

            ApiResponses responses = operation.getResponses();
            responses.remove("200");
            ApiResponse item = makeResponse(body, bodyType);
            responses.addApiResponse(String.valueOf(status.value()), item);
        }
        return operation;
    }

    private ApiResponse makeResponse(String body, Class<?> bodyType) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.description("요청이 정상적으로 처리된 경우");
        Content content = makeContent(body, bodyType);
        apiResponse.setContent(content);
        return apiResponse;
    }

    private Content makeContent(String body, Class<?> bodyType) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        if (!body.equals("")) {
            mediaType.addExamples("success", makeExample(body));
        }
        if (!bodyType.getSimpleName().equals("Void")) {
            mediaType.setSchema(makeSchema(bodyType));
        }
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private Schema<?> makeSchema(Class<?> bodyType) {
        Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/" + bodyType.getSimpleName());
        return schema;
    }

    private Example makeExample(String exampleBody) {
        Example example = new Example();
        example.setValue(exampleBody);
        return example;
    }
}
