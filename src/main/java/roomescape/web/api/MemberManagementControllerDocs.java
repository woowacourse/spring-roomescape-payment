package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.application.dto.response.member.MemberResponse;

@Tag(name = "사용자 관리", description = "사용자 관리 API")
public interface MemberManagementControllerDocs {

    @Operation(summary = "회원가입", description = "회원가입을 한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 이미 가입한 이메일인 경우
                            """,
                    content = @Content(examples = @ExampleObject("이미 가입한 이메일입니다"))
            )
    })
    ResponseEntity<Void> signup(@Valid SignupRequest request);

    @Operation(summary = "전체 회원 조회", description = "전체 회원을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 회원 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class)),
                            examples = @ExampleObject(value = """
                                    [
                                         {"id": 1, "name": "썬"},
                                         {"id": 2, "name": "오리"},
                                         {"id": 3, "name": "재즈"},
                                         {"id": 4, "name": "망쵸"}
                                    ]
                                    """
                            )
                    )
            )
    })
    ResponseEntity<List<MemberResponse>> findAllMembers();

    @Operation(summary = "회원 탈퇴", description = "회원이 서비스를 탈퇴한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "회원 탈퇴 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 존재하지 않는 회원인 경우
                            """,
                    content = @Content(examples = @ExampleObject("존재하지 않는 회원입니다"))
            )
    })
    ResponseEntity<Void> withdrawal(
            @Parameter(description = "멤버 ID", example = "1") Long memberId
    );
}
