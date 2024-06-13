package roomescape.controller.api.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.controller.dto.response.ThemeResponse;

@Tag(name = "AdminTheme", description = "관리자만 접근할 수 있는 방탈출 테마 관련 API")
public interface AdminThemeApiDocs {
    @Operation(summary = "테마 생성", description = "테마를 생성할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = {@Content(schema = @Schema(implementation = ThemeResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<ThemeResponse> save(CreateThemeRequest request);

    @Operation(summary = "테마 삭제", description = "테마를 삭제할 수 있다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "요청 정보 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "접근 권한이 없어서 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 내부 문제로 실패",
                    content = {@Content(schema = @Schema(implementation = ErrorMessageResponse.class))}),
    })
    ResponseEntity<Void> delete(Long id);
}
