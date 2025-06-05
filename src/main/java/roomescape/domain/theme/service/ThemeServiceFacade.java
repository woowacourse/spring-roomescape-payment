package roomescape.domain.theme.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.dto.ThemeRequest;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.theme.entity.Theme;

@RequiredArgsConstructor
@Service
public class ThemeServiceFacade {

    private final ThemeService themeService;

    @Transactional
    public ThemeResponse saveTheme(final ThemeRequest request) {
        final String name = request.name();
        final String description = request.description();
        final String thumbnail = request.thumbnail();

        final Theme savedTheme = themeService.save(name, description, thumbnail);

        return ThemeResponse.from(savedTheme);
    }

    @Transactional
    public void deleteTheme(final Long id) {
        themeService.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getThemes() {
        final List<Theme> themes = themeService.findAll();

        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getPopularThemes() {
        final List<Theme> popularThemes = themeService.findPopularThemes();

        return popularThemes.stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
