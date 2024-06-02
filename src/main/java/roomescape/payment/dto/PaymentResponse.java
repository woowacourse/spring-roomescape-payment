package roomescape.payment.dto;

public record PaymentResponse(
        String orderName,
        String requestedAt,
        String approvedAt,
        String currency,
        long totalAmount) {
}
