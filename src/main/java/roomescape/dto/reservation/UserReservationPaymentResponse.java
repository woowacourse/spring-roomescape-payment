package roomescape.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.waiting.WaitingResponse;

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
    private static final String PENDING = "결제 대기";

    public static UserReservationPaymentResponse of(final Reservation reservation, final PaymentResponse payment) {
        return new UserReservationPaymentResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                payment.paymentKey(),
                payment.totalAmount()
        );
    }

    public static UserReservationPaymentResponse fromPending(final Reservation pending) {
        return new UserReservationPaymentResponse(
                pending.getId(),
                pending.getTheme().getThemeName(),
                pending.getDate(),
                pending.getTime().getStartAt(),
                PENDING,
                null,
                null
        );
    }

    public static UserReservationPaymentResponse from(final WaitingResponse waiting) {
        return new UserReservationPaymentResponse(
                waiting.waitingId(),
                waiting.theme(),
                waiting.date(),
                waiting.startAt(),
                String.format(WAITING_ORDER, waiting.order()),
                "",
                BigDecimal.ZERO
        );
    }
}
