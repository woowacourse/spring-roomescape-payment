package roomescape.payment.dto;

public record PaymentResponse(String paymentKey,
                              int amount,
                              String totalAmount,
                              String type) {

}
