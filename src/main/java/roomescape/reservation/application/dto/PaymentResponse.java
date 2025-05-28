package roomescape.reservation.application.dto;

public record PaymentResponse(
        String paymentKey,
        Long totalAmount
) {

}
