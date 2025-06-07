package roomescape.payment.dto;

public record PaymentRequestDto(String paymentKey, String orderId, int totalAmount) {

}
