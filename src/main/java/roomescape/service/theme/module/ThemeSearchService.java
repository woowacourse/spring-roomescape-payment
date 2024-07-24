package roomescape.service.theme.module;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.dto.theme.ThemeResponse;
import roomescape.exception.custom.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

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
        Theme theme = findThemeById(themeId);
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
                LocalDate.now().minusDays(7), LocalDate.now().minusDays(1));

        return themeRepository.findAllById(popularThemeIds)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    private Theme findThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 테마 정보 입니다.",
                        "theme_id : " + themeId
                ));
    }
}
