package roomescape.theme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.logging.LogExecution;
import roomescape.common.util.DateTime;
import roomescape.reservation.domain.ReservationPeriod;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.PopularThemeResponse;
import roomescape.theme.dto.response.ThemeResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private static final int POPULAR_THEME_COUNT = 10;
    private static final int START_OFFSET_DAYS = 8;
    private static final int END_OFFSET_DAYS = 1;

    private final DateTime dateTime;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    @LogExecution
    public ThemeResponse createTheme(final ThemeRequest request) {
        Theme theme = Theme.createWithoutId(request.name(), request.description(), request.thumbnail());
        Theme save = themeRepository.save(theme);

        return ThemeResponse.from(save);
    }

    @Transactional
    @LogExecution
    public void deleteThemeById(final Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new IllegalArgumentException("예약한 기록이 존재하여 삭제할 수 없습니다.");
        }
        themeRepository.deleteById(id);
    }

    public List<ThemeResponse> getThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<PopularThemeResponse> getPopularThemes() {
        ReservationPeriod period = new ReservationPeriod(dateTime.nowDate(), START_OFFSET_DAYS, END_OFFSET_DAYS);

        return themeRepository.findPopularThemes(period, POPULAR_THEME_COUNT).stream()
                .map(theme -> new PopularThemeResponse(theme.getName(), theme.getThumbnail(), theme.getDescription()))
                .toList();
    }
}
