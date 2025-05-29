package roomescape.payment.dto;

public record PaymentResponse(String paymentKey,
                              int amount,
                              String orderId,
                              String status) {

}
