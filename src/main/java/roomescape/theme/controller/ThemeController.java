package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "테마 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "전체 테마 조회", description = "전체 테마를 조회한다.")
    @GetMapping
    public List<ThemeResponse> findThemes() {
        return themeService.findThemes();
    }

    @Operation(summary = "인기 테마 조회", description = "예약이 많은 인기 테마를 조회한다.")
    @GetMapping("/popular")
    public List<ThemeResponse> findPopularThemes() {
        return themeService.findPopularThemes();
    }

    @Operation(summary = "테마 추가", description = "테마를 추가한다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeCreateRequest request) {
        ThemeResponse response = themeService.createTheme(request);

        URI location = URI.create("/themes/" + response.id());
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제한다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable Long id) {
        themeService.deleteTheme(id);
    }
}

