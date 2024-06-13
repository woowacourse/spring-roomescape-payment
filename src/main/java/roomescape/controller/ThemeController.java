package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.theme.ThemePriceResponse;
import roomescape.service.ThemeService;
import roomescape.dto.response.theme.ThemeResponse;

@Tag(name = "사용자 테마 API", description = "사용자 테마 관련 API 입니다.")
@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "사용자 테마 조회 API")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> responses = themeService.findAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 인기 테마 조회 API")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findPopularThemes() {
        List<ThemeResponse> responses = themeService.findPopularThemes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "사용자 테마 가격 조회 API")
    @GetMapping("/{id}/price")
    public ResponseEntity<ThemePriceResponse> findThemePrice(@PathVariable int id) {
        return ResponseEntity.ok(themeService.findThemePriceById(id));
    }
}
