package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.PopularThemeRequest;
import roomescape.service.dto.response.ThemeResponses;

@Tag(name = "[USER] 테마 API", description = "사용자가 테마를 조회할 수 있습니다.")
@RestController
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "전체 테마를 반환합니다.")
    })
    @GetMapping("/themes")
    public ResponseEntity<ThemeResponses> getThemes() {
        ThemeResponses themeResponses = themeService.getThemes();
        return ResponseEntity.ok(themeResponses);
    }

    @Operation(summary = "인기테마 조회 API")
    @GetMapping("/themes/popular")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "인기테마를 반환합니다.")
    })
    public ResponseEntity<ThemeResponses> getPopularThemes(
            @ModelAttribute @Valid PopularThemeRequest popularThemeRequest
    ) {
        ThemeResponses popularThemes = themeService.getPopularThemes(popularThemeRequest);
        return ResponseEntity.ok(popularThemes);
    }
}
