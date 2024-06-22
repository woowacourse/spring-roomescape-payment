package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import roomescape.reservation.controller.dto.ThemeRequest;
import roomescape.reservation.controller.dto.ThemeResponse;
import roomescape.reservation.service.ThemeService;

@Controller
@RequestMapping("/themes")
@Tag(name = "Theme API", description = "테마 관련 API")
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    @Operation(summary = "모든 테마를 조회한다.")
    @ApiResponse(responseCode = "200", description = "OK : 모든 테마를 리스트로 반환")
    public ResponseEntity<List<ThemeResponse>> findAll() {
        return ResponseEntity.ok(themeService.findAllThemes());
    }

    @PostMapping
    @Operation(summary = "테마를 생성한다.")
    @ApiResponse(responseCode = "201", description = "Created")
    @Parameter(name = "themeRequest", description = "테마 정보 DTO", required = true)
    public ResponseEntity<ThemeResponse> create(@RequestBody @Valid ThemeRequest themeRequest) {
        ThemeResponse response = themeService.create(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + response.id())).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "테마를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "400", description = "예약이 존재한 테마생 삭제하는 경우 발생")
    })
    @Parameter(name = "themeId", description = " 테마 ID", required = true)
    public ResponseEntity<Void> delete(@PathVariable("id") @Min(1) long themeId) {
        themeService.delete(themeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 테마를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 잘못된 경우 발생")
    })
    @Parameters({
            @Parameter(name = "startDate", description = "시작 날짜"),
            @Parameter(name = "endDate", description = "종료 날짜"),
            @Parameter(name = "limit", description = "조회 개수")
    })
    public ResponseEntity<List<ThemeResponse>> findPopular(
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "limit", required = false, defaultValue = "10")
            @Min(value = 1) @Max(value = 20) int limit) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return ResponseEntity.ok(themeService.findPopularThemes(startDate, endDate, limit));
    }
}
