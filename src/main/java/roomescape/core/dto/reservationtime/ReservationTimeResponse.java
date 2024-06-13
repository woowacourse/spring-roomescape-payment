package roomescape.core.dto.reservationtime;

import roomescape.core.domain.ReservationTime;

public record ReservationTimeResponse(Long id, String startAt) {
    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        final Long id = reservationTime.getId();
        final String startAt = reservationTime.getStartAtString();

        return new ReservationTimeResponse(id, startAt);
    }
}
