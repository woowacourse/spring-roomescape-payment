package roomescape.controller;

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
import roomescape.domain.theme.ThemeService;
import roomescape.domain.theme.dto.ThemeRequest;
import roomescape.domain.theme.dto.ThemeResponse;

@Tag(
        name = "테마 컨트롤러"
)
@RestController
@RequestMapping("/themes")
@AllArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @Operation(
            description = "테마를 생성한다."
    )
    @PostMapping
    public ResponseEntity<ThemeResponse> create(
            @RequestBody @Valid final ThemeRequest request
    ) {
        final ThemeResponse response = themeService.create(request);
        return ResponseEntity
                .created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @Operation(
            description = "테마를 모두 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> readAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(
            description = "이전 일주일간의 테마를 생성된 예약이 많은 순서대로 10개 조회한다."
    )
    @GetMapping("/ranking")
    public ResponseEntity<List<ThemeResponse>> readTopRankThemes(
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        final List<ThemeResponse> topRankThemes = themeService.findTopRank(size);
        return ResponseEntity.ok(topRankThemes);
    }

    @Operation(
            description = "테마를 삭제한다. 사용중이라면 삭제가 불가능하다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") final Long id
    ) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
