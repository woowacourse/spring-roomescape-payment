package roomescape.theme.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.theme.dto.ThemeAddRequest;
import roomescape.theme.dto.ThemeResponse;

@Tag(name = "테마", description = "테마 API")
public interface ThemeControllerDocs {

    @Operation(summary = "테마 목록 조회", description = "테마 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "테마 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class))))
    public ResponseEntity<List<ThemeResponse>> getThemeList();

    @Operation(summary = "테마 추가", description = "테마를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "테마 추가 성공",
            content = @Content(schema = @Schema(implementation = ThemeResponse.class)))
    @Parameters({
            @Parameter(name = "name", description = "최대 길이 40 글자", required = true),
            @Parameter(name = "description", description = "최대 길이 80 글자", required = true),
            @Parameter(name = "thumbnail", description = "썸네일 경로", required = true)
    })
    public ResponseEntity<ThemeResponse> addTheme(ThemeAddRequest themeAddRequest);

    @Operation(summary = "인기 테마 조회", description = "인기 테마를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class))))
    public ResponseEntity<List<ThemeResponse>> getPopularTheme();

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "테마 삭제 성공")
    public ResponseEntity<Void> deleteTheme(Long id);
}
