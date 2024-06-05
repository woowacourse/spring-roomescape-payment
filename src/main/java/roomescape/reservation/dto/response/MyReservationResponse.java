package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        Integer order,
        String paymentKey,
        Integer amount
) {

    public static MyReservationResponse toResponse(Reservation reservation, int order, Payment payment) {
        return new MyReservationResponse(reservation.getId(), reservation.getTheme().getName(), reservation.getDate(),
                reservation.getTime().getStartAt(), reservation.getStatus().getStatus(), order, payment.getPaymentKey(),
                payment.getAmount());
    }
}
