package roomescape.reservation.dto;

import java.math.BigDecimal;

public record PaymentResponse(String status, String paymentKey, String orderId, BigDecimal totalAmount) {
}
