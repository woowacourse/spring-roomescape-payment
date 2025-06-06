package roomescape.theme.ui;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.uri.UriFactory;
import roomescape.theme.application.ThemeFacade;
import roomescape.theme.ui.dto.CreateThemeWebRequest;
import roomescape.theme.ui.dto.ThemeResponse;

@Slf4j
@RestController
@RequestMapping(ThemeController.BASE_PATH)
@RequiredArgsConstructor
public class ThemeController {

    public static final String BASE_PATH = "/themes";

    private final ThemeFacade themeFacade;

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAll() {
        log.info("[THEME] 테마 전체 조회 요청");
        return ResponseEntity.ok(themeFacade.getAll());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ThemeResponse>> getRanking() {
        log.info("[THEME] 테마 랭킹 조회 요청");
        return ResponseEntity.ok(themeFacade.getRanking());
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> create(@RequestBody final CreateThemeWebRequest createThemeWebRequest) {
        log.info("[THEME] 테마 생성 요청: {}", createThemeWebRequest);
        final ThemeResponse themeResponse = themeFacade.create(createThemeWebRequest);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(themeResponse.id()));
        return ResponseEntity.created(location)
                .body(themeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        log.info("[THEME] 테마 삭제 요청: id={}", id);
        themeFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
