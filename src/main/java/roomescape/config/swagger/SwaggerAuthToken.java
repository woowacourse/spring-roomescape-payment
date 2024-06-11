package roomescape.config.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import roomescape.auth.AuthConstants;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        in = ParameterIn.COOKIE, name = AuthConstants.AUTH_COOKIE_NAME, description = "쿠키에 저장된 JWT 입니다.",
        required = true, schema = @Schema(implementation = String.class)
)
public @interface SwaggerAuthToken {
}
