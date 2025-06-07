package roomescape.dto.response;

import java.time.LocalDate;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;

public record ReservationForMemberResponse(
        Long id,
        String memberName,
        LocalDate date,
        ReservationTimeResponse time,
        String themeName,
        PaymentResponse payment
) {
    public static ReservationForMemberResponse of(Reservation reservation, Payment payment) {
        ReservationTimeResponse timeDto = ReservationTimeResponse.from(reservation.getReservationTime());
        PaymentResponse paymentDto = PaymentResponse.from(payment);
        return new ReservationForMemberResponse(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getDate(),
                timeDto,
                reservation.getTheme().getName(),
                paymentDto
        );
    }
}
