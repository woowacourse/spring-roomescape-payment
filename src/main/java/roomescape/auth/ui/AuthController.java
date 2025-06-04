package roomescape.auth.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.auth.application.AuthService;
import roomescape.auth.application.dto.LoginCheckResponse;
import roomescape.auth.application.dto.LoginRequest;
import roomescape.common.response.ApiResponse;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @Operation(
            summary = "로그인 요청",
            description = "사용자의 로그인 정보를 받아 토큰을 생성하고, 쿠키에 토큰을 담아 반환합니다. 성공 시 응답 본문은 비어있고 쿠키가 설정됩니다."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        ResponseCookie cookie = cookieProvider.createCookieForLogin(token);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }

    @Operation(
            summary = "로그인 상태 확인",
            description = "요청에 포함된 토큰을 기반으로 현재 로그인된 사용자의 정보를 확인합니다. 유효한 토큰일 경우 사용자 ID를 반환합니다."
    )
    @GetMapping("/login/check")
    public ResponseEntity<ApiResponse<LoginCheckResponse>> checkLogin(@LoginMemberId Long memberId) {
        LoginCheckResponse response = authService.loginCheck(memberId);
        ApiResponse<LoginCheckResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "로그아웃 요청",
            description = "사용자의 인증 쿠키를 삭제하여 로그아웃 처리합니다. 응답 본문은 비어있고, 삭제된 쿠키가 포함됩니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie cookie = cookieProvider.createCookieForLogout();
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(apiResponse);
    }
}
