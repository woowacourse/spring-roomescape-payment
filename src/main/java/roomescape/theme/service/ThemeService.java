package roomescape.theme.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.config.Performance;
import roomescape.global.exception.ReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final Clock clock;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeResponse saveTheme(final ThemeRequest request) {
        log.info("[테마 추가 요청] name: {}, description: {}, thumbnail: {}", request.name(), request.description(),
                request.thumbnail());
        Theme theme = themeRepository.save(Theme.of(request.name(), request.description(), request.thumbnail()));
        log.info("[테마 추가 성공] themeId: {}", theme.getId());
        return new ThemeResponse(theme);
    }

    @Performance
    public List<ThemeResponse> findAll() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResponse::new)
                .toList();
    }

    @Performance
    public List<PopularThemeResponse> findAllPopular() {
        LocalDate nowDate = LocalDate.now(clock);
        LocalDate startDate = nowDate.minusDays(7);
        LocalDate endDate = nowDate.minusDays(1);
        return themeRepository.findAllPopular(startDate, endDate)
                .stream()
                .map(PopularThemeResponse::new)
                .toList();
    }

    public void delete(final Long themeId) {
        log.info("[테마 삭제 요청] themeId: {}", themeId);
        if (reservationRepository.existsByThemeId((themeId))) {
            log.warn("[테마 삭제 실패] 테마 사용 중 - themeId: {}", themeId);
            throw new ReservationException("해당 테마로 예약된 건이 존재합니다.");
        }
        themeRepository.deleteById(themeId);
        log.info("[테마 삭제 성공] themeId: {}", themeId);
    }
}
