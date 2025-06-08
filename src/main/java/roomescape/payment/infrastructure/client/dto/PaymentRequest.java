package roomescape.payment.infrastructure.client.dto;

public record PaymentRequest(String paymentKey,
                             int amount,
                             String orderId,
                             String paymentType) {

}
