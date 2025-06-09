package roomescape.theme.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.repository.ThemeRepository;

@Service
@Slf4j
public class ThemeCommandService {

    private final ThemeRepository themeRepository;
    private final ReservationQueryService reservationQueryService;

    public ThemeCommandService(
            final ThemeRepository themeRepository,
            final ReservationQueryService reservationQueryService
    ) {
        this.themeRepository = themeRepository;
        this.reservationQueryService = reservationQueryService;
    }

    @Transactional
    public void deleteThemeById(final Long themeId) {
        if (reservationQueryService.existsReservationInTheme(themeId)) {
            log.warn("[DELETE-THEME-FAIL] 예약 존재로 삭제 불가 - themeId: {}", themeId);
            throw new BadRequestException("예약이 존재하여 테마를 삭제할 수 없습니다.");
        }
        themeRepository.deleteById(themeId);
    }

    public Theme createTheme(
            final ThemeName name,
            final ThemeDescription description,
            final ThemeThumbnail themeThumbnail
    ) {
        return themeRepository.save(new Theme(
                null,
                name,
                description,
                themeThumbnail
        ));
    }
}
