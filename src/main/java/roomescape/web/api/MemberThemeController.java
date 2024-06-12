package roomescape.web.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ThemeService;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.policy.WeeklyRankingPolicy;

@RestController
@RequiredArgsConstructor
public class MemberThemeController implements MemberThemeControllerDocs {
    private final ThemeService themeService;

    @GetMapping("/themes")
    public ResponseEntity<List<ThemeResponse>> findAllTheme() {
        List<ThemeResponse> response = themeService.findAllTheme();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/themes/ranking")
    public ResponseEntity<List<ThemeResponse>> findAllPopularTheme() {
        List<ThemeResponse> response = themeService.findAllPopularThemes(new WeeklyRankingPolicy());
        return ResponseEntity.ok().body(response);
    }
}
