package roomescape.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 중"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소"),
    ;

    private final String description;

    PaymentStatus(final String description) {
        this.description = description;
    }
}
