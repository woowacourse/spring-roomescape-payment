package roomescape.theme.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @PostMapping("/admin/themes")
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse saveTheme(@Valid @RequestBody final ThemeRequest request) {
        return themeService.saveTheme(request);
    }

    @GetMapping("/themes")
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @GetMapping("/themes/ranking")
    public List<PopularThemeResponse> findAllPopular() {
        return themeService.findAllPopular();
    }

    @DeleteMapping("/admin/themes/{themeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable final Long themeId) {
        themeService.delete(themeId);
    }
}
