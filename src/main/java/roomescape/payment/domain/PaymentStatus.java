package roomescape.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    NOT_PAID("결제 전"),
    PENDING("결제 중"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELED("결제 취소"),
    REFUNDED("환불 완료"),
    ;

    private final String description;
}
