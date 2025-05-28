package roomescape.payment.application.dto;

public record PaymentDataRequest(
        String orderId,
        String orderName,
        Long amount
) {
}
