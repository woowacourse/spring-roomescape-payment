package roomescape.controller;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import roomescape.dto.ThemeRequest;
import roomescape.dto.ThemeResponse;
import roomescape.service.ThemeService;

@Tag(name = "테마 API", description = "테마 API 입니다.")
@RestController
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 조회", description = "전체 테마를 조회합니다.")
    @GetMapping("/themes")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @Operation(summary = "인기 테마 조회", description = "인기 테마를 조회합니다.")
    @GetMapping("/themes/ranking")
    public List<ThemeResponse> findAndOrderByPopularity(@RequestParam int count) {
        return themeService.findAndOrderByPopularity(count);
    }

    @Operation(summary = "테마 추가", description = "테마를 추가합니다.")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> save(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse savedThemeResponse = themeService.save(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + savedThemeResponse.id()))
                .body(savedThemeResponse);
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
