package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.THEME_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemePopularFilter;
import roomescape.dto.theme.ThemeResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ThemeRepository;

@Transactional
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(final ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public ThemeResponse create(final Theme theme) {
        return ThemeResponse.from(themeRepository.save(theme));
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findAll() {
        final List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ThemeResponse findById(final Long id) {
        final Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));
        return ThemeResponse.from(theme);
    }

    public void deleteById(final Long id) {
        final Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));
        themeRepository.deleteById(theme.getId());
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findPopularThemes() {
        final ThemePopularFilter themePopularFilter = ThemePopularFilter.getThemePopularFilter(LocalDate.now());
        final List<Theme> popularThemes = themeRepository.findPopularThemesBy(themePopularFilter);
        return popularThemes.stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
