package roomescape.domain.payment;

public record CancelReason(String cancelReason) {

    public static CancelReason empty() {
        return new CancelReason("고객이 취소를 요청했습니다.");
    }
}
