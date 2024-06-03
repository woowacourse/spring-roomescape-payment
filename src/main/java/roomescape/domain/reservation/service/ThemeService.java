package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.domain.reservation.dto.SaveThemeRequest;
import roomescape.domain.reservation.dto.ThemeDto;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.repository.CustomThemeRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ThemeService {

    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final CustomThemeRepository customThemeRepository;

    public ThemeService(
            final CustomThemeRepository customThemeRepository,
            final ReservationRepository reservationRepository,
            final ThemeRepository themeRepository
    ) {
        this.customThemeRepository = customThemeRepository;
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
    }

    public List<ThemeDto> getThemes() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeDto::from)
                .toList();
    }

    public ThemeDto saveTheme(final SaveThemeRequest saveThemeRequest) {
        final Theme savedTheme = themeRepository.save(saveThemeRequest.toTheme());
        return ThemeDto.from(savedTheme);
    }

    public void deleteTheme(final Long themeId) {
        validateReservationOfIncludeThemeExist(themeId);
        themeRepository.deleteById(themeId);
    }

    private void validateReservationOfIncludeThemeExist(final Long themeId) {
        if (reservationRepository.existsByThemeId(themeId)) {
            throw new IllegalArgumentException("예약에 포함된 테마 정보는 삭제할 수 없습니다.");
        }
    }

    public List<ThemeDto> getPopularThemes() {
        final ReservationDate startAt = new ReservationDate(LocalDate.now().minusDays(7));
        final ReservationDate endAt = new ReservationDate(LocalDate.now().minusDays(1));
        final int maximumThemeCount = 10;

        return customThemeRepository.findPopularThemes(startAt, endAt, maximumThemeCount)
                .stream()
                .map(ThemeDto::from)
                .toList();
    }
}
