package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.dto.ThemesResponse;

@Service
@Transactional
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Transactional(readOnly = true)
    public Theme findThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.THEME_NOT_FOUND,
                        String.format("[themeId: %d]", id), HttpStatus.BAD_REQUEST));
    }

    @Transactional(readOnly = true)
    public ThemesResponse findAllThemes() {
        List<ThemeResponse> response = themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();

        return new ThemesResponse(response);
    }

    @Transactional(readOnly = true)
    public ThemesResponse getTop10Themes(LocalDate today) {
        LocalDate startDate = today.minusDays(7);
        LocalDate endDate = today.minusDays(1);
        int limit = 10;

        List<ThemeResponse> response = themeRepository.findTopNThemeBetweenStartDateAndEndDate(startDate, endDate,
                        limit)
                .stream()
                .map(ThemeResponse::from)
                .toList();

        return new ThemesResponse(response);
    }

    public ThemeResponse addTheme(ThemeRequest request) {
        Theme theme = themeRepository.save(new Theme(request.name(), request.description(), request.thumbnail()));

        return ThemeResponse.from(theme);
    }

    public void removeThemeById(Long id) {
        themeRepository.deleteById(id);
    }
}
