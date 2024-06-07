package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.PopularThemeRequest;
import roomescape.service.dto.response.ThemeResponses;

@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 조회 API", description = "테마를 조회한다.")
    @GetMapping("/themes")
    public ResponseEntity<ThemeResponses> getThemes() {
        ThemeResponses themeResponses = themeService.getThemes();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "인기테마 조회 API", description = "인기 테마를 조회한다.")
    @GetMapping("/themes/popular")
    public ResponseEntity<ThemeResponses> getPopularThemes(
            @ModelAttribute @Valid PopularThemeRequest popularThemeRequest
    ) {
        ThemeResponses popularThemes = themeService.getPopularThemes(popularThemeRequest);
        return ResponseEntity.ok(popularThemes);
    }
}
