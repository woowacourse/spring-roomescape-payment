package roomescape.dto.response;

import java.time.LocalDate;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Status;
import roomescape.domain.WaitingWithRank;

public record ReservationWithStatusResponse(
        Long id,
        String memberName,
        LocalDate date,
        ReservationTimeResponse time,
        String themeName,
        String status,
        PaymentResponse payment
) {
    public static ReservationWithStatusResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse timeDto = ReservationTimeResponse.from(reservation.getReservationTime());
        PaymentResponse paymentDto = PaymentResponse.from(payment);
        return new ReservationWithStatusResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                timeDto,
                reservation.getTheme().getName(),
                Status.CONFIRMED.toString(),
                paymentDto
        );
    }

    public static ReservationWithStatusResponse of(WaitingWithRank waitingWithRank) {
        ReservationTimeResponse dto = ReservationTimeResponse.from(waitingWithRank.waiting().getReservationTime());
        return new ReservationWithStatusResponse(
                waitingWithRank.waiting().getId(),
                waitingWithRank.waiting().getMember().getName(),
                waitingWithRank.waiting().getDate(),
                dto,
                waitingWithRank.waiting().getTheme().getName(),
                String.format("%d번째 예약대기", waitingWithRank.rank()),
                null
        );
    }
}
