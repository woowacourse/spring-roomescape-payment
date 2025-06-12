package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import roomescape.common.exception.error.ErrorResponse;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.controller.dto.MemberInfoResponse;
import roomescape.member.controller.dto.SignupRequest;

@Tag(name = "인증 관리", description = "인증 API")
public interface SwaggerAuthController {

    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (존재하지 않는 이메일 또는 비밀번호 불일치)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> login(LoginRequest loginRequest);

    @Operation(summary = "로그아웃", description = "로그아웃합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    ResponseEntity<Void> logout();

    @Operation(summary = "로그인 확인", description = "현재 로그인 상태를 확인합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 확인 성공", content = @Content(schema = @Schema(implementation = LoginCheckResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 상태가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<LoginCheckResponse> checkLogin(@Parameter(hidden = true) HttpServletRequest request);

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름으로 회원가입합니다.", responses = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberInfoResponse.class))),
            @ApiResponse(responseCode = "409", description = "회원가입 실패 (이미 존재하는 이메일)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<MemberInfoResponse> signup(SignupRequest signupRequest);
}
