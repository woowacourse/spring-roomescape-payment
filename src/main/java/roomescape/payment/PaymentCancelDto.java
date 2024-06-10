package roomescape.payment;

public record PaymentCancelDto(
        String cancelReason,
        String canceledAt,
        String cancelAmount,
        String cancelStatus
) {
}
