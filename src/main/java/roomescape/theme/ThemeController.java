package roomescape.theme;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;

@RestController
@RequestMapping("/themes")
@AllArgsConstructor
@Tag(name = "테마 관련 API", description = "테마 생성, 조회 및 삭제 등을 위한 API입니다.")
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 생성", description = "새로운 테마를 생성합니다.")
    @PostMapping
    public ResponseEntity<ThemeResponse> create(
            @RequestBody @Valid final ThemeRequest request
    ) {
        final ThemeResponse response = themeService.create(request);
        return ResponseEntity
                .created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(summary = "전체 테마 조회", description = "전체 테마 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> readAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(summary = "테마 순위 조회", description = "테마의 순위를 조회합니다.")
    @GetMapping("/ranking")
    public ResponseEntity<List<ThemeResponse>> readTopRankThemes(
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        final List<ThemeResponse> topRankThemes = themeService.findTopRank(size);
        return ResponseEntity.ok(topRankThemes);
    }

    @Operation(summary = "특정 테마 삭제", description = "ID로 특정 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
