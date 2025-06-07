package roomescape.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.controller.apidocs.annotation.SecurityDocs;
import roomescape.dto.request.SignupRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.exception.dto.ErrorResponse;

@Tag(name = "[사용자 API]")
@SecurityRequirement(name = "cookieAuth")
public interface MemberApi {

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "전체 사용자 데이터 조회", description = "모든 사용자 정보를 조회합니다. 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "전체 사용자 데이터 조회 성공", value = """
                    [
                       {
                         "id": 1,
                         "name": "플린트"
                       },
                       {
                         "id": 2,
                         "name": "조로"
                       }
                     ]
                    """)
    }))
    ResponseEntity<List<MemberResponse>> getMembers();

    @Operation(summary = "회원 가입", description = "사용자가 입력한 이름, 이메일, 패스워드를 기반으로 회원 가입합니다.",
            requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SignupRequest.class),
                    examples = @ExampleObject(name = "회원 가입 요청 예시", value = """
                            {
                                "name": "testapi",
                                "email": "testapi@testapi.com",
                                "password": "testapi"
                            }
                            """))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 가입 성공", content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "회원 가입 성공", value = """
                                    {
                                        "id": 4,
                                        "name": "testapi"
                                    }
                                    """)
                            }))
            }
    )
    @ApiResponse(responseCode = "400", description = "회원 가입 실패", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "이메일 중복", value = """
                    {
                        "message": "동일한 이메일로 추가할 수 없습니다."
                    }
                    """
            )))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "이름은 비어있을 수 없습니다."
                        },
                        {
                            "message": "이메일은 비어있을 수 없습니다."
                        },
                        {
                            "message": "비밀번호는 비어있을 수 없습니다."
                        }
                    ]
                    """)))
    ResponseEntity<MemberResponse> signUp(SignupRequest request);
}
