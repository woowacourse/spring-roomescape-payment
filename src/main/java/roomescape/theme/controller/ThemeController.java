package roomescape.theme.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeRankResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ThemeController implements ThemeControllerSwagger {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Override
    @PostMapping("/admin/themes")
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeRequest themeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(themeService.addTheme(themeRequest));
    }

    @Override
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> themeList() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(themeService.findThemes());
    }

    @Override
    @GetMapping("/themes/rank")
    public ResponseEntity<List<ThemeRankResponse>> themeRankList(@RequestParam LocalDate date) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(themeService.findRankedThemes(date));
    }

    @Override
    @DeleteMapping("/admin/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") long themeId) {
        themeService.removeTheme(themeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    @GetMapping("/themes/{id}")
    public ResponseEntity<ThemeResponse> getThemeById(@PathVariable("id") long themeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(themeService.findTheme(themeId));
    }
}
