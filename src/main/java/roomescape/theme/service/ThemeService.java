package roomescape.theme.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.InvalidReservationException;
import roomescape.common.util.DateTime;
import roomescape.reservation.domain.ReservationPeriod;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.PopularThemeResponse;
import roomescape.theme.dto.response.ThemeResponse;

@Service
public class ThemeService {

    private static final Logger log = LoggerFactory.getLogger(ThemeService.class);
    private static final int POPULAR_THEME_COUNT = 10;
    private static final int START_OFFSET_DAYS = 8;
    private static final int END_OFFSET_DAYS = 1;

    private final DateTime dateTime;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(final DateTime dateTime,
                        final ThemeRepository themeRepository,
                        final ReservationRepository reservationRepository) {
        this.dateTime = dateTime;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ThemeResponse createTheme(final ThemeRequest request) {
        Theme theme = Theme.createWithoutId(request.name(), request.description(), request.thumbnail());
        Theme save = themeRepository.save(theme);

        log.info("테마 생성 성공 - id: {}, name: {}", save.getId(), save.getName());
        return ThemeResponse.from(save);
    }

    @Transactional
    public void deleteThemeById(final Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            log.error("테마 삭제 실패 - 사용 중인 테마 id: {}", id);
            throw new InvalidReservationException("예약한 기록이 존재하여 삭제할 수 없습니다.");
        }
        log.info("테마 삭제 성공 - id: {}", id);
        themeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> getThemes() {
        return themeRepository.findAll().stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularThemeResponse> getPopularThemes() {
        ReservationPeriod period = new ReservationPeriod(dateTime.nowDate(), START_OFFSET_DAYS, END_OFFSET_DAYS);

        return themeRepository.findPopularThemes(period, POPULAR_THEME_COUNT).stream()
                .map(theme -> new PopularThemeResponse(theme.getName(), theme.getThumbnail(), theme.getDescription()))
                .toList();
    }
}
