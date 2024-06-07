package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ThemeSaveRequest;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.dto.response.ThemeResponses;

import java.net.URI;

@RestController
public class AdminThemeController {

    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "어드민 테마 추가 API", description = "어드민이 테마를 추가한다.")
    @PostMapping("/admin/themes")
    public ResponseEntity<ThemeResponse> saveTheme(@RequestBody @Valid ThemeSaveRequest themeSaveRequest) {
        ThemeResponse themeResponse = themeService.saveTheme(themeSaveRequest);
        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id()))
                .body(themeResponse);
    }

    @Operation(summary = "어드민 테마 조회 API", description = "어드민이 테마를 조회한다.")
    @GetMapping("/admin/themes")
    public ResponseEntity<ThemeResponses> getThemes() {
        ThemeResponses themeResponses = themeService.getThemes();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "어드민 테마 삭제 API", description = "어드민이 테마를 삭제한다.")
    @DeleteMapping("/admin/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
