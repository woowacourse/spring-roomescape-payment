package roomescape.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.core.AuthorizationManager;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.dto.response.GetAuthInfoResponse;
import roomescape.auth.dto.response.LoginResponse;
import roomescape.auth.service.AuthService;

@Tag(name = "인증 API", description = "인증 관련 API")
@RestController
public class AuthController {

    private final AuthorizationManager authorizationManager;
    private final AuthService authService;

    public AuthController(final AuthorizationManager authorizationManager, final AuthService authService) {
        this.authorizationManager = authorizationManager;
        this.authService = authService;
    }

    @Operation(summary = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 이메일은 메일 형식만 가능합니다.
                    2. 이메일은 공백 문자가 불가능합니다.
                    3. 비밀번호는 공백 문자가 불가능합니다.
                    4. 아이디 또는 비밀번호를 잘못 입력했습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "이메일에 해당되는 회원이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpServletResponse httpServletResponse, @RequestBody @Valid LoginRequest loginMemberRequest) {
        LoginResponse loginResponse = authService.login(loginMemberRequest);
        authorizationManager.setAuthorization(httpServletResponse, loginResponse.token());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 인증 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 회원이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/login/check")
    public ResponseEntity<GetAuthInfoResponse> getMemberAuthInfo(@AuthenticationPrincipal AuthInfo authInfo) {
        GetAuthInfoResponse getAuthInfoResponse = authService.getMemberAuthInfo(authInfo);
        return ResponseEntity.ok(getAuthInfoResponse);
    }

    @Operation(summary = "로그아웃 API")
    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        authorizationManager.removeAuthorization(httpServletResponse);
        return ResponseEntity.noContent().build();
    }
}
