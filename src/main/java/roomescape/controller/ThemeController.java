package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
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

@RestController
@Tag(name = "테마", description = "테마 API")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping("/themes")
    @Operation(summary = "테마 목록 조회", description = "테마목록을 조회할 때 사용하는 API")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @GetMapping("/themes/ranking")
    @Operation(summary = "인기 테마 목록 조회", description = "기간에 따른 인기 테마 목록을 조회할 때 사용하는 API")
    public List<ThemeResponse> findAndOrderByPopularity(@RequestParam LocalDate start,
                                                        @RequestParam LocalDate end,
                                                        @RequestParam int count) {
        return themeService.findAndOrderByPopularity(start, end, count);
    }

    @PostMapping("/admin/themes")
    @Operation(summary = "관리자 테마 생성", description = "관리자가 테마를 생성할 때 사용하는 API")
    public ResponseEntity<ThemeResponse> save(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse saved = themeService.save(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + saved.id()))
                .body(saved);
    }

    @DeleteMapping("/admin/themes/{id}")
    @Operation(summary = "관리자 테마 삭제", description = "관리자가 테마를 삭기할 때 사용하는 API")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
