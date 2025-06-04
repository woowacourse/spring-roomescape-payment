package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.LoginResponse;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.global.LoginInfo;

import javax.naming.AuthenticationException;

@Tag(name = "1. 보안 관련 API")
public interface MemberAuthApi {

    @Operation(summary = "회원가입")
    ResponseEntity<MemberRegisterResponse> register(
            @RequestBody(required = true) MemberRegisterRequest request
    );

    @Operation(summary = "로그인")
    ResponseEntity<Void> login(
            @RequestBody(required = true) LoginRequest loginRequest,
            @Parameter(hidden = true) HttpSession session
    ) throws AuthenticationException;

    @Operation(summary = "로그인 정보 확인")
    ResponseEntity<LoginResponse> loginCheck(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "로그아웃")
    ResponseEntity<Void> logout(
            @Parameter(hidden = true) HttpSession session
    );
}
