package roomescape.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    RESERVED("예약"),
    WAITING("대기"),
    CANCELED("취소"),
    PENDING_PAYMENT("결제 대기"),
    PAYMENT_FAILED("결제 실패"),
    ;

    private final String description;
}
