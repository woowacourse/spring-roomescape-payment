package roomescape.service.dto.response;

import roomescape.domain.reservation.*;

import java.util.Optional;
import java.util.OptionalLong;

import static roomescape.domain.reservation.ReservationStatus.RESERVED;
import static roomescape.domain.reservation.ReservationStatus.WAITING;

public record UserReservationResponse(
        long id,
        ReservationSlot reservationSlot,
        ReservationStatus status,
        OptionalLong rank,
        Optional<Payment> payment
) {
    public static UserReservationResponse reserved(ReservationWithPay reservationWithPay) {
        return new UserReservationResponse(
                reservationWithPay.reservation().getId(),
                reservationWithPay.reservation().getReservationSlot(),
                RESERVED,
                OptionalLong.empty(),
                Optional.ofNullable(reservationWithPay.payment())
        );
    }

    public static UserReservationResponse from(WaitingWithRank waitingWithRank) {
        return new UserReservationResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().getReservation().getReservationSlot(),
                WAITING,
                OptionalLong.of(waitingWithRank.rank() + 1),
                Optional.empty()
        );
    }
}
