package roomescape.web.controller.api;

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
import roomescape.service.ThemeService;
import roomescape.service.request.ThemeSaveAppRequest;
import roomescape.service.response.ThemeAppResponse;
import roomescape.web.controller.request.ThemeRequest;
import roomescape.web.controller.response.ThemeResponse;

@Tag(name = "Theme", description = "테마 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 추가", description = "테마를 추가합니다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody ThemeRequest request) {
        ThemeAppResponse appResponse = themeService.save(ThemeSaveAppRequest.from(request));

        Long id = appResponse.id();
        ThemeResponse webResponse = ThemeResponse.from(appResponse);

        return ResponseEntity.created(URI.create("/themes/" + id)).body(webResponse);
    }

    @Operation(summary = "전체 테마 조회", description = "전체 테마를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> response = themeService.findAll().stream()
                .map(ThemeResponse::from).toList();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "인기 테마 조회", description = "전체 테마 중 인기있는 테마를 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> findPopular() {
        List<ThemeResponse> response = themeService.findPopular().stream()
                .map(ThemeResponse::from)
                .toList();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "테마 삭제", description = "테마id로 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
