package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.request.CreateThemeRequest;
import roomescape.theme.dto.response.CreateThemeResponse;
import roomescape.theme.dto.response.FindPopularThemesResponse;
import roomescape.theme.dto.response.FindThemeResponse;
import roomescape.theme.service.ThemeService;

import java.net.URI;
import java.util.List;

@Tag(name = "테마 API", description = "테마 관련 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(final ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "테마 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "테마 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 테마 명은 공백 문자가 불가능합니다.
                    2. 테마 명은 최대 255자까지 입력이 가능합니다.
                    3. 테마 설명은 공백 문자가 불가능합니다.
                    4. 테마 설명은 최대 255자까지 입력이 가능합니다.
                    5. 테마 썸네일은 공백 문자가 불가능합니다.
                    6. 테마 썸네일은 최대 255자까지 입력이 가능합니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<CreateThemeResponse> createTheme(@Valid @RequestBody CreateThemeRequest createThemeRequest) {
        CreateThemeResponse createThemeResponse = themeService.createTheme(createThemeRequest);
        return ResponseEntity.created(URI.create("/themes/" + createThemeResponse.id())).body(createThemeResponse);
    }

    @Operation(summary = "테마 목록 조회 API")
    @ApiResponse(responseCode = "200", description = "테마 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<FindThemeResponse>> getThemes() {
        return ResponseEntity.ok(themeService.getThemes());
    }

    @Operation(summary = "인기 테마 조회 API")
    @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<FindPopularThemesResponse>> getPopularThemes(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(themeService.getPopularThemes(pageable));
    }

    @Operation(summary = "테마 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "식별자에 해당하는 테마을 사용 중인 예약이 존재합니다. 삭제가 불가능합니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 테마가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
