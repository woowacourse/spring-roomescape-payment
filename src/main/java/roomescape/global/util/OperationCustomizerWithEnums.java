package roomescape.global.util;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;
import roomescape.exception.ErrorType;
import roomescape.exception.ErrorTypeGroup;
import roomescape.exception.ExceptionResponse;


import io.swagger.v3.oas.models.Operation;
import roomescape.global.annotation.ApiErrorResponse;
import roomescape.global.annotation.ApiErrorResponses;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class OperationCustomizerWithEnums implements OperationCustomizer {

    @SuppressWarnings("rawtypes")
    private final Schema errorEntitySchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(ExceptionResponse.class).schema;


    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses apiResponses = operation.getResponses();
        ApiErrorResponses apiResponseCodes = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
        ApiErrorResponse apiErrorResponse = handlerMethod.getMethodAnnotation(ApiErrorResponse.class);
        if (apiResponseCodes != null) {
            putApiErrorResponsesCode(apiResponses, apiResponseCodes);
        }
        if (apiErrorResponse != null) {
            putApiErrorResponseCode(apiResponses, apiErrorResponse.value());
        }
        return operation;
    }

    private void putApiErrorResponsesCode(ApiResponses apiResponses, ApiErrorResponses apiErrorResponses) {
        Arrays.stream(apiErrorResponses.value())
                .forEach(code -> putApiErrorResponseCode(apiResponses, code));
        Arrays.stream(apiErrorResponses.groups())
                .forEach(errorTypeGroup -> putApiGroupResponseCode(apiResponses, errorTypeGroup));
    }

    private void putApiGroupResponseCode(ApiResponses apiResponses, ErrorTypeGroup errorTypeGroup) {
        errorTypeGroup.getErrorTypes()
                .forEach(errorType -> putApiErrorResponseCode(apiResponses, errorType));
    }

    private void putApiErrorResponseCode(ApiResponses apiResponses, ErrorType code) {
        apiResponses.put(code.getFormattedMessage(), convertErrorResponse(code));
    }

    private ApiResponse convertErrorResponse(ErrorType code) {
        return convertResponseInner(
                errorEntitySchema.description(code.getMessage()),
                code,
                ExceptionResponse.of(code)
        );
    }

    private ApiResponse convertResponseInner(@SuppressWarnings("rawtypes") Schema schema, ErrorType code, ExceptionResponse example) {
        MediaType mediaType = new MediaType()
                .schema(schema);
        if (example != null) {
            mediaType.addExamples(code.name(), new Example().value(example));
        }

        return new ApiResponse()
                .content(
                        new Content()
                                .addMediaType(
                                        APPLICATION_JSON_VALUE,
                                        mediaType
                                )
                )
                .description(code.getMessage());
    }
}

