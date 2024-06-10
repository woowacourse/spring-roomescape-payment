package roomescape.theme.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Tag(name = "Theme", description = "Theme API")
@RestController
public class ThemeController {
    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @Operation(summary = "모든 테마 조회", description = "모든 테마를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 테마 조회 성공", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "400", description = "모든 테마 조회 실패")})
    @GetMapping("/themes")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @Operation(summary = "인기 테마 조회", description = "인기 테마를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 테마 조회 성공", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "400", description = "인기 테마 조회 실패")})
    @GetMapping("/themes/ranking")
    public List<ThemeResponse> findAndOrderByPopularity(
            @Parameter(required = true, description = "인기 테마 조회 개수") @RequestParam int count) {
        return themeService.findAndOrderByPopularity(count);
    }

    @Operation(summary = "테마 생성", description = "테마를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "테마 생성 성공", content = @Content(schema = @Schema(implementation = ThemeResponse.class))),
            @ApiResponse(responseCode = "400", description = "테마 생성 실패")})
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> save(@RequestBody ThemeRequest themeRequest) {
        ThemeResponse savedThemeResponse = themeService.save(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + savedThemeResponse.id()))
                .body(savedThemeResponse);
    }

    @Operation(summary = "테마 삭제", description = "테마를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "테마 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "테마 삭제 실패")})
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(required = true, name = "id") @PathVariable long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
