package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/themes")
@Tag(name = "테마", description = "테마 관련 API")
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "모든 테마 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @SecurityRequirements(value = {})
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        List<ThemeResponse> responses = themeService.getAllThemes();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "인기 테마 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @SecurityRequirements(value = {})
    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> getPopularThemes(
            @RequestParam("limit") int limit
    ) {
        List<ThemeResponse> response = themeService.getPopularThemes(limit);
        return ResponseEntity.ok().body(response);
    }
}
