package roomescape.presentation.api;

public record PaymentApproveRequest(
        String paymentKey,
        Long amount
) {
}
