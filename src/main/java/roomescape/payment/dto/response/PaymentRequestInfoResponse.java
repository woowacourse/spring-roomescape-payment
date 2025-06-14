package roomescape.payment.dto.response;

public record PaymentRequestInfoResponse(String orderId, String orderName, Long amount) {
}
