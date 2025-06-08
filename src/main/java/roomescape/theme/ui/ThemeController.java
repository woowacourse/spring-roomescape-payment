package roomescape.theme.ui;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeResponse;

@RestController
@AllArgsConstructor
@RequestMapping("themes")
public class ThemeController {
    private final ThemeService themeService;

    @Operation(
            summary = "전체 테마 목록 조회",
            description = "저장된 모든 테마를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getAll() {
        List<ThemeResponse> response = themeService.findAll();
        ApiResponse<List<ThemeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "기간별 인기 테마 순위 조회",
            description = "특정 기간 내 인기 있는 테마를 순위별로 조회합니다."
    )
    @GetMapping("/ranked")
    public ResponseEntity<ApiResponse<List<ThemeResponse>>> getRankedByPeriod() {
        List<ThemeResponse> response = themeService.findRankedByPeriod();
        ApiResponse<List<ThemeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }
}
