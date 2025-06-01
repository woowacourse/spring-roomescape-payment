package roomescape.service.query;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Theme;
import roomescape.dto.response.ThemeResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ThemeQueryService {

    private final ThemeRepository themeRepository;

    public ThemeQueryService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeResponse> findAllThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::new)
                .toList();
    }

    public List<ThemeResponse> findTopThemes(LocalDate from, LocalDate to, int size) {
        List<Theme> themes = themeRepository.findThemesOrderByReservationCount(from, to, size);
        return themes.stream()
                .map(ThemeResponse::new)
                .toList();
    }

    public Theme getThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 테마는 존재하지 않습니다."));
    }
}
