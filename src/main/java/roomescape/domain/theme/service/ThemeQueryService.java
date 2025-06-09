package roomescape.domain.theme.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.repository.ThemeRepository;
import roomescape.domain.theme.response.FindAllThemeResponse;
import roomescape.domain.theme.response.FindTopThemeResponse;
import roomescape.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class ThemeQueryService {

    private final ThemeRepository themeRepository;

    public ThemeQueryService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<FindAllThemeResponse> findAllThemes() {
        return themeRepository.findAll().stream()
                .map(FindAllThemeResponse::new)
                .toList();
    }

    public List<FindTopThemeResponse> findTopThemes(LocalDate from, LocalDate to, int size) {
        List<Theme> themes = themeRepository.findThemesOrderByReservationCount(from, to, size);
        return themes.stream()
                .map(FindTopThemeResponse::new)
                .toList();
    }

    public Theme getThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 테마는 존재하지 않습니다."));
    }
}
