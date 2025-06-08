package roomescape.theme.presentation;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.adaptor.ThemeApiAdaptor;
import roomescape.theme.docs.*;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.PopularThemeResponse;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
@RequestMapping(ThemeController.THEME_BASE_URL)
public class ThemeController {

    public static final String THEME_BASE_URL = "/themes";
    private static final String SLASH = "/";

    private final ThemeService themeService;
    private final ThemeApiAdaptor themeApiAdaptor;

    public ThemeController(ThemeService themeService, ThemeApiAdaptor themeApiAdaptor) {
        this.themeService = themeService;
        this.themeApiAdaptor = themeApiAdaptor;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponseDocs>> getThemes() {
        List<ThemeResponse> responses = themeService.getThemes();
        List<ThemeResponseDocs> responseDocs = responses.stream()
                .map(themeApiAdaptor::toThemeResponseDocs)
                .toList();
        return ResponseEntity.ok().body(responseDocs);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularThemeResponseDocs>> getPopularThemes() {
        List<PopularThemeResponse> responses = themeService.getPopularThemes();
        List<PopularThemeResponseDocs> responseDocs = responses.stream()
                .map(themeApiAdaptor::toPopularThemeResponseDocs)
                .toList();
        return ResponseEntity.ok().body(responseDocs);
    }

    @PostMapping
    public ResponseEntity<ThemeResponseDocs> createTheme(@RequestBody final ThemeRequestDocs requestDocs) {
        ThemeRequest request = themeApiAdaptor.toThemeRequest(requestDocs);
        ThemeResponse response = themeService.createTheme(request);
        ThemeResponseDocs responseDocs = themeApiAdaptor.toThemeResponseDocs(response);
        return ResponseEntity.created(URI.create(THEME_BASE_URL + SLASH + response.id())).body(responseDocs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }
}
