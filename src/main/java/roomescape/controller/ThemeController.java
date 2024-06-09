package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.request.ThemeRequest;
import roomescape.controller.response.ReservationThemeResponse;
import roomescape.model.Theme;
import roomescape.service.ThemeReadService;
import roomescape.service.ThemeWriteService;

import java.net.URI;
import java.util.List;

@RestController
public class ThemeController {

    private final ThemeReadService themeReadService;
    private final ThemeWriteService themeWriteService;

    public ThemeController(ThemeReadService themeReadService,
                           ThemeWriteService themeWriteService) {
        this.themeReadService = themeReadService;
        this.themeWriteService = themeWriteService;
    }

    @GetMapping("/themes")
    public ResponseEntity<List<Theme>> getThemes() {
        return ResponseEntity.ok(themeReadService.findAllThemes());
    }

    @PostMapping("/themes")
    public ResponseEntity<ReservationThemeResponse> addTheme(@RequestBody ThemeRequest themeRequest) {
        Theme theme = themeReadService.addTheme(themeRequest);
        ReservationThemeResponse response = ReservationThemeResponse.of(theme);
        return ResponseEntity.created(URI.create("/themes/" + theme.getId())).body(response);
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable(name = "id") long id) {
        themeWriteService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/themes/top10")
    public ResponseEntity<List<ReservationThemeResponse>> getPopularThemes() {
        List<Theme> popularThemes = themeReadService.findPopularThemes();
        List<ReservationThemeResponse> responses = popularThemes.stream()
                .map(ReservationThemeResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
