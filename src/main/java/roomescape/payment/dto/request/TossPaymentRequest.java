package roomescape.payment.dto.request;

public record TossPaymentRequest(String paymentKey, String orderId, Integer amount, String paymentType) {
    public TossPaymentRequest {
        if (paymentKey == null) {
            throw new IllegalArgumentException("결제 키는 null 일 수 없습니다.");
        }

        if (orderId == null) {
            throw new IllegalArgumentException("주문 번호는 null 일 수 없습니다.");
        }

        if (amount == null) {
            throw new IllegalArgumentException("가격은 null 일 수 없습니다.");
        }

        if (paymentType == null) {
            throw new IllegalArgumentException("결제 타입은 null 일 수 없습니다.");
        }
    }
}
