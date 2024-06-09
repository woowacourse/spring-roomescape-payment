package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.dto.payment.PaymentRequest;

public record ReservationRequestWithPayment(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        Long memberId,
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType
) {

    public static ReservationRequestWithPayment of(UserReservationRequest request, Long memberId) {
        return new ReservationRequestWithPayment(
                request.date(),
                request.timeId(),
                request.themeId(),
                memberId,
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                request.paymentType()
        );
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(
                date,
                timeId,
                themeId,
                memberId
        );
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(
                paymentKey,
                orderId,
                amount,
                paymentType
        );
    }
}
