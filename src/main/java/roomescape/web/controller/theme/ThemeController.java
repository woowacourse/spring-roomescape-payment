package roomescape.web.controller.theme;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.theme.ThemeRequest;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.theme.ThemeDeleteService;
import roomescape.service.theme.ThemeRegisterService;
import roomescape.service.theme.ThemeSearchService;

@Tag(name = "테마 관리")
@RestController
@RequestMapping("/themes")
class ThemeController {

    private final ThemeRegisterService registerService;
    private final ThemeSearchService searchService;
    private final ThemeDeleteService deleteService;

    public ThemeController(ThemeRegisterService registerService,
                           ThemeSearchService searchService,
                           ThemeDeleteService deleteService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.deleteService = deleteService;
    }

    @Operation(summary = "테마 등록", description = "요청한 테마 내용으로 방탈출 테마를 등록한다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody ThemeRequest request) {
        ThemeResponse response = registerService.registerTheme(request);
        return ResponseEntity.created(URI.create("/themes/" + response.id())).body(response);
    }

    @Operation(summary = "테마 조회", description = "등록된 테마 중 특정 테마를 조회한다.")
    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponse> getTheme(@PathVariable Long id) {
        ThemeResponse response = searchService.findTheme(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 테마 조회", description = "등록된 모든 테마를 조회한다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        List<ThemeResponse> responses = searchService.findAllThemes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "인기 테마 조회", description = "최근 일주일을 기준으로 하여 해당 기간 내에 방문하는 예약이 많은 테마 10개를 조회한다.")
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularTheme() {
        List<ThemeResponse> responses = searchService.findPopularThemes();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "테마 삭제", description = "등록된 테마를 삭제한다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        deleteService.deleteTheme(id);
        return ResponseEntity.noContent().build();
    }
}
