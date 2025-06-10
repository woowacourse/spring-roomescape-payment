package roomescape.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    RESERVED("예약"),
    WAITING("예약 대기"),
    CANCELED("취소"),
    PENDING("처리 중"),
    ;

    private final String description;
}
