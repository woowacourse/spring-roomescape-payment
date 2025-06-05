package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "테마 저장 API")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse saveTheme(@Valid @RequestBody final ThemeRequest request) {
        return themeService.saveTheme(request);
    }

    @Operation(summary = "테마 조회 API")
    @GetMapping
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @Operation(summary = "테마 순위권 조회 API")
    @GetMapping("/ranking")
    public List<PopularThemeResponse> findAllPopular() {
        return themeService.findAllPopular();
    }

    @Operation(summary = "테마 삭제 API")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long id) {
        themeService.delete(id);
    }
}
