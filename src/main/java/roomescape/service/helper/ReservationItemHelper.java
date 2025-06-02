package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationItemRepository;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class ReservationItemHelper {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationItemRepository reservationItemRepository;

    @Transactional
    public ReservationItem getOrCreate(LocalDate date, long timeId, long themeId) {
        return reservationItemRepository.findReservationItemByDateAndTimeIdAndThemeId(date, timeId, themeId)
                .orElseGet(() -> create(date, timeId, themeId));
    }

    private ReservationItem create(LocalDate date, long timeId, long themeId) {
        final ReservationTime time = reservationTimeRepository.findById(timeId).orElseThrow();
        final ReservationTheme theme = reservationThemeRepository.findById(themeId).orElseThrow();

        final ReservationItem item = new ReservationItem(date, time, theme);
        return reservationItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public boolean isExistReservationItem(final LocalDate date, final Long timeId, final Long themeId) {
        return reservationItemRepository.existsByDateAndTimeAndTheme(date, timeId, themeId);
    }

    @Transactional
    public void delete(ReservationItem reservationItem) {
        reservationItemRepository.delete(reservationItem);
    }
}
