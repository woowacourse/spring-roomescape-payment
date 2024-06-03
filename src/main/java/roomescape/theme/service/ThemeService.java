package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.PopularThemePeriod;

@Service
public class ThemeService {
    private static final int POPULAR_THEME_LIMIT = 10;

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<ThemeResponse> findThemes() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findPopularThemes() {
        PopularThemePeriod popularThemePeriod = new PopularThemePeriod();
        LocalDate startDate = popularThemePeriod.getStartDate();
        LocalDate endDate = popularThemePeriod.getEndDate();

        return themeRepository.findOrderByReservationCountDesc(startDate, endDate, POPULAR_THEME_LIMIT)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public ThemeResponse createTheme(ThemeCreateRequest request) {
        Theme createdTheme = themeRepository.save(request.createTheme());
        return ThemeResponse.from(createdTheme);
    }

    public void deleteTheme(Long id) {
        themeRepository.deleteById(id);
    }
}
