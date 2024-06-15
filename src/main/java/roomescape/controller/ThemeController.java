package roomescape.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.theme.ThemePriceResponse;
import roomescape.service.ThemeService;
import roomescape.dto.response.theme.ThemeResponse;

@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAllThemes() {
        List<ThemeResponse> responses = themeService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findPopularThemes() {
        List<ThemeResponse> responses = themeService.findPopularThemes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/price")
    public ResponseEntity<ThemePriceResponse> findThemePrice(@PathVariable int id) {
        return ResponseEntity.ok(themeService.findThemePriceById(id));
    }
}
