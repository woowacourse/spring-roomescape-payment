package roomescape.application.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.application.payment.dto.PaymentResponse;
import roomescape.domain.payment.Payment;

public record ReservationStatusResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime startAt,
        long waitingCount,
        PaymentResponse payment
) {
    public ReservationStatusResponse(long id, String theme, LocalDate date, LocalTime time, long waitingCount) {
        this(id, theme, date, time, waitingCount, null);
    }

    public ReservationStatusResponse withPayment(Payment payment) {
        return new ReservationStatusResponse(
                id,
                theme,
                date,
                startAt,
                waitingCount,
                new PaymentResponse(payment.getPaymentKey(), payment.getAmount(), payment.getStatus())
        );
    }
}
