package roomescape.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.repository.ThemeRepository;

@Service
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
            throw new IllegalStateException("이미 예약이 존재해서 테마를 삭제할 수 없습니다.");
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
