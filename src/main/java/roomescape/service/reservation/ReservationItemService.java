package roomescape.service.reservation;

import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.NON_EXIST_RESERVATION_ITEM;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationItemRepository;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.global.exception.roomescape.RoomEscapeException;

@RequiredArgsConstructor
@Service
public class ReservationItemService {

    private final ReservationItemRepository reservationItemRepository;

    public ReservationItem createReservationItemIfNotExist(LocalDate date,
                                                           ReservationTime reservationTime,
                                                           ReservationTheme theme) {
        if (isExistReservationItem(date, reservationTime, theme)) {
            return getReservationItemByDateAndTimeAndTheme(date, reservationTime, theme);
        }
        return addReservationItem(date, reservationTime, theme);
    }

    private ReservationItem addReservationItem(LocalDate date, ReservationTime reservationTime, ReservationTheme theme) {
        return reservationItemRepository.save(
                ReservationItem.builder()
                        .date(date)
                        .time(reservationTime)
                        .theme(theme)
                        .build()
        );
    }

    private ReservationItem getReservationItemByDateAndTimeAndTheme(LocalDate date,
                                                                   ReservationTime reservationTime,
                                                                   ReservationTheme theme) {
        return reservationItemRepository.findReservationItemByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseThrow(() -> new RoomEscapeException(NON_EXIST_RESERVATION_ITEM));
    }

    public void deleteReservationItem(ReservationItem reservationItem) {
        reservationItemRepository.delete(reservationItem);
    }

    public boolean isExistReservationItem(LocalDate date, ReservationTime reservationTime, ReservationTheme theme) {
        return reservationItemRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
    }
}
