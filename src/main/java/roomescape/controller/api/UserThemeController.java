package roomescape.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.ThemeResponse;
import roomescape.service.ThemeService;

@Tag(name = "UserTheme", description = "사용자도 확인 가능한 방탈출 테마 관련 API")
@RestController
@RequestMapping("/themes")
public class UserThemeController {
    private final ThemeService themeService;

    public UserThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "모든 테마 조회", description = "모든 방탈출 테마를 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        List<ThemeResponse> response = themeService.findAll();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "인기 테마 조회", description = "최근 일주일 간 인기 있었던 테마 10개를 조회할 수 있다.")
    @GetMapping("/trending")
    public ResponseEntity<List<ThemeResponse>> findPopular() {
        List<ThemeResponse> response = themeService.findPopular();
        return ResponseEntity.ok().body(response);
    }
}
