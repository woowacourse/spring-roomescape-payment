package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        Integer order,
        String paymentKey,
        Long amount
) {

    private static final long FREE_AMOUNT = 0L;
    private static final String ADMIN_RESERVED = "관리자에 의한 예약";

    public MyReservationResponse(Payment payment) {
        this(payment.getReservation().getId(), payment.getReservation().getTheme().getName(),
                payment.getReservation().getDate(),
                payment.getReservation().getTime().getStartAt(), payment.getReservation().getStatus().getStatus(),
                0, payment.getPaymentKey(), payment.getAmount());
    }

    public MyReservationResponse(Reservation reservation, Integer order) {
        this(reservation.getId(), reservation.getTheme().getName(), reservation.getDate(),
                reservation.getTime().getStartAt(), reservation.getStatus()
                        .getStatus(), order, ADMIN_RESERVED, FREE_AMOUNT);

    }
}
