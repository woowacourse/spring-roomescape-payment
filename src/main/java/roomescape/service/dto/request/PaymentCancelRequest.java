package roomescape.service.dto.request;

public record PaymentCancelRequest(
        String paymentKey,
        String cancelReason
) {
}
