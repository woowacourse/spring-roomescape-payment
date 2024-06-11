package roomescape.service.fixture;

import roomescape.model.ReservationTime;

import java.time.LocalTime;

public enum ReservationTimeFixture {
    GENERAL(1L, LocalTime.parse("10:00"));
    private Long id;
    private LocalTime startAt;

    ReservationTimeFixture(final Long id, final LocalTime startAt) {
        this.id = id;
        this.startAt = startAt;
    }

    public ReservationTime getReservationTime() {
        return new ReservationTime(id, startAt);
    }
}
