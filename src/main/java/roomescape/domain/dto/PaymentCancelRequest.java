package roomescape.domain.dto;

public record PaymentCancelRequest(String paymentKey, String cancelReason) {
}
