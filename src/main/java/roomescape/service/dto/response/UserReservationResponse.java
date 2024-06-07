package roomescape.service.dto.response;

import static roomescape.domain.reservation.Payment.EMPTY_PAYMENT;
import static roomescape.domain.reservation.ReservationStatus.RESERVED;
import static roomescape.domain.reservation.ReservationStatus.WAITING;

import java.util.OptionalLong;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationSlot;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.WaitingWithRank;

public record UserReservationResponse(
        long id,
        ReservationSlot reservationSlot,
        ReservationStatus status,
        Payment payment,
        OptionalLong rank
) {
    public static UserReservationResponse reserved(Reservation reservation) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getReservationSlot(),
                RESERVED,
                reservation.getPayment(),
                OptionalLong.empty()
        );
    }

    public static UserReservationResponse from(WaitingWithRank waitingWithRank) {
        return new UserReservationResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().getReservation().getReservationSlot(),
                WAITING,
                EMPTY_PAYMENT,//TODO null 처리 고민
                OptionalLong.of(waitingWithRank.rank() + 1)
        );
    }
}
