package roomescape.registration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.domain.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime createdAt,
        Long amount) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCreatedAt(),
                payment.getAmount()
        );
    }

    public static PaymentResponse getPaymentResponseForNotPaidReservation() {
        return new PaymentResponse(
                null, null, null
        );
    }
}
