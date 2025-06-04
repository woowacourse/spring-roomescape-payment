package roomescape.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    RESERVED,
    WAITING,
    CANCELED,
    PENDING_PAYMENT,
    PAYMENT_FAILED,
}
