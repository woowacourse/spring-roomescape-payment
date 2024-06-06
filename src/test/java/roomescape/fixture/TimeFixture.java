package roomescape.fixture;

import roomescape.domain.reservationdetail.ReservationTime;

public enum TimeFixture {
    ONE_PM("13:00"),
    TWO_PM("14:00"),
    THREE_PM("15:00"),
    FOUR_PM("16:00");

    private final String startAt;

    TimeFixture(String startAt) {
        this.startAt = startAt;
    }

    public ReservationTime create() {
        return new ReservationTime(startAt);
    }

    public String getStartAt() {
        return startAt;
    }
}
