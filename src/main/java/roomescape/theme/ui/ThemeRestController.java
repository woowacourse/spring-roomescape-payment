package roomescape.theme.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.theme.application.ThemeService;
import roomescape.theme.ui.dto.CreateThemeRequest;
import roomescape.theme.ui.dto.ThemeResponse;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
@Tag(name = "테마", description = "테마 관련 API")
public class ThemeRestController {

    private final ThemeService themeService;

    @PostMapping
    @RequiresRole(authRoles = {ADMIN})
    @Operation(summary = "테마 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public ResponseEntity<ThemeResponse> create(
            @RequestBody @Valid final CreateThemeRequest request
    ) {
        final ThemeResponse response = themeService.create(request);

        return ResponseEntity.created(URI.create("/themes/" + response.id()))
                .body(response);
    }

    @DeleteMapping({"/{id}"})
    @RequiresRole(authRoles = {ADMIN})
    @Operation(summary = "테마 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public ResponseEntity<Void> delete(
            @PathVariable final Long id
    ) {
        themeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "모든 테마 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public ResponseEntity<List<ThemeResponse>> findAll() {
        final List<ThemeResponse> themeResponses = themeService.findAll();

        return ResponseEntity.ok(themeResponses);
    }

    @GetMapping("/popular-list")
    @Operation(summary = "인기 테마 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public ResponseEntity<List<ThemeResponse>> findPopularThemes() {
        final List<ThemeResponse> popularThemes = themeService.findPopularThemes();

        return ResponseEntity.ok(popularThemes);
    }
}
