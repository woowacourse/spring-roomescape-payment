package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.annotation.CheckRole;
import roomescape.dto.request.CreateThemeRequest;
import roomescape.dto.response.ThemeResponse;
import roomescape.entity.Theme;
import roomescape.global.Role;
import roomescape.service.ThemeService;

@Tag(name = "테마", description = "테마 관리 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 목록 조회", description = "모든 테마 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "테마 목록 조회 성공")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        List<Theme> themes = themeService.findAll();
        List<ThemeResponse> responses = themes.stream()
                .map(ThemeResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "인기 테마 조회", description = "현재 날짜 기준으로 인기 있는 테마 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 테마 목록 조회 성공")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> popularThemes() {
        List<Theme> rankingThemes = themeService.getRankingThemes(LocalDate.now());
        List<ThemeResponse> responses = rankingThemes.stream()
                .map(ThemeResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "테마 추가", description = "관리자 권한으로 새로운 테마를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "테마 추가 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @PostMapping
    @CheckRole(value = Role.ADMIN)
    public ResponseEntity<ThemeResponse> addTheme(
            @Parameter(description = "테마 생성 요청") @RequestBody @Valid CreateThemeRequest request) {
        Theme theme = themeService.addTheme(request);
        ThemeResponse response = ThemeResponse.from(theme);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "테마 삭제", description = "관리자 권한으로 테마를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "테마 삭제 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @ApiResponse(responseCode = "500", description = "내부 서버 에러")
    @DeleteMapping("/{id}")
    @CheckRole(value = Role.ADMIN)
    public ResponseEntity<Void> deleteTheme(
            @Parameter(description = "테마 ID") @PathVariable Long id) {
        themeService.deleteThemeById(id);

        return ResponseEntity.noContent().build();
    }
}
