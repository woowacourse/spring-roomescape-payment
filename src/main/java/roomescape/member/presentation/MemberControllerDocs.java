package roomescape.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.SignupResponse;

import java.util.List;

@Tag(name = "회원", description = "회원 관련 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 존재하는 이메일",
                            value = "{\"message\": \"이미 존재하는 이메일입니다.\"}"
                    ),
                    @ExampleObject(
                            name = "필수 필드 누락",
                            value = "{\"message\": \"이메일과 비밀번호는 필수입니다.\"}"
                    )
            }))
    })
    ResponseEntity<SignupResponse> signup(SignupRequest request);

    @Operation(summary = "회원 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 사용자")
    })
    ResponseEntity<List<MemberResponse>> findAllMembers();
} 