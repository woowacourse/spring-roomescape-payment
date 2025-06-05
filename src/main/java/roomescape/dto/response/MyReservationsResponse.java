package roomescape.dto.response;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Status;
import roomescape.domain.WaitingWithRank;

import java.time.LocalDate;

public record MyReservationsResponse(
        Long id,
        String memberName,
        LocalDate date,
        ReservationTimeResponse time,
        String themeName,
        String status,
        String paymentKey,
        int amount
) {
    public static MyReservationsResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(reservation.getReservationTime());

        return new MyReservationsResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                dto,
                reservation.getTheme().getName(),
                Status.CONFIRMED.toString(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationsResponse from(WaitingWithRank waitingWithRank) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(waitingWithRank.waiting().getReservationTime());
        return new MyReservationsResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().getMember().getName(),
                waitingWithRank.waiting().getDate(),
                dto,
                waitingWithRank.waiting().getTheme().getName(),
                String.format("%d번째 예약대기", waitingWithRank.rank()),
                null,
                0
        );
    }
}
