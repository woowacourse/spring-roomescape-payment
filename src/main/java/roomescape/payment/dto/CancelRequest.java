package roomescape.payment.dto;

public record CancelRequest(String paymentKey, String cancelReason) {

    public CancelRequest(String paymentKey) {
        this(paymentKey, "예약취소");
    }
}
