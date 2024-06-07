package roomescape.domain.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservation.PaymentInfo;

import java.time.LocalDateTime;

public record PaymentResponse(
        String mId,
        String paymentKey,
        String orderId,
        PaymentStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        LocalDateTime requestedAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        LocalDateTime approvedAt,
        PaymentFailure failure,
        Integer totalAmount
) {
    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(paymentKey, orderId, totalAmount);
    }
}
