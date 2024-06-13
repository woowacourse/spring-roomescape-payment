package roomescape.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.response.ThemeResponse;
import roomescape.domain.theme.Theme;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Service
public class ThemeService {
    private static final int POPULAR_START_DATE = 8;
    private static final int POPULAR_END_DATE = 1;
    private static final int POPULAR_THEME_COUNT = 10;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ThemeResponse save(CreateThemeRequest request) {
        Theme theme = new Theme(request.name(), request.description(), request.thumbnail());
        validateDuplication(request.name());
        return ThemeResponse.from(themeRepository.save(theme));
    }

    private void validateDuplication(String name) {
        if (themeRepository.existsByName(name)) {
            throw new RoomescapeException("같은 이름의 테마가 이미 존재합니다.");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new RoomescapeException("해당 테마를 사용하는 예약이 존재하여 삭제할 수 없습니다.");
        }
        themeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findAll() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThemeResponse> findPopular() {
        LocalDate start = LocalDate.now().minusDays(POPULAR_START_DATE);
        LocalDate end = LocalDate.now().minusDays(POPULAR_END_DATE);
        List<Theme> themes = themeRepository.findPopular(start, end, POPULAR_THEME_COUNT);
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }
}
