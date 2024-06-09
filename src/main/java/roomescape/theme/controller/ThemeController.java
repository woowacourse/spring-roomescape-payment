package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.net.URI;
import java.util.List;

@Tag(name = "테마 컨트롤러")
@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성")
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@Valid @RequestBody ThemeCreateRequest themeCreateRequest) {
        ThemeResponse theme = themeService.createTheme(themeCreateRequest);
        return ResponseEntity.created(URI.create("/themes/" + theme.id()))
                .body(theme);
    }

    @Operation(summary = "테마 목록 조회")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> readThemes() {
        List<ThemeResponse> themes = themeService.readThemes();
        return ResponseEntity.ok(themes);
    }

    @Operation(summary = "테마 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponse> readTheme(@Parameter(description = "Theme id") @PathVariable Long id) {
        ThemeResponse themeResponse = themeService.readTheme(id);
        return ResponseEntity.ok(themeResponse);
    }

    @Operation(summary = "인기 테마 조회")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> readPopularThemes() {
        List<ThemeResponse> themeResponses = themeService.readPopularThemes();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "테마 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@Parameter(description = "Theme id") @PathVariable Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
