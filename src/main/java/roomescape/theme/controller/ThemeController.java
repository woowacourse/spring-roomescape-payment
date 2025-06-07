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
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.theme.service.ThemeService;
import roomescape.theme.service.dto.request.ThemeRequest;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.net.URI;
import java.util.List;

@Tag(name = "테마")
@RequestMapping("/themes")
@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "전체 방탈출 테마 조회")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> readAllThemes() {
        List<ThemeResponse> responses = themeService.getAll();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "방탈출 테마 인기순 조회", description = "최근 일주일 예약 내역을 기준으로, 상위 10개 항목을 조회한다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> readPopularThemes() {
        List<ThemeResponse> responses = themeService.getPopularThemes();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "테마 생성", description = "어드민 권한으로 방탈출 테마를 생성한다.")
    @RequiredAdmin
    @PostMapping
    public ResponseEntity<ThemeResponse> create(@Valid @RequestBody final ThemeRequest request) {
        ThemeResponse response = themeService.create(request);

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(summary = "테마 삭제", description = "어드민 권한으로 테마를 삭제한다.")
    @RequiredAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
