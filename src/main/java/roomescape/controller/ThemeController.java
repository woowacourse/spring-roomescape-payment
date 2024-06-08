package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.ThemeRequest;
import roomescape.dto.ThemeResponse;
import roomescape.service.ThemeService;

import java.net.URI;
import java.util.List;

@Tag(name = "Theme", description = "테마 API")
@RestController
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @GetMapping("/themes/ranking")
    public List<ThemeResponse> findAndOrderByPopularity(@RequestParam int count) {
        return themeService.findAndOrderByPopularity(count);
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> save(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse savedThemeResponse = themeService.save(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + savedThemeResponse.id()))
                .body(savedThemeResponse);
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
