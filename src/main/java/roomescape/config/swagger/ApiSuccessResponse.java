package roomescape.config.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.SOURCE)
public @interface ApiSuccessResponse {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @interface Ok {
        @AliasFor(annotation = Operation.class, attribute = "summary")
        String value() default "";

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created")
    })
    @interface Created {
        @AliasFor(annotation = Operation.class, attribute = "summary")
        String value() default "";

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content")
    })
    @interface NoContent {
        @AliasFor(annotation = Operation.class, attribute = "summary")
        String value() default "";

        @AliasFor(annotation = Operation.class, attribute = "description")
        String description() default "";
    }
}
