package roomescape.dto;

public record PaymentResponse(String paymentKey,
                              String orderName,
                              String requestedAt,
                              String approvedAt,
                              String currency,
                              long totalAmount) {
}
