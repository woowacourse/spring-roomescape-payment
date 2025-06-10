package roomescape.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import roomescape.common.argumentResolver.Login;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.dto.response.LoginCheckResponse;

@Tag(name = "로그인", description = "로그인 관련 API")
public interface LoginControllerDocs {

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "잘못된 이메일 또는 비밀번호",
                            value = "{\"message\": \"이메일 또는 비밀번호가 일치하지 않습니다.\"}"
                    )
            }))
    })
    ResponseEntity<Void> login(LoginRequest request, HttpServletResponse response);

    @Operation(summary = "로그인 상태 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 상태 확인 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<LoginCheckResponse> loginCheck(@Login LoginMember loginMember);

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    ResponseEntity<Void> logout(HttpServletResponse response);
} 