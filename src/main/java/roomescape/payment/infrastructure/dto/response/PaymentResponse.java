package roomescape.payment.infrastructure.dto.response;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record PaymentResponse(String paymentKey, String orderId, String type,
                              Integer totalAmount, String status,
                              OffsetDateTime requestedAt) {
    public LocalDateTime getRequestedAt() {
        return requestedAt.toLocalDateTime();
    }
}
