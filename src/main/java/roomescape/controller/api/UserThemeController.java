package roomescape.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.dto.response.ThemeResponse;
import roomescape.service.ThemeService;

@RestController
@RequestMapping("/themes")
public class UserThemeController {
    private final ThemeService themeService;

    public UserThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> response = themeService.findAll();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<ThemeResponse>> findPopular() {
        List<ThemeResponse> response = themeService.findPopular();
        return ResponseEntity.ok().body(response);
    }
}
