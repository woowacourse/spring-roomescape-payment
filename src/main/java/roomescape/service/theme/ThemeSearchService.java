package roomescape.service.theme;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.dto.theme.ThemeResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;
import roomescape.util.DateUtil;

@Service
@Transactional(readOnly = true)
public class ThemeSearchService {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeSearchService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ThemeResponse findTheme(Long themeId) {
        Theme theme = themeRepository.findByIdOrThrow(themeId);
        return ThemeResponse.from(theme);
    }

    public List<ThemeResponse> findAllThemes() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findPopularThemes() {
        List<Long> popularThemeIds = reservationRepository.findTopThemeIdsByReservationCountsForDate(
                DateUtil.getaWeekAgo(), DateUtil.getYesterday());

        return themeRepository.findAllById(popularThemeIds)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
