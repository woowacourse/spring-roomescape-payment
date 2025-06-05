package roomescape.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.annotation.SecurityDocs;
import roomescape.dto.request.CreateThemeRequest;
import roomescape.dto.response.ThemeResponse;
import roomescape.exception.dto.ErrorResponse;

@Tag(name = "[방탈출 테마 API]")
@SecurityRequirement(name = "cookieAuth")
public interface ThemeApi {

    @Operation(summary = "전체 테마 조회", description = "모든 방탈출 테마를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "name": "공포의 방",
                            "description": "무서운 공포 테마입니다",
                            "thumbnail": "horror-room.jpg"
                        },
                        {
                            "id": 2,
                            "name": "추리의 방",
                            "description": "추리 테마입니다",
                            "thumbnail": "detective-room.jpg"
                        }
                    ]
                    """)
    )
    )
    ResponseEntity<List<ThemeResponse>> getThemes();

    @Operation(summary = "인기 테마 조회", description = "인기 있는 방탈출 테마를 조회합니다. 조회 날짜 기준 7일 이내의 예약 횟수를 기준으로 내림차순 정렬합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "name": "공포의 방",
                            "description": "무서운 공포 테마입니다",
                            "thumbnail": "horror-room.jpg"
                        },
                        {
                            "id": 2,
                            "name": "추리의 방",
                            "description": "추리 테마입니다",
                            "thumbnail": "detective-room.jpg"
                        }
                    ]
                    """)
    )
    )
    ResponseEntity<List<ThemeResponse>> popularThemes();

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "테마 추가", description = "새로운 방탈출 테마를 추가합니다. 관리자만 사용 가능합니다.")
    @ApiResponse(responseCode = "201", description = "테마 추가 성공", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "id": 3,
                        "name": "신비의 방",
                        "description": "신비한 테마입니다",
                        "thumbnail": "mystery-room.jpg"
                    }
                    """)))
    @ApiResponse(responseCode = "400", description = "중복된 테마 이름", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "이미 존재하는 테마 이름입니다."
                    }
                    """)))
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)),
            examples = @ExampleObject(name = "입력값 검증 실패",
                    description = "입력값 존재 여부 또는 유효하지 않은 값일 경우 제공되는 오류 메시지입니다.", value = """
                    [
                        {
                            "message": "테마 이름은 비어있을 수 없습니다."
                        },
                        {
                            "message": "설명은 비어있을 수 없습니다."
                        },
                        {
                            "message": "썸네일은 비어있을 수 없습니다."
                        }
                    ]
                    """)))
    ResponseEntity<ThemeResponse> addTheme(CreateThemeRequest request);

    @SecurityDocs.Unauthorized
    @SecurityDocs.Forbidden
    @Operation(summary = "테마 삭제", description = "방탈출 테마를 삭제합니다. 관리자만 사용 가능합니다.",
            parameters = {
                    @Parameter(name = "id", description = "삭제할 테마 ID", required = true,
                            schema = @Schema(type = "integer", format = "int64"))
            }
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "테마를 찾을 수 없음", content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                    {
                        "message": "존재하지 않는 테마입니다."
                    }
                    """)))
    ResponseEntity<Void> deleteTheme(Long id);
}