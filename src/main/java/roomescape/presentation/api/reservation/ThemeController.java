package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.query.ThemeQueryService;
import roomescape.application.reservation.query.dto.ThemeResult;
import roomescape.presentation.api.reservation.response.ThemeResponse;

import java.util.List;

@RestController
@Tag(name = "테마 API")
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeQueryService themeQueryService;

    public ThemeController(final ThemeQueryService themeQueryService) {
        this.themeQueryService = themeQueryService;
    }

    @Operation(
            summary = "테마 조회",
            description = "모든 테마를 조회합니다. 각 테마의 상세 정보가 포함됩니다."
    )
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        final List<ThemeResult> themeResults = themeQueryService.findAll();
        final List<ThemeResponse> themeResponses = themeResults.stream()
                .map(ThemeResponse::from)
                .toList();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(
            summary = "주간 인기 테마 조회",
            description = "최근 일주일 동안 가장 인기 있는 테마를 조회합니다."
    )
    @GetMapping("/rank")
    public ResponseEntity<List<ThemeResponse>> findRankBetweenDate() {
        final List<ThemeResult> rank = themeQueryService.findWeeklyPopularThemes();
        final List<ThemeResponse> themeResponses = rank.stream()
                .map(ThemeResponse::from)
                .toList();
        return ResponseEntity.ok(themeResponses);
    }
}
