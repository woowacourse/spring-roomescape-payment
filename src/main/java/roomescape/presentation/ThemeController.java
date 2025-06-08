package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import roomescape.dto.request.ThemeRequest;
import roomescape.dto.response.ThemeResponse;
import roomescape.service.ThemeService;

@Tag(name = "테마 API", description = "테마 API 입니다.")
@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성", description = "새 테마를 생성합니다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> createNewTheme(@Valid @RequestBody ThemeRequest request) {
        ThemeResponse themeResponse = themeService.createTheme(request);
        return ResponseEntity.created(URI.create("/api/themes/" + themeResponse.id())).body(themeResponse);
    }

    @Operation(summary = "모든 테마 조회", description = "모든 테마를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        return ResponseEntity.ok(themeService.findAllThemes());
    }

    @Operation(summary = "테마 삭제", description = "특정 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteThemeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인기 테마 조회", description = "예약 횟수 상위 N개의 테마를 조회합니다.")
    @GetMapping("/rank")
    public ResponseEntity<List<ThemeResponse>> getThemeRank() {
        return ResponseEntity.ok(themeService.getTopThemes());
    }
}
