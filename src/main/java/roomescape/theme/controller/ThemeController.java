package roomescape.theme.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.dto.response.ThemeCreateResponse;
import roomescape.theme.dto.response.ThemeReadResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "Theme", description = "테마 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 생성", description = "ADMIN 권한으로 새로운 테마를 생성합니다.")
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ThemeCreateResponse> createTheme(
            @RequestBody @Valid ThemeCreateRequest request
    ) {
        ThemeCreateResponse response = themeService.createTheme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 테마 조회", description = "모든 테마 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeReadResponse>> getAllThemes() {
        List<ThemeReadResponse> responses = themeService.getAllThemes();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "인기 테마 조회", description = "인기 테마 목록을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeReadResponse>> getPopularThemes(
            @RequestParam("limit") int limit
    ) {
        List<ThemeReadResponse> response = themeService.getPopularThemes(limit);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "테마 삭제", description = "ADMIN 권한으로 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteTheme(
            @PathVariable("id") Long id
    ) {
        themeService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
