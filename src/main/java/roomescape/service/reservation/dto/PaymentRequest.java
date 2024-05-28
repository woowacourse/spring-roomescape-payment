package roomescape.service.reservation.dto;

public record PaymentRequest(String paymentKey, String orderId, Long amount) {

}
