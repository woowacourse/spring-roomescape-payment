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
                getPayment(reservation),
                OptionalLong.empty()
        );
    }

    public static UserReservationResponse from(WaitingWithRank waitingWithRank) {
        return new UserReservationResponse(
                waitingWithRank.getWaitingId(),
                waitingWithRank.getReservationSlot(),
                WAITING,
                getPayment(waitingWithRank.getReservation()),
                OptionalLong.of(waitingWithRank.getRank())
        );
    }

    private static Payment getPayment(Reservation reservation) {
        if (reservation.getPayment() == null) {
            return EMPTY_PAYMENT;
        }
        return reservation.getPayment();
    }
}
