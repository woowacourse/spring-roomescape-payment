package roomescape.system.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.system.auth.annotation.MemberId;
import roomescape.system.auth.dto.LoginCheckResponse;
import roomescape.system.auth.dto.LoginRequest;
import roomescape.system.auth.jwt.dto.TokenDto;
import roomescape.system.auth.service.AuthService;
import roomescape.system.dto.response.ErrorResponse;
import roomescape.system.dto.response.RoomEscapeApiResponse;

@RestController
@Tag(name = "1. 인증 / 인가 API", description = "로그인, 로그아웃 및 로그인 상태를 확인합니다")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "로그인", description = "입력받은 이메일, 비밀번호로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공시 쿠키에 토큰 정보를 저장합니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원이거나, 이메일 또는 비밀번호가 잘못 입력되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<Void> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        TokenDto tokenInfo = authService.login(loginRequest);
        addCookieToResponse(new Cookie("accessToken", tokenInfo.accessToken()), response);
        return RoomEscapeApiResponse.success();
    }

    @GetMapping("/login/check")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "로그인 상태 확인", description = "로그인 상태를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 상태이며, 로그인된 회원의 이름을 반환합니다."),
            @ApiResponse(responseCode = "400", description = "쿠키에 있는 토큰 정보로 회원을 조회할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인되지 않은 상태이며, 로그인이 필요합니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RoomEscapeApiResponse<LoginCheckResponse> checkLogin(@MemberId @Parameter(hidden = true) Long memberId) {
        LoginCheckResponse response = authService.checkLogin(memberId);
        return RoomEscapeApiResponse.success(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "로그아웃", description = "현재 로그인된 회원을 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공시 쿠키에 저장된 토큰 정보를 삭제합니다.")
    })
    public RoomEscapeApiResponse<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = getTokenCookie(request);
        cookie.setValue(null);
        cookie.setMaxAge(0);
        addCookieToResponse(cookie, response);
        return RoomEscapeApiResponse.success();
    }

    private Cookie getTokenCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("accessToken")) {
                return cookie;
            }
        }
        return new Cookie("accessToken", null);
    }

    private void addCookieToResponse(Cookie cookie, HttpServletResponse response) {
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
    }
}
