package roomescape.dto.response;

public record PaymentSuccessResponse(
        String paymentKey,
        int totalAmount
) {
}
