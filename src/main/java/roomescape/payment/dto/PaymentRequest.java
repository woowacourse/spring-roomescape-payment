package roomescape.payment.dto;

public record PaymentRequest(String paymentKey,
                             int amount,
                             String orderId,
                             String paymentType) {

}
