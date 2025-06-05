package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.helper.ReservationItemHelper;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationThemeService {

    public static final int POPULAR_THEME_AMOUNT = 10;
    public static final int POPULAR_THEME_DATE_FROM = 7;
    public static final int POPULAR_THEME_DATE_TO = 1;

    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationItemHelper reservationItemHelper;

    @Transactional
    public ReservationThemeResponse save(final ReservationThemeRequest request) {
        final ReservationTheme reservationTheme = ReservationTheme.builder()
                .name(request.name())
                .description(request.description())
                .thumbnail(request.thumbnail())
                .build();
        validateUniqueThemes(reservationTheme);
        ReservationTheme saved = reservationThemeRepository.save(reservationTheme);
        return ReservationThemeResponse.from(saved);
    }

    private void validateUniqueThemes(final ReservationTheme reservationTheme) {
        if (reservationThemeRepository.existsByName(reservationTheme.getName())) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 테마 입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationThemeResponse> getAll() {
        List<ReservationTheme> reservationThemes = reservationThemeRepository.findAll();
        return reservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationThemeResponse> getPopulars() {
        List<ReservationTheme> popularReservationThemes = reservationThemeRepository.findWeeklyThemeOrderByCountDesc(
                POPULAR_THEME_AMOUNT,
                LocalDate.now().minusDays(POPULAR_THEME_DATE_FROM),
                LocalDate.now().minusDays(POPULAR_THEME_DATE_TO)
        );
        return popularReservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    @Transactional
    public void remove(final long id) {
        if (!reservationThemeRepository.existsById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 테마입니다.");
        }
        if (!reservationThemeRepository.isAvailableToRemove(id)) {
            throw new IllegalArgumentException("[ERROR] 예약이 존재해 테마를 삭제할 수 없습니다.");
        }
        reservationItemHelper.deleteAllReservationItemByThemeId(id);
        reservationThemeRepository.deleteById(id);
    }
}
