package roomescape.config.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import roomescape.exception.ExceptionTemplate;

@Retention(RetentionPolicy.SOURCE)
public @interface ApiErrorResponse {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ExceptionTemplate.class))
            )
    })
    @interface BadRequest {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionTemplate.class))
            )
    })
    @interface Unauthorized {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ExceptionTemplate.class))
            )
    })
    @interface Forbidden {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500", description = "3rd party API Error",
                    content = @Content(schema = @Schema(implementation = ExceptionTemplate.class))
            )
    })
    @interface ThirdPartyApiError {
    }
}
