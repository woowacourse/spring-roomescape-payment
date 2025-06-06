package roomescape.controller.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.LoginResponse;

@Tag(name = "인증 관리", description = "로그인, 로그아웃 및 인증 상태 확인 API")
public interface LoginControllerDocs {

    @Operation(
            summary = "로그인 페이지",
            description = "로그인 페이지를 반환합니다.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "로그인 페이지 반환 성공"
            )
    )
    String loginPage();

    @Operation(
            summary = "로그인",
            description = """
        사용자 로그인을 처리합니다.
        1. 이메일로 사용자를 찾습니다.
        2. 비밀번호를 검증합니다.
        3. 성공 시 세션이 생성되며, 세션 유효기간은 1시간입니다.
        4. 사용자의 세션 ID를 업데이트합니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 - 세션이 생성됨"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 (유효성 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "이메일 형식 오류",
                                    value = "{\"errorMessage\": \"이메일 형식이 올바르지 않습니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자 (이메일로 사용자를 찾을 수 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 사용자",
                                    value = "{\"errorMessage\": \"존재하지 않는 사용자입니다.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "비밀번호 불일치 (인증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "비밀번호 불일치",
                                    value = "{\"errorMessage\": \"비밀번호가 일치하지 않습니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<Void> login(
            @RequestBody(
                    description = "로그인 요청 데이터 - 이메일 형식 검증 필요",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "로그인 요청 예시",
                                    summary = "유효한 이메일 형식과 비밀번호",
                                    value = """
                    {
                        "email": "user@example.com",
                        "password": "password123"
                    }
                    """
                            )
                    )
            )
            @Valid LoginRequest loginRequest,
            HttpSession session
    );

    @Operation(
            summary = "로그아웃",
            description = """
        현재 사용자의 세션을 무효화하여 로그아웃을 처리합니다.
        세션이 완전히 삭제되며, 로그아웃 후에는 인증이 필요한 API 호출 시 401 에러가 발생합니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공 - 세션이 무효화됨"
            )
    })
    ResponseEntity<Void> logout(HttpSession session);

    @Operation(
            summary = "로그인 상태 확인",
            description = """
        현재 사용자의 로그인 상태를 확인합니다.
        세션에서 사용자 ID를 조회하고, 해당 사용자의 이름을 반환합니다.
        로그인되지 않은 상태라면 401 상태코드를 반환합니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인된 상태 - 사용자 이름 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "name": "홍길동"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 상태 (세션에 사용자 정보 없음)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "세션의 사용자 ID로 사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "사용자 조회 실패",
                                    value = "{\"errorMessage\": \"존재하지 않는 사용자입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<LoginResponse> loginCheck(HttpSession session);
}
