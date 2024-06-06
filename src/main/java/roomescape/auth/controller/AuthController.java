package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.controller.dto.LoginRequest;
import roomescape.auth.controller.dto.SignUpRequest;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.handler.RequestHandler;
import roomescape.auth.handler.ResponseHandler;
import roomescape.auth.service.AuthService;

@RestController
@Tag(name = "Auth API", description = "권환 관련 API")
public class AuthController {

    private final AuthService authService;
    private final RequestHandler requestHandler;
    private final ResponseHandler responseHandler;

    public AuthController(AuthService authService, RequestHandler requestHandler, ResponseHandler responseHandler) {
        this.authService = authService;
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 토큰을 획득한다.")
    public void login(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        authService.authenticate(loginRequest);
        responseHandler.set(response, authService.createToken(loginRequest).accessToken());
    }

    @GetMapping("/login/check")
    @Operation(summary = "로그인한 유저인지 조회한다.")
    public AuthInfo checkLogin(HttpServletRequest request) {
        return authService.fetchByToken(requestHandler.extract(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃한다.")
    public void logout(HttpServletResponse response) {
        responseHandler.expire(response);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원을 추가한다.")
    public void signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
    }
}
