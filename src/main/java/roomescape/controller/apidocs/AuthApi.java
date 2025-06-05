package roomescape.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.LoginRequest;
import roomescape.exception.dto.ErrorResponse;

@Tag(name = "[인증 API]")
@SecurityRequirement(name = "cookieAuth")
public interface AuthApi {

    @Operation(summary = "로그인", description = "사용자가 입력한 이메일, 패스워드를 기반으로 로그인합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(name = "로그인 요청 예시", value = """
                            {
                                "email": "user@example.com",
                                "password": "password123"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            headers = {@Header(name = "Set-Cookie",
                                    description = "인증 토큰이 포함된 쿠키",
                                    schema = @Schema(type = "string", example = "token=eyJhbGciOiJ...; HttpOnly; Path=/; Max-Age=3600"))},
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 로그인 정보",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                        "message": "이메일 또는 비밀번호가 일치하지 않습니다."
                                    }
                                    """)
                            )
                    )})
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "이메일은 비어있을 수 없습니다."
                        },
                        {
                            "message": "비밀번호는 비어있을 수 없습니다."
                        }
                    ]
                    """)))
    ResponseEntity<Void> login(LoginRequest request, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "유저 정보 검증", description = "사용자 쿠키 토큰을 기반으로 유저 정보를 검증합니다.",
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
                    examples = {@ExampleObject(name = "사용자 토큰 검증 성공", value = """
                            {
                                "id": 1,
                                "name": "플린트",
                                "role": "USER"
                            }
                            """)
                    })),
                    @ApiResponse(responseCode = "401", description = "토큰 만료",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                        "message": "토큰이 만료되었습니다."
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 검증 실패",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                    {
                                        "message": "유효하지 않은 토큰입니다."
                                    }
                                    """)
                            )
                    )
            })
    ResponseEntity<LoginMemberRequest> checkLogin(@Parameter(hidden = true) LoginMemberRequest loginMemberRequest);


    @Operation(summary = "로그아웃", description = "사용자를 로그아웃합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공(기존 토큰은 만료 처리합니다.)",
                            headers = {@Header(name = "Set-Cookie",
                                    description = "인증 토큰이 포함된 쿠키",
                                    schema = @Schema(type = "string", example = "token=\"\"; HttpOnly; Path=/; Max-Age=0"))},
                            content = @Content(mediaType = "application/json")),
            })
    ResponseEntity<Void> logout(@Parameter(hidden = true) HttpServletResponse response);
}
