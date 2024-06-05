package roomescape.service.theme.module;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
public class ThemeDeleteService {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeDeleteService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void deleteTheme(Long themeId) {
        Theme theme = themeRepository.findByIdOrThrow(themeId);
        validateDeletable(theme);
        themeRepository.deleteById(themeId);
    }

    private void validateDeletable(Theme theme) {
        if (reservationRepository.existsByThemeId(theme.getId())) {
            throw new RoomEscapeException(
                    ErrorCode.THEME_NOT_DELETE_BY_DUPLICATE,
                    "theme_id = " + theme.getId()
            );
        }
    }
}
