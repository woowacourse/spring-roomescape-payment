package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;

@Tag(name = "테마 관리", description = "테마 관리 API")
interface AdminThemeControllerDocs {

    @Operation(summary = "테마 등록", description = "관리자가 테마를 등록한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "테마 등록 성공"
            )
    })
    ResponseEntity<ThemeResponse> saveTheme(
            @Valid ThemeRequest request
    );

    @Operation(summary = "테마 삭제", description = "관리자가 테마를 삭제한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "테마 삭제 성공"
            )
    })
    ResponseEntity<Void> deleteTheme(
            @Parameter(description = "테마 ID", example = "1") Long themeId
    );
}
