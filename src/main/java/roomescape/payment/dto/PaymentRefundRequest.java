package roomescape.payment.dto;

public record PaymentRefundRequest(String cancelReason) {
    public static final PaymentRefundRequest DEFAULT_REQUEST =
            new PaymentRefundRequest("어드민이 예약을 취소했습니다.");
}
