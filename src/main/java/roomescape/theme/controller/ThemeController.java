package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "테마", description = "테마 API")
@Slf4j
@RequestMapping("/themes")
@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성", description = "새로운 테마를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "테마 생성 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody final ThemeRequest request) {
        log.info("테마 생성 요청: name={}, description={}", request.name(), request.description());
        ThemeResponse response = themeService.create(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(summary = "전체 테마 조회", description = "등록된 모든 테마를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전체 테마 조회 성공",
            content = @Content(
                    mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> readAllThemes() {
        List<ThemeResponse> responses = themeService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "인기 테마 조회", description = "예약 수 기준으로 인기 테마를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공",
            content = @Content(
                    mediaType = "application/json"))
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> readPopularThemes() {
        List<ThemeResponse> responses = themeService.getPopularThemes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "테마 삭제", description = "테마 ID를 기준으로 테마를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "삭제할 수 없는 테마 (예약 존재)",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 테마 ID",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{themeId}")
    public ResponseEntity<Void> delete(@Parameter(description = "삭제할 테마 ID", example = "1")
                                       @PathVariable("themeId") final Long themeId) {
        log.info("테마 삭제 요청: themeId={}", themeId);
        themeService.delete(themeId);
        return ResponseEntity.noContent().build();
    }
}
