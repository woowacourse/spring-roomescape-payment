package roomescape.theme.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeRequest;
import roomescape.theme.application.dto.ThemeResponse;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "전체 테마 조회 API")
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @Operation(summary = "테마 추가 API")
    @PostMapping
    public ResponseEntity<ThemeResponse> add(@Valid @RequestBody final ThemeRequest requestDto) {
        return new ResponseEntity<>(themeService.add(requestDto), HttpStatus.CREATED);
    }

    @Operation(summary = "테마 제거 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "인기 테마 조회 API")
    @GetMapping("/rank")
    public ResponseEntity<List<ThemeResponse>> sortByRank() {
        return ResponseEntity.ok(themeService.sortByRank());
    }

}
