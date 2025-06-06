package roomescape.presentation.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.presentation.dto.request.ThemeCreateRequest;
import roomescape.presentation.dto.response.ThemeResponse;
import roomescape.application.ThemeService;

@Tag(name = "테마 관련 기능 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    @Operation(summary = "전체 테마 조회")
    @ApiResponse(responseCode = "200", description = "정상 응답")
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        return ResponseEntity.ok(themeService.getThemes());
    }

    @Hidden
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody @Valid ThemeCreateRequest request) {
        ThemeResponse response = themeService.createTheme(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 테마 조회")
    @ApiResponse(responseCode = "200", description = "정상 응답")
    public ResponseEntity<List<ThemeResponse>> popularThemes() {
        List<ThemeResponse> popularThemes = themeService.getPopularThemes();
        return ResponseEntity.ok(popularThemes);
    }
}
