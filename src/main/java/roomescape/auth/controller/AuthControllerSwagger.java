package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.auth.dto.LoginRequest;
import roomescape.member.dto.MemberLoginCheckResponse;

import static roomescape.config.SwaggerConfig.JWT_TOKEN_COOKIE_AUTH;

@Tag(name = "Auth", description = "인증 기능을 제공하는 API")
public interface AuthControllerSwagger {

    @Operation(
            summary = "로그인",
            description = "사용자 이름과 비밀번호를 이용하여 로그인합니다.",
            requestBody = @RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공, JWT 토큰이 담긴 쿠키를 반환합니다.",
                            content = @Content(schema = @Schema(hidden = true)),
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "JWT 토큰을 포함한 쿠키. 예: 'Token=abc123; Path=/; HttpOnly'"
                                    )
                            }
                    )
            }
    )
    ResponseEntity<Void> login(LoginRequest loginRequest);

    @SecurityRequirement(name = JWT_TOKEN_COOKIE_AUTH)
    @Operation(
            summary = "로그인 상태 확인",
            description = "로그인된 사용자의 정보를 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자 로그인 정보",
                            content = @Content(
                                    schema = @Schema(implementation = MemberLoginCheckResponse.class)
                            )
                    )
            }
    )
    MemberLoginCheckResponse loginCheck(@Parameter(hidden = true) Long id);
}
