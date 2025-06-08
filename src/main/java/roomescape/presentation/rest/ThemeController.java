package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ThemeService;
import roomescape.presentation.request.CreateThemeRequest;
import roomescape.presentation.response.ThemeResponse;

@Tag(name = "Theme", description = "테마 관련 API")
@RestController
@RequestMapping("/themes")
@AllArgsConstructor
public class ThemeController {

    private final ThemeService service;

    @Operation(summary = "테마 생성", description = "관리자가 테마를 생성합니다.")
    @PostMapping
    @ResponseStatus(CREATED)
    public ThemeResponse register(@RequestBody @Valid final CreateThemeRequest request) {
        var theme = service.register(request.name(), request.description(), request.thumbnail());
        return ThemeResponse.from(theme);
    }

    @Operation(summary = "테마 조회", description = "모든 테마를 조회합니다.")
    @GetMapping
    public List<ThemeResponse> getAllThemes() {
        var themes = service.findAllThemes();
        return ThemeResponse.from(themes);
    }

    @Operation(summary = "인기 테마 조회", description = "일정 갯수의 테마를 일정 기간동안 가장 예약이 많은 순서로 조회합니다.")
    @GetMapping(value = "/popular", params = {"startDate", "endDate", "count"})
    public List<ThemeResponse> getAvailableTimes(
            @RequestParam("startDate") final LocalDate startDate,
            @RequestParam("endDate") final LocalDate endDate,
            @RequestParam("count") final Integer count
    ) {
        var themes = service.findPopularThemes(startDate, endDate, count);
        return ThemeResponse.from(themes);
    }

    @Operation(summary = "테마 삭제", description = "관리자가 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") final long id) {
        service.removeById(id);
    }
}
