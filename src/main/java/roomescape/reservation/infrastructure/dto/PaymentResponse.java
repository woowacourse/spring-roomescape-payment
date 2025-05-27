package roomescape.reservation.infrastructure.dto;

public record PaymentResponse(
        String paymentKey,
        Long totalAmount
) {

}
