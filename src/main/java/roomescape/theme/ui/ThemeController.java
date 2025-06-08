package roomescape.theme.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeResponse;

@Tag(name = "테마 API", description = "테마 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("themes")
public class ThemeController {
    private final ThemeService themeService;

    @Operation(summary = "테마 조회", description = "모든 테마를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getAll() {
        List<ThemeResponse> response = themeService.findAll();
        ApiResponse<List<ThemeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "인기 테마 조회", description = "일주일 이내 예약이 많이 됐던 상위 테마 10개를 조회합니다.")
    @GetMapping("/ranked")
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getRankedByPeriod() {
        List<ThemeResponse> response = themeService.findRankedByPeriod();
        ApiResponse<List<ThemeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }
}
