package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.dto.request.theme.ThemeRequest;
import roomescape.dto.response.theme.ThemePriceResponse;
import roomescape.dto.response.theme.ThemeResponse;
import roomescape.exception.RoomescapeException;

@Service
public class ThemeService {
    private static final int START_DAY_TO_SUBTRACT = 8;
    private static final int END_DATE_TO_SUBTRACT = 1;
    private static final int COUNT_OF_LIMIT = 10;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ThemeResponse save(ThemeRequest themeRequest) {
        Theme savedTheme = themeRepository.save(themeRequest.toTheme());
        return ThemeResponse.from(savedTheme);
    }

    public List<ThemeResponse> findAll() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void deleteById(long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND,
                        String.format("존재하지 않는 테마입니다. 요청 테마 id:%d", id)));
        themeRepository.deleteById(theme.getId());
    }

    public List<ThemeResponse> findPopularThemes() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(START_DAY_TO_SUBTRACT);
        LocalDate endDate = today.minusDays(END_DATE_TO_SUBTRACT);
        return reservationRepository.findPopularThemesDateBetween(startDate, endDate).stream()
                .limit(COUNT_OF_LIMIT)
                .map(ThemeResponse::from)
                .toList();
    }

    public ThemePriceResponse findThemePriceById(long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(HttpStatus.NOT_FOUND, "존재하지 않는 테마입니다."));
        return new ThemePriceResponse(theme.getPrice());
    }
}
