package roomescape.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    RESERVED("예약"),
    CANCELED("취소"),
    ;

    private final String description;
}
