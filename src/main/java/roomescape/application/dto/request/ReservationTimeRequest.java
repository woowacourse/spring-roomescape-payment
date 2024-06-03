package roomescape.application.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

import roomescape.domain.reservation.detail.ReservationTime;

public record ReservationTimeRequest(
        @NotNull(message = "예약 시작 시간을 입력해주세요.")
        LocalTime startAt
) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
