package roomescape.controller.api;

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
import roomescape.dto.theme.request.ThemeRequest;
import roomescape.dto.theme.response.PopularThemeResponse;
import roomescape.dto.theme.response.ThemeResponse;
import roomescape.service.ThemeService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
public class ThemeController {

    public static final String GET_ADMIN_THEME = "/admin/theme";

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getThemes() {
        log.info("테마 목록 조회 요청 수신");
        List<ThemeResponse> responses = themeService.getThemes();

        log.info("테마 목록 조회 완료: 총 {}건", responses.size());
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/popular-themes")
    public ResponseEntity<List<PopularThemeResponse>> getPopularThemes() {
        log.info("인기 테마 조회 요청 수신");
        List<PopularThemeResponse> responses = themeService.getPopularThemes();

        log.info("인기 테마 조회 완료: 총 {}건", responses.size());
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody final ThemeRequest request) {
        log.info("테마 생성 요청 수신: name={}, description={}", request.name(), request.description());
        ThemeResponse response = themeService.createTheme(request);

        log.info("테마 생성 완료: id={}, name={}", response.id(), response.name());
        return ResponseEntity.created(URI.create(GET_ADMIN_THEME)).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final Long id) {
        log.info("테마 삭제 요청 수신: id={}", id);
        themeService.deleteThemeById(id);

        log.info("테마 삭제 완료: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
