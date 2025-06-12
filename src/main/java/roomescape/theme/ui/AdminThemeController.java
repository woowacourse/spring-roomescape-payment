package roomescape.theme.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.theme.application.ThemeService;
import roomescape.theme.application.dto.ThemeRequest;
import roomescape.theme.application.dto.ThemeResponse;

@Tag(name = "테마 API For 관리자", description = "관리자만 호출 가능한 테마 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("admin/themes")
public class AdminThemeController {
    private final ThemeService themeService;

    @Operation(summary = "테마 생성", description = "테마를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ThemeResponse>> create(@Valid @RequestBody ThemeRequest request) {
        ThemeResponse response = themeService.create(request);
        ApiResponse<ThemeResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable("id") Long id) {
        themeService.deleteById(id);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
