package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.ThemeRequest;
import roomescape.dto.ThemeResponse;
import roomescape.service.ThemeService;

@RestController
@RequestMapping(value = "/themes")
@Tag(name = "테마 API", description = "테마 관련 API 입니다.")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    @Operation(summary = "테마 목록 조회 API", description = "모든 테마 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "테마 목록 조회 성공")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @GetMapping("/ranking")
    @Operation(summary = "인기 테마 목록 조회 API", description = "인기 테마 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 테마 목록 조회 성공")
    public List<ThemeResponse> findAndOrderByPopularity(
            @RequestParam @Schema(description = "시작 날짜") LocalDate start,
            @RequestParam @Schema(description = "종료 날짜") LocalDate end,
            @RequestParam @Schema(description = "출력 수") int count
    ) {
        return themeService.findAndOrderByPopularity(start, end, count);
    }

    @PostMapping
    @Operation(summary = "테마 등록 API", description = "테마를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "테마 등록 성공")
    public ResponseEntity<ThemeResponse> save(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse saved = themeService.save(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + saved.id()))
                .body(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "테마 삭제 API", description = "테마를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "테마 삭제 성공")
    public ResponseEntity<Void> delete(@PathVariable @Schema(description = "테마 ID") long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
