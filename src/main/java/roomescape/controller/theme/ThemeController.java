package roomescape.controller.theme;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import roomescape.domain.theme.Theme;
import roomescape.dto.theme.ThemeResponse;
import roomescape.dto.theme.ThemeSaveRequest;
import roomescape.service.ThemeService;

@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody final ThemeSaveRequest request) {
        final Theme theme = request.toModel();
        return ResponseEntity.status(HttpStatus.CREATED).body(themeService.create(theme));
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(summary = "인기 테마 조회")
    @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findAllPopular() {
        return ResponseEntity.ok(themeService.findPopularThemes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable final Long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
