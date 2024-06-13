package roomescape.theme.presentation;

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
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeAddRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
@Tag(name = "Theme API", description = "테마 관련 API")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "전체 테마 조회 API", description = "전체 테마를 조회합니다.")
    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> getThemeList() {
        return ResponseEntity.ok(themeService.findAllTheme());
    }

    @Operation(summary = "테마 생성 API", description = "테마를 생성합니다.")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> addTheme(@Valid @RequestBody ThemeAddRequest themeAddRequest) {
        ThemeResponse saveResponse = themeService.saveTheme(themeAddRequest);
        URI createdUri = URI.create("/themes/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @Operation(summary = "인기 테마 조회 API", description = "지난 7일간 가장 많이 예약된 테마 10개를 조회합니다.")
    @GetMapping("/themes/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularTheme() {
        return ResponseEntity.ok(themeService.findPopularTheme());
    }

    @Operation(summary = "테마 삭제 API", description = "테마를 삭제합니다.")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") Long id) {
        themeService.removeTheme(id);
        return ResponseEntity.noContent().build();
    }
}
