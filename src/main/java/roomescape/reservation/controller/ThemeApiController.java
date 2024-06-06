package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.MultipleResponses;
import roomescape.reservation.domain.Period;
import roomescape.reservation.dto.PopularThemeResponse;
import roomescape.reservation.dto.ThemeResponse;
import roomescape.reservation.dto.ThemeSaveRequest;
import roomescape.reservation.service.ThemeService;

@Tag(name = "테마 API", description = "방탈출 테마 API 입니다.")
@RestController
public class ThemeApiController {

    private final ThemeService themeService;

    public ThemeApiController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "인기 테마 조회 API", description = "선택한 기간의 인기 테마를 limitCount개 만큼 조회 합니다.")
    @GetMapping("/themes/popular")
    public ResponseEntity<MultipleResponses<PopularThemeResponse>> findTopTenThemesOfLastWeek(
            @RequestParam(name = "period", defaultValue = "WEEK") Period period,
            @RequestParam(name = "limitCount", defaultValue = "10") int limitCount
    ) {
        List<PopularThemeResponse> popularThemeResponses = themeService.findPopularThemesBetweenPeriod(period, limitCount);

        return ResponseEntity.ok(new MultipleResponses<>(popularThemeResponses));
    }

    @Operation(summary = "전체 테마 조회 API", description = "전체 테마를 조회 합니다.")
    @GetMapping("/themes")
    public ResponseEntity<MultipleResponses<ThemeResponse>> findAll() {
        List<ThemeResponse> themeResponses = themeService.findAll();

        return ResponseEntity.ok(new MultipleResponses<>(themeResponses));
    }

    @Operation(summary = "테마 추가 API", description = "테마를 추가 합니다.")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> save(@Valid @RequestBody ThemeSaveRequest themeSaveRequest) {
        ThemeResponse themeResponse = themeService.save(themeSaveRequest);

        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id())).body(themeResponse);
    }

    @Operation(summary = "테마 삭제 API", description = "테마를 삭제 합니다.")
    @Parameter(name = "id", description = "삭제할 테마의 id", schema = @Schema(type = "integer", example = "1"))
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
