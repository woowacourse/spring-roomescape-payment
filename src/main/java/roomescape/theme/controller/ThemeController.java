package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.global.dto.ErrorResponse;
import roomescape.theme.controller.dto.CreateThemeRequest;
import roomescape.theme.controller.dto.ThemeResponse;

@Tag(name = "테마 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 테마 정보를 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 테마 정보 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        return ResponseEntity.ok(themeService.findAllThemes());
    }

    @Operation(summary = "테마 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "테마를 성공적으로 생성한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 테마 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복되거나 조건에 맞지 않아 테마 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 테마 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody @Valid final CreateThemeRequest request) {
        ThemeResponse response = themeService.createTheme(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "테마 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "테마를 성공적으로 삭제한다."),
            @ApiResponse(responseCode = "404", description = "테마를 찾을 수 없어 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 테마 삭제에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable final Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인기 테마 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 테마 목록을 반환한다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 인기 테마 조회에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> popularThemes() {
        List<ThemeResponse> popularThemes = themeService.getWeeklyPopularThemes();
        return ResponseEntity.ok(popularThemes);
    }
}
