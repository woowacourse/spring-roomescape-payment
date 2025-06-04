package roomescape.presentation.dto.request;

public record PaymentRequest(
        String orderId,
        Long amount
) {
}
