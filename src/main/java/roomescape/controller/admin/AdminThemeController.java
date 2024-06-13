package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ThemeSaveRequest;
import roomescape.service.dto.response.ThemeResponse;
import roomescape.service.dto.response.ThemeResponses;

import java.net.URI;

@Tag(name = "[ADMIN] 테마 API", description = "어드민 권한으로 테마를 생성/조회/삭제할 수 있습니다.")
@RestController
public class AdminThemeController {

    private final ThemeService themeService;

    public AdminThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "어드민 테마 추가 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "생성된 테마 정보를 반환합니다.")
    })
    @PostMapping("/admin/themes")
    public ResponseEntity<ThemeResponse> saveTheme(@RequestBody @Valid ThemeSaveRequest themeSaveRequest) {
        ThemeResponse themeResponse = themeService.saveTheme(themeSaveRequest);
        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id()))
                .body(themeResponse);
    }

    @Operation(summary = "어드민 테마 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "테마 정보를 반환합니다.")
    })
    @GetMapping("/admin/themes")
    public ResponseEntity<ThemeResponses> getThemes() {
        ThemeResponses themeResponses = themeService.getThemes();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "어드민 테마 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "테마 삭제에 성공했습니다.")
    })
    @DeleteMapping("/admin/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
