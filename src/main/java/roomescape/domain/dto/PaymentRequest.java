package roomescape.domain.dto;

public record PaymentRequest(String paymentKey, String orderId, Long amount) {

}
