package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "요청 정보가 잘못되었거나 일치하는 유저가 존재하지 않는 경우")
    })
    @Parameter(name = "loginRequest", description = "로그인 정보 DTO")
    public void login(HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        authService.authenticate(loginRequest);
        responseHandler.set(response, authService.createToken(loginRequest).accessToken());
    }

    @GetMapping("/login/check")
    @Operation(summary = "로그인한 유저인지 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않는 경우")
    })
    public AuthInfo checkLogin(HttpServletRequest request) {
        return authService.fetchByToken(requestHandler.extract(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃한다.")
    @ApiResponse(responseCode = "200", description = "OK")
    public void logout(HttpServletResponse response) {
        responseHandler.expire(response);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원을 추가한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일인 경우")
    })
    @Parameter(name = "signUpRequest", description = "추가하고자 하는 회원 정보 DTO")
    public void signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
    }
}
