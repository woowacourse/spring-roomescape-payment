package roomescape.theme.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.controller.dto.CreateThemeRequest;
import roomescape.theme.controller.dto.ThemeResponse;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "테마 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        return ResponseEntity.ok(themeService.findAllThemes());
    }

    @Operation(summary = "테마 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "테마 생성 성공"
            )
    })
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody @Valid final CreateThemeRequest request) {
        ThemeResponse response = themeService.createTheme(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "테마 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "테마 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "예약이 존재하여 테마를 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable final Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인기 테마 조회 API", description = "주간 인기 테마 상위 10개 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 테마 조회 성공"
            )
    })
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> popularThemes() {
        List<ThemeResponse> popularThemes = themeService.getWeeklyPopularThemes();
        return ResponseEntity.ok(popularThemes);
    }
}
