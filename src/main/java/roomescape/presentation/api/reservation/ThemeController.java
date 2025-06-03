package roomescape.presentation.api.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.query.ThemeQueryService;
import roomescape.application.reservation.query.dto.ThemeResult;
import roomescape.presentation.api.reservation.response.ThemeResponse;

import java.util.List;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeQueryService themeQueryService;

    public ThemeController(final ThemeQueryService themeQueryService) {
        this.themeQueryService = themeQueryService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        final List<ThemeResult> themeResults = themeQueryService.findAll();
        final List<ThemeResponse> themeResponses = themeResults.stream()
                .map(ThemeResponse::from)
                .toList();
        return ResponseEntity.ok(themeResponses);
    }

    @GetMapping("/rank")
    public ResponseEntity<List<ThemeResponse>> findRankBetweenDate() {
        final List<ThemeResult> rank = themeQueryService.findWeeklyPopularThemes();
        final List<ThemeResponse> themeResponses = rank.stream()
                .map(ThemeResponse::from)
                .toList();
        return ResponseEntity.ok(themeResponses);
    }
}
