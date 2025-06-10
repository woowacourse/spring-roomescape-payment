package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
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
import roomescape.dto.theme.ThemeCreateRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.ThemeService;

@Tag(name = "테마 API", description = "예약 가능한 테마 관리 API입니다.")
@RestController
@RequestMapping("/themes")
public class ThemeController {
    private static final Logger log = LoggerFactory.getLogger(ThemeController.class);
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "전체 테마 조회", description = "등록된 모든 테마를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        List<ThemeResponse> allThemes = themeService.findAllThemes();
        return ResponseEntity.ok(allThemes);
    }

    @Operation(summary = "테마 추가", description = "새로운 테마를 등록합니다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> postTheme(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "테마 생성 요청 정보")
            @RequestBody final ThemeCreateRequest request) {
        ThemeResponse response = themeService.createTheme(request);
        log.info("Theme created: name={}", request.name());
        return ResponseEntity.created(URI.create("themes/" + response.id())).body(response);
    }

    @Operation(summary = "인기 테마 조회", description = "예약률이 높은 인기 테마를 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularThemes() {
        List<ThemeResponse> popularThemes = themeService.findPopularThemes();
        return ResponseEntity.ok(popularThemes);
    }

    @Operation(summary = "테마 삭제", description = "특정 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(
            @Parameter(description = "테마 ID") @PathVariable("id") final Long id) {
        themeService.deleteThemeById(id);
        log.info("Theme deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
