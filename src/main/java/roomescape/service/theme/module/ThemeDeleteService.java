package roomescape.service.theme.module;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.Theme;
import roomescape.exception.custom.RoomEscapeException;
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
        Theme theme = findThemeById(themeId);
        validateDeletable(theme);
        themeRepository.deleteById(themeId);
    }

    private Theme findThemeById(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 테마 정보 입니다.",
                        "theme_id : " + themeId
                ));
    }

    private void validateDeletable(Theme theme) {
        if (reservationRepository.existsByThemeId(theme.getId())) {
            throw new RoomEscapeException(
                    "예약되어있는 테마는 삭제할 수 없습니다.",
                    "theme_id : " + theme.getId()
            );
        }
    }
}
