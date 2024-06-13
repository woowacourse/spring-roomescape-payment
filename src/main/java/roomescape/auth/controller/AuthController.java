package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.controller.dto.LoginRequest;
import roomescape.auth.controller.dto.SignUpRequest;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.handler.RequestHandler;
import roomescape.auth.handler.ResponseHandler;
import roomescape.auth.service.AuthService;

@Tag(name = "Auth", description = "Auth API")
@RestController
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
    public void login(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        authService.authenticate(loginRequest);
        responseHandler.set(response, authService.createToken(loginRequest).accessToken());
    }

    @GetMapping("/login/check")
    public AuthInfo checkLogin(HttpServletRequest request) {
        return authService.fetchByToken(requestHandler.extract(request));
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        responseHandler.expire(response);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
    }
}
