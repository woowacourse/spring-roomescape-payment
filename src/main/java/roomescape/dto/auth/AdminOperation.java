package roomescape.dto.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "관리자 API", description = "관리자 권한이 없으면 사용하지 못합니다.")
@ApiResponses({
        @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
})
public @interface AdminOperation {
}