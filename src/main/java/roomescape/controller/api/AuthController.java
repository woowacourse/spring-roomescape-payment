package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.annotation.CurrentMember;
import roomescape.controller.util.CookieHandler;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.auth.LoginRequestDto;
import roomescape.dto.member.MemberNameResponseDto;
import roomescape.service.query.AuthQueryService;

@Tag(name = "인증 API")
@RestController
public class AuthController {

    private static final String TOKEN_COOKIE_NAME = "token";

    private final AuthQueryService authQueryService;
    private final CookieHandler cookieHandler;

    public AuthController(AuthQueryService authQueryService, CookieHandler cookieHandler) {
        this.authQueryService = authQueryService;
        this.cookieHandler = cookieHandler;
    }

    @Operation(summary = "로그인")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        String token = authQueryService.publishLoginToken(loginRequestDto);
        Cookie cookie = cookieHandler.createCookie(TOKEN_COOKIE_NAME, token);
        response.addCookie(cookie);
    }

    @Operation(summary = "로그인 상태 확인")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/login/check")
    @ResponseStatus(HttpStatus.OK)
    public MemberNameResponseDto checkLogin(
            @CurrentMember LoginInfo loginMember
    ) {
        return new MemberNameResponseDto(loginMember.name());
    }

    @Operation(summary = "로그아웃")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(
            HttpServletResponse response
    ) {
        Cookie cookie = cookieHandler.createCookie(TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
