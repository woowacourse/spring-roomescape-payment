package roomescape.paymenthistory.dto;

public record PaymentResponse(
        String paymentKey,
        long amount
) {
}
