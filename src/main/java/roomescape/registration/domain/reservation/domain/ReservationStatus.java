package roomescape.registration.domain.reservation.domain;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "예약 상태", description = "예약과 예약 대기를 구분하는 상태를 관리한다.")
public enum ReservationStatus {

    RESERVED("예약"),
    WAITING("번째 예약 대기");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
