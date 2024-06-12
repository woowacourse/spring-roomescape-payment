package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.reservation.domain.PopularThemes;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.ThemeCreateRequest;
import roomescape.reservation.dto.response.PopularThemeResponse;
import roomescape.reservation.dto.response.ThemeResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ThemeResponse save(ThemeCreateRequest themeCreateRequest) {
        themeRepository.findByThemeName(themeCreateRequest.name())
                .ifPresent(empty -> {
                    throw new IllegalArgumentException("이미 존재하는 테마 이름입니다.");
                });

        Theme theme = themeRepository.save(themeCreateRequest.toTheme());

        return ThemeResponse.toResponse(theme);
    }

    public ThemeResponse findById(Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다."));

        return ThemeResponse.toResponse(theme);
    }

    public List<ThemeResponse> findAll() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResponse::toResponse)
                .toList();
    }

    public List<PopularThemeResponse> findPopularThemeBetweenWeekLimitTen() {
        List<Theme> themes = getThemeBetweenWeek();
        PopularThemes popularThemes = new PopularThemes(themes);
        return popularThemes.getPopularThemes().stream()
                .map(PopularThemeResponse::toResponse)
                .toList();
    }

    private List<Theme> getThemeBetweenWeek() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysBefore = today.minusDays(7);

        return reservationRepository.findByDateBetween(sevenDaysBefore, today).stream()
                .map(Reservation::getTheme)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        themeRepository.deleteById(id);
    }
}
