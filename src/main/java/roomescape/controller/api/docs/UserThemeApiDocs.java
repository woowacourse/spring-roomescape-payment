package roomescape.controller.api.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.ThemeResponse;

@Tag(name = "UserTheme", description = "사용자도 확인 가능한 방탈출 테마 관련 API")
public interface UserThemeApiDocs {
    @Operation(summary = "모든 테마 조회", description = "모든 방탈출 테마를 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ThemeResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<ThemeResponse>> findAll();

    @Operation(summary = "인기 테마 조회", description = "최근 일주일 간 인기 있었던 테마 10개를 조회할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ThemeResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<List<ThemeResponse>> findPopular();
}
