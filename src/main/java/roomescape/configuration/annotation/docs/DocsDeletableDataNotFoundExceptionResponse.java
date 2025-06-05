package roomescape.configuration.annotation.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.ProblemDetail;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "404", description = "삭제할 데이터가 존재하지 않습니다.",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
public @interface DocsDeletableDataNotFoundExceptionResponse {

}
