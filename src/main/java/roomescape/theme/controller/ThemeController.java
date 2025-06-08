package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.service.ThemeService;
import roomescape.theme.service.dto.request.ThemeRequest;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.net.URI;
import java.util.List;

@RequestMapping("/themes")
@RestController
@Tag(name = "테마 컨트롤러", description = "테마 관련 API 모음")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    @Operation(summary = "전체 테마 목록 조회", description = "전체 테마 목록을 조회합니다.")
    public ResponseEntity<List<ThemeResponse>> readAllThemes() {
        List<ThemeResponse> responses = themeService.getAll();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 테마 목록 조회", description = "1주일간 가장 에약이 많은 테마 10개를 조회합니다. ")
    public ResponseEntity<List<ThemeResponse>> readPopularThemes() {
        List<ThemeResponse> responses = themeService.getPopularThemes();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "테마 생성", description = "이름,설명,썸네일을 포함한 테마를 생성합니다.")
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody final ThemeRequest request) {
        ThemeResponse response = themeService.create(request);

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "테마 삭제", description = "선택한 테마를 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
