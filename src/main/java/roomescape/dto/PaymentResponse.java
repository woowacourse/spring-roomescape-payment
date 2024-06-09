package roomescape.dto;

public record PaymentResponse(String paymentKey,
                              String orderId,
                              long totalAmount) {
}
