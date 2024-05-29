package roomescape.service.dto;

public record PaymentRequestDto(String orderId, long amount, String paymentKey) {

}
