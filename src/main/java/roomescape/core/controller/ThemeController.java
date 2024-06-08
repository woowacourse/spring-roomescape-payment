package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.core.dto.theme.ThemeResponse;
import roomescape.core.service.ThemeService;

@Tag(name = "테마 관리 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "모든 테마 조회")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(summary = "최근 1주간 TOP10 인기 테마 조회")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findPopular() {
        return ResponseEntity.ok(themeService.findPopularTheme());
    }
}
