package roomescape.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    PENDING("결제 대기"),
    APPROVED("결제 승인"),
    FAILED("결제 실패"),
    CANCELED("결제 취소"),
    EXPIRED("결제 만료"),
    ;

    private final String description;

    public boolean isApproved() {
        return this == APPROVED;
    }
}
