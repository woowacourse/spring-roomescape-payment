package roomescape.theme.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.PopularThemeResponse;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ThemeController.THEME_BASE_URL)
public class ThemeController implements ThemeControllerDocs {

    public static final String THEME_BASE_URL = "/themes";
    private static final String SLASH = "/";

    private final ThemeService themeService;

    @Override
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        List<ThemeResponse> responses = themeService.getThemes();
        return ResponseEntity.ok().body(responses);
    }

    @Override
    @GetMapping("/popular")
    public ResponseEntity<List<PopularThemeResponse>> getPopularThemes() {
        List<PopularThemeResponse> responses = themeService.getPopularThemes();
        return ResponseEntity.ok().body(responses);
    }

    @Override
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody final ThemeRequest request) {
        ThemeResponse response = themeService.createTheme(request);
        return ResponseEntity.created(URI.create(THEME_BASE_URL + SLASH + response.id())).body(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }
}
