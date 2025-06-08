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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Slf4j
@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse saveTheme(@Valid @RequestBody final ThemeRequest request) {
        log.info("[테마 추가] 이름: {}, 설명: {}, 썸네일: {}",
                request.name(),
                request.description(),
                request.thumbnail()
        );

        return themeService.saveTheme(request);
    }

    @GetMapping
    public List<ThemeResponse> findAll() {
        return themeService.findAll();
    }

    @GetMapping("/ranking")
    public List<PopularThemeResponse> findAllPopular() {
        return themeService.findAllPopular();
    }

    @DeleteMapping("/{themeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable final Long themeId) {
        log.info("[테마 삭제] themeId: {}", themeId);
        themeService.delete(themeId);
    }
}
