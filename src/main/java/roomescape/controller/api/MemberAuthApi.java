package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.LoginResponse;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.global.LoginInfo;
import roomescape.global.exception.ErrorResponse;

import javax.naming.AuthenticationException;

@Tag(name = "1. 보안 관련 API")
public interface MemberAuthApi {

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "이메일이 중복된 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이름이 중복된 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<MemberRegisterResponse> register(
            @RequestBody(required = true) MemberRegisterRequest request
    );

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일인 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> login(
            @RequestBody(required = true) LoginRequest loginRequest,
            @Parameter(hidden = true) HttpSession session
    ) throws AuthenticationException;

    @Operation(summary = "로그인 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<LoginResponse> loginCheck(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 응답"),
    })
    ResponseEntity<Void> logout(
            @Parameter(hidden = true) HttpSession session
    );
}
