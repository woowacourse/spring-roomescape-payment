package roomescape.controller.theme;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import roomescape.controller.reservation.dto.PopularThemeResponse;
import roomescape.controller.theme.dto.CreateThemeRequest;
import roomescape.controller.theme.dto.PopularThemeRequest;
import roomescape.controller.theme.dto.ThemeResponse;
import roomescape.service.ThemeService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private static final Logger log = LoggerFactory.getLogger(ThemeController.class);
    private static final Map<PopularThemeRequest, List<PopularThemeResponse>> popularThemeCache = new HashMap<>();

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public List<ThemeResponse> getThemes() {
        return themeService.getThemes();
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> addTheme(
            @RequestBody @Valid final CreateThemeRequest createThemeRequest) {
        final ThemeResponse theme = themeService.addTheme(createThemeRequest);
        final URI uri = UriComponentsBuilder.fromPath("/themes/{id}")
                .buildAndExpand(theme.id())
                .toUri();
        log.info("테마 생성 theme={}", theme);
        return ResponseEntity.created(uri)
                .body(theme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final long id) {
        themeService.deleteTheme(id);
        log.info("테마 삭제 id={}", id);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping(value = "/popular", params = {"from", "until", "limit"})
    public List<PopularThemeResponse> getPopularThemes(@Valid final PopularThemeRequest popularThemeRequest) {
        return popularThemeCache.computeIfAbsent(popularThemeRequest, request ->
                themeService.getPopularThemes(request.from(), request.until(), request.limit())
        );
    }
}
