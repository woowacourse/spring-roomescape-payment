package roomescape.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
import roomescape.service.theme.ThemeService;
import roomescape.service.theme.dto.ThemeRequest;
import roomescape.service.theme.dto.ThemeResponse;

@Tag(name = "Theme", description = "테마 컨트롤러입니다.")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    @ApiSuccessResponse.Created("테마 등록")
    @ApiErrorResponse.BadRequest
    public ResponseEntity<ThemeResponse> createTheme(@RequestBody @Valid ThemeRequest themeRequest) {
        ThemeResponse themeResponse = themeService.create(themeRequest);
        return ResponseEntity.created(URI.create("/themes/" + themeResponse.id())).body(themeResponse);
    }

    @GetMapping
    @ApiSuccessResponse.Ok("모든 테마 조회")
    public List<ThemeResponse> findAllThemes() {
        return themeService.findAll();
    }

    @GetMapping("/popular")
    @ApiSuccessResponse.Ok("인기 테마 조회")
    public List<ThemeResponse> findPopularThemes() {
        return themeService.findPopularThemes();
    }

    @DeleteMapping("/{id}")
    @ApiSuccessResponse.NoContent("id를 통해 테마 삭제")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") long id) {
        themeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
