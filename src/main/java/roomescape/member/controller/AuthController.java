package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import roomescape.member.controller.dto.LoginCheckResponse;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.controller.dto.MemberInfoResponse;
import roomescape.member.controller.dto.SignupRequest;

@Tag(name = "로그인 API")
public interface AuthController {

    @Operation(summary = "로그인")
    ResponseEntity<Void> login(@RequestBody(required = true) LoginRequest loginRequest);

    @Operation(summary = "로그아웃", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<Void> logout();

    @Operation(summary = "로그인 정보 조회", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<LoginCheckResponse> checkLogin(HttpServletRequest request);

    @Operation(summary = "회원가입")
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = SignupRequest.class),
                    examples = {
                            @ExampleObject(name = "sample",
                                    value = "{\"email\":\"abc@gmail.com\",\"password\":\"qwe123\",\"name\":\"leo\"}")
                    }
            )
    )
    ResponseEntity<MemberInfoResponse> signup(SignupRequest signupRequest);
}
