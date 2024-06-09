package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.response.member.MemberResponse;

@Tag(name = "사용자 인증", description = "사용자 인증 API")
interface AuthenticationControllerDocs {
    String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Operation(summary = "로그인", description = "사용자가 로그인을 한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공하면, 쿠키에 인증 토큰이 전달된다.",
                    content = @Content(examples = @ExampleObject("Cookie:token=" + TOKEN))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 이메일이 존재하지 않는 경우
                                                        
                            (2) 비밀번호가 잘못된 경우
                            """,
                    content = @Content(examples = @ExampleObject("존재하지 않는 이메일입니다"))
            )
    })
    ResponseEntity<Void> login(@Valid LoginRequest request);

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃을 한다.")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공하면, 쿠키에 인증 토큰이 제거된다.",
                    content = @Content(examples = @ExampleObject("Cookie:token=;"))

            )
    )
    ResponseEntity<Void> logout();

    @Operation(summary = "로그인 체크", description = "사용자가 로그인이 되어있는지 확인한다. 성공을 위해서 인증 토큰이 필요하다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 체크 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 체크 실패",
                    content = @Content(examples = @ExampleObject("인증에 실패하였습니다."))
            )
    })
    ResponseEntity<MemberResponse> checkAuthenticated(
            @Parameter(in = ParameterIn.COOKIE, description = "인증 토큰", example = TOKEN) String token
    );
}
