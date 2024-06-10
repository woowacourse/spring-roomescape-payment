package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.request.CreateThemeRequest;
import roomescape.theme.dto.response.CreateThemeResponse;
import roomescape.theme.dto.response.FindPopularThemesResponse;
import roomescape.theme.dto.response.FindThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "테마 API", description = "테마 관련 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성 API")
    @PostMapping
    public ResponseEntity<CreateThemeResponse> createTheme(@Valid @RequestBody CreateThemeRequest createThemeRequest) {
        CreateThemeResponse createThemeResponse = themeService.createTheme(createThemeRequest);
        return ResponseEntity.created(URI.create("/themes/" + createThemeResponse.id())).body(createThemeResponse);
    }

    @Operation(summary = "테마 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<FindThemeResponse>> getThemes() {
         return ResponseEntity.ok(themeService.getThemes());
    }

    @Operation(summary = "인기 테마 조회 API")
    @GetMapping("/popular")
    public ResponseEntity<List<FindPopularThemesResponse>> getPopularThemes(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(themeService.getPopularThemes(pageable));
    }

    @Operation(summary = "테마 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
