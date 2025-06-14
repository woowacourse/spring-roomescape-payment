package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "Theme", description = "테마 API")
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 생성", description = "새로운 테마를 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse saveTheme(
            @Parameter(description = "테마 생성 요청 정보") @Valid @RequestBody final ThemeRequest request
    ) {
        return themeService.saveTheme(request);
    }

    @Operation(summary = "테마 목록 조회", description = "모든 테마 목록을 조회합니다.")
    @GetMapping
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @Operation(summary = "인기 테마 목록 조회", description = "인기 테마 목록을 조회합니다.")
    @GetMapping("/ranking")
    public List<PopularThemeResponse> findAllPopular() {
        return themeService.findAllPopular();
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "테마 ID") @PathVariable final Long id) {
        themeService.delete(id);
    }
}
