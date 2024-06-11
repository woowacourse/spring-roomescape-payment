package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.request.ThemeRequest;
import roomescape.controller.response.ReservationThemeResponse;
import roomescape.model.Theme;
import roomescape.service.ThemeReadService;
import roomescape.service.ThemeWriteService;

import java.net.URI;
import java.util.List;

@Tag(name = "reservation-theme", description = "방탈출 테마 API")
@RestController
public class ThemeController {

    private final ThemeReadService themeReadService;
    private final ThemeWriteService themeWriteService;

    public ThemeController(ThemeReadService themeReadService,
                           ThemeWriteService themeWriteService) {
        this.themeReadService = themeReadService;
        this.themeWriteService = themeWriteService;
    }

    @Operation(summary = "방탈출 테마 조회", description = "모든 방탈출 테마를 조회합니다.")
    @GetMapping("/themes")
    public ResponseEntity<List<Theme>> getThemes() {
        return ResponseEntity.ok(themeReadService.findAllThemes());
    }

    @Operation(summary = "방탈출 테마 등록", description = "방탈출 테마를 등록합니다.")
    @PostMapping("/themes")
    public ResponseEntity<ReservationThemeResponse> addTheme(@RequestBody ThemeRequest themeRequest) {
        Theme theme = themeReadService.addTheme(themeRequest);
        ReservationThemeResponse response = ReservationThemeResponse.of(theme);
        return ResponseEntity.created(URI.create("/themes/" + theme.getId())).body(response);
    }

    @Operation(summary = "방탈출 테마 삭제", description = "방탈출 테마를 삭제합니다.")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable(name = "id") long id) {
        themeWriteService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인기 테마 조회", description = "한 주 동안 많이 예약된 상위 10개의 방탈출 테마를 조회합니다.")
    @GetMapping("/themes/top10")
    public ResponseEntity<List<ReservationThemeResponse>> getPopularThemes() {
        List<Theme> popularThemes = themeReadService.findPopularThemes();
        List<ReservationThemeResponse> responses = popularThemes.stream()
                .map(ReservationThemeResponse::of)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
