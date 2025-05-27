package roomescape.theme.controller;


import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.theme.controller.dto.CreateThemeRequest;
import roomescape.theme.controller.dto.ThemeResponse;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.service.ThemeCommandService;
import roomescape.theme.service.ThemeQueryService;

@Service
public class ThemeService {
    private final ThemeQueryService themeQueryService;
    private final ThemeCommandService themeCommandService;

    public ThemeService(final ThemeQueryService themeQueryService, final ThemeCommandService themeCommandService) {
        this.themeQueryService = themeQueryService;
        this.themeCommandService = themeCommandService;
    }

    public List<ThemeResponse> findAllThemes() {
        return ThemeResponse.from(themeQueryService.findAll());
    }

    public ThemeResponse createTheme(final CreateThemeRequest request) {
        return ThemeResponse.from(themeCommandService.createTheme(
                new ThemeName(request.name()),
                new ThemeDescription(request.description()),
                new ThemeThumbnail(request.thumbnail())
        ));
    }

    public void deleteThemeById(final Long themeId) {
        themeCommandService.deleteThemeById(themeId);
    }

    public List<ThemeResponse> getWeeklyPopularThemes() {
        return ThemeResponse.from(themeQueryService.getWeeklyPopularThemes());
    }
}
