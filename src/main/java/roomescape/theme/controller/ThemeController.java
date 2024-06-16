package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
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
import roomescape.theme.dto.ThemeRankResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "테마 컨트롤러", description = "사용자 요청에 따른 테마 생성, 테마 불러오기 등 컨트롤러 수행")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeRequest themeRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(themeService.addTheme(themeRequest));
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> themeList() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(themeService.findThemes());
    }

    @GetMapping("/rank")
    public ResponseEntity<List<ThemeRankResponse>> themeRankList(@RequestParam LocalDate date) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(themeService.findRankedThemes(date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") long themeId) {
        themeService.removeTheme(themeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
