package roomescape.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;

public record UserReservationPaymentResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    private static final String RESERVED = "예약";
    private static final String WAITING_ORDER = "%d번째 예약 대기";

    public static UserReservationPaymentResponse of(final Reservation reservation, final Payment payment) {
        return new UserReservationPaymentResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static UserReservationPaymentResponse of(final Waiting waiting) {
        return new UserReservationPaymentResponse(
                waiting.getId(),
                waiting.getReservation().getTheme().getThemeName(),
                waiting.getReservation().getDate(),
                waiting.getReservation().getTime().getStartAt(),
                String.format(WAITING_ORDER, waiting.getWaitingOrderValue()),
                "",
                BigDecimal.ZERO
        );
    }
}
