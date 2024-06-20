package roomescape.controller.theme;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.auth.RoleAllowed;
import roomescape.domain.member.MemberRole;
import roomescape.service.theme.ThemeService;
import roomescape.service.theme.dto.ThemeListResponse;
import roomescape.service.theme.dto.ThemeRequest;
import roomescape.service.theme.dto.ThemeResponse;

import java.net.URI;

@Tag(name = "Theme")
@RestController
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    @Operation(summary = "전체 테마 조회", description = "전체 테마 정보를 조회한다.")
    public ResponseEntity<ThemeListResponse> findAllTheme() {
        ThemeListResponse response = themeService.findAllTheme();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/themes/popular")
    @Operation(summary = "인기 테마 조회", description = "TOP10 인기 테마를 조회한다.")
    public ResponseEntity<ThemeListResponse> findAllPopularTheme() {
        ThemeListResponse response = themeService.findAllPopularTheme();
        return ResponseEntity.ok().body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @PostMapping("/themes")
    @Operation(summary = "[관리자] 테마 추가", description = "테마를 추가한다.")
    public ResponseEntity<ThemeResponse> saveTheme(@RequestBody @Valid ThemeRequest request) {
        ThemeResponse response = themeService.saveTheme(request);
        return ResponseEntity.created(URI.create("/themes/" + response.getId())).body(response);
    }

    @RoleAllowed(MemberRole.ADMIN)
    @DeleteMapping("/themes/{themeId}")
    @Operation(summary = "[관리자] 테마 삭제", description = "테마를 삭제한다.")
    public ResponseEntity<Void> deleteTheme(
            @PathVariable @NotNull(message = "themeId 값이 null일 수 없습니다.") Long themeId) {
        themeService.deleteTheme(themeId);
        return ResponseEntity.noContent().build();
    }
}
