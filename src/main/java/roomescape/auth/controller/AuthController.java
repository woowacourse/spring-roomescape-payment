package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.dto.LoginResponse;
import roomescape.auth.service.AuthService;
import roomescape.common.exception.dto.ErrorResponse;

@Tag(name = "인증", description = "인증 API")
@Slf4j
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "잘못된 이메일 또는 비밀번호",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<Void> createToken(@RequestBody @Valid final LoginRequest request) {
        log.info("로그인 요청: email={}", request.email());
        LoginResponse loginResponse = authService.login(request);
        String tokenValue = loginResponse.tokenValue();
        ResponseCookie cookie = ResponseCookie.from("token", tokenValue)
                .path("/")
                .httpOnly(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Operation(
            summary = "로그인 상태 확인",
            description = "요청 쿠키의 토큰을 기반으로 사용자가 로그인 상태인지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 상태",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/login/check")
    public ResponseEntity<LoginCheckResponse> checkLogin(final LoginMember member) {
        if (member == null) {
            log.warn("로그인 체크 실패: 인증되지 않은 사용자");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginCheckResponse("unauthorized"));
        }
        return ResponseEntity.ok(new LoginCheckResponse(member.name()));
    }

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("로그아웃 요청");
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
        log.info("로그아웃 완료");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
