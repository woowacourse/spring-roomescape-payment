package roomescape.service.dto;

public record TossPaymentRequestDto(String orderId, long amount, String paymentKey) {

}
