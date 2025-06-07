package roomescape.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.exception.handler.ErrorResponse;
import roomescape.common.security.annotation.RequireRole;
import roomescape.common.security.application.AuthService;
import roomescape.common.security.dto.request.LoginRequest;
import roomescape.common.security.dto.request.MemberInfo;
import roomescape.common.security.dto.response.CheckLoginResponse;
import roomescape.common.security.dto.response.LoginResponse;
import roomescape.common.security.infrastructure.CookieManager;
import roomescape.member.application.MemberApplicationService;
import roomescape.member.domain.MemberRole;
import roomescape.member.presentation.dto.request.SignupWebRequest;
import roomescape.member.presentation.dto.response.MemberWebResponse;
import roomescape.member.presentation.dto.response.SignUpWebResponse;

@RestController
@Tag(name = "멤버", description = "멤버 관련 API")
public class MemberController {

    private static final String TOKEN = "token";

    private final MemberApplicationService memberApplicationService;
    private final AuthService authService;
    private final CookieManager cookieManager;

    public MemberController(final MemberApplicationService memberApplicationService, final AuthService authService,
                            final CookieManager cookieManager) {
        this.memberApplicationService = memberApplicationService;
        this.authService = authService;
        this.cookieManager = cookieManager;
    }

    @Operation(summary = "멤버 생성",
            description = "멤버를 생성합니다.",
            responses = {
                @ApiResponse(description = "멤버 생성 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignUpWebResponse.class))),
                @ApiResponse(description = "멤버 이름이 10글자를 초과할 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(description = "중복된 이메일", responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/members")
    public ResponseEntity<SignUpWebResponse> signUp(final @RequestBody SignupWebRequest signupWebRequest) {
        SignUpWebResponse response = memberApplicationService.signup(signupWebRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 멤버 조회",
            description = "사이트에 가입된 모든 멤버의 정보를 조회합니다.",
            responses = {
                @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MemberWebResponse.class))))
            })
    @RequireRole(MemberRole.ADMIN)
    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberWebResponse>> findAllRegular() {
        return ResponseEntity.ok(memberApplicationService.findAllRegular());
    }


    @Operation(summary = "로그인",
            description = "로그인 후, 성공 시 토큰을 쿠키에 자동으로 등록합니다.",
            responses = {
                @ApiResponse(description = "로그인 성공\n\n(토큰 자동 등록)", responseCode = "200")
            })
    @PostMapping("/login")
    public ResponseEntity<Void> login(final @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieManager.makeCookie(TOKEN, loginResponse.accessToken()).toString())
                .build();
    }


    @Operation(summary = "로그인 상태 확인",
            description = "로그인 여부를 확인하며, 로그인이 되어있는 경우 현재 로그인된 멤버의 이름을 반환합니다.",
            responses = {
                @ApiResponse(description = "상태 확인 성공", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckLoginResponse.class)))
            })
    @RequireRole(MemberRole.REGULAR)
    @GetMapping("/login/check")
    public ResponseEntity<CheckLoginResponse> checkLogin(@Parameter(hidden = true) final MemberInfo memberInfo) {
        return ResponseEntity.ok(CheckLoginResponse.from(memberApplicationService.getById(memberInfo.id())));
    }


    @Operation(summary = "로그아웃",
            description = "로그인 되어있는 경우, 쿠키에서 토큰을 삭제하여 로그아웃합니다.",
            responses = {
                @ApiResponse(description = "로그아웃 성공\n\n(쿠키에서 토큰 자동 삭제)", responseCode = "200")
            })
    @RequireRole(MemberRole.REGULAR)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletResponse httpServletResponse) {
        cookieManager.deleteCookie(httpServletResponse, TOKEN);
        return ResponseEntity.noContent().build();
    }
}
