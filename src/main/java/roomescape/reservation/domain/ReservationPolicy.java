package roomescape.reservation.domain;

import org.springframework.stereotype.Component;
import roomescape.exception.ReservationException;

@Component
public class ReservationPolicy {

    public void validateReservationAvailable(final Reservation reservation, final boolean existsDuplicatedReservation) {
        validateUniqueReservation(existsDuplicatedReservation);
        validateNotPast(reservation);
    }

    private void validateUniqueReservation(final boolean existsDuplicatedReservation) {
        if (existsDuplicatedReservation) {
            throw new ReservationException("이미 해당 날짜에 예약이 존재합니다.");
        }
    }

    private void validateNotPast(final Reservation reservation) {
        if (reservation.isPast()) {
            throw new ReservationException("지난 날짜와 시간에 대한 예약은 불가능합니다.");
        }
    }
}
