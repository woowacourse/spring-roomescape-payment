package roomescape.theme.application;

import java.time.Clock;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationtime.exception.ReservationTimeInUseException;
import roomescape.theme.domain.Theme;
import roomescape.theme.presentation.dto.request.ThemeCreateWebRequest;
import roomescape.theme.presentation.dto.response.ThemeWebResponse;

@Service
@Transactional
@Slf4j
public class ThemeApplicationService {

    private final ThemeDataService themeDataService;
    private final ReservationSlotDataService reservationSlotDataService;
    private final Clock clock;

    public ThemeApplicationService(final ThemeDataService themeDataService,
                                   final ReservationSlotDataService reservationSlotDataService,
                                   final Clock clock) {
        this.themeDataService = themeDataService;
        this.reservationSlotDataService = reservationSlotDataService;
        this.clock = clock;
    }

    public ThemeWebResponse create(final ThemeCreateWebRequest request) {
        log.info("테마 생성: name={}, description={}", request.name(), request.description());

        Theme theme = themeDataService.save(request.toTheme());

        log.info("테마 생성 완료: id={}, name={}", theme.getId(), theme.getName());

        return ThemeWebResponse.from(theme);
    }

    public List<ThemeWebResponse> findAll() {
        return themeDataService.findAll().stream()
                .map(ThemeWebResponse::from)
                .toList();
    }

    public List<ThemeWebResponse> findPopular(int days, int limit) {
        Page<Theme> popularThemes = themeDataService.findPopularThemes(clock, days, limit);
        return popularThemes.getContent().stream()
                .map(ThemeWebResponse::from)
                .toList();
    }

    public void delete(Long id) {
        log.info("테마 삭제 시도: id={}", id);

        validateExist(id);
        themeDataService.deleteById(id);

        log.info("테마 삭제 완료: id={}", id);
    }

    private void validateExist(final Long id) {
        if (reservationSlotDataService.existsByThemeId(id)) {
            log.warn("테마 삭제 실패 - 예약 존재: id={}", id);
            throw new ReservationTimeInUseException("해당 테마에 대한 예약이 존재하여 삭제할 수 없습니다.");
        }
    }
}
