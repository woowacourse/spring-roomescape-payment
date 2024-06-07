package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.theme.dto.ThemeRankResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;

import java.time.LocalDate;
import java.util.List;

import static jakarta.servlet.http.HttpServletRequest.BASIC_AUTH;

@SecurityRequirement(name = BASIC_AUTH)
@Tag(name = "Theme", description = "테마 관련 기능을 제공하는 API")
public interface ThemeControllerSwagger {

    @Operation(
            summary = "테마 생성",
            description = "새로운 테마를 생성합니다.",
            requestBody = @RequestBody(
                    description = "테마 생성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ThemeRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "테마가 성공적으로 생성되었습니다.",
                            headers = @Header(name = "Location", description = "성공적으로 생성된 테마의 URI"),
                            content = @Content(
                                    schema = @Schema(implementation = ThemeResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ThemeResponse> createTheme(ThemeRequest themeRequest);

    @Operation(
            summary = "테마 목록 조회",
            description = "등록된 모든 테마의 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "테마 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ThemeResponse.class))
                            )
                    )
            }
    )
    ResponseEntity<List<ThemeResponse>> themeList();

    @Operation(
            summary = "테마 순위 목록 조회",
            description = "지정된 날짜에 따른 테마 순위 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "테마 순위 목록이 반환됩니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ThemeRankResponse.class))
                            )
                    )
            }
    )
    ResponseEntity<List<ThemeRankResponse>> themeRankList(LocalDate date);

    @Operation(
            summary = "테마 삭제",
            description = "지정된 테마 ID를 가진 테마를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "테마가 성공적으로 삭제되었습니다.",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    ResponseEntity<Void> deleteTheme(long themeId);
}
