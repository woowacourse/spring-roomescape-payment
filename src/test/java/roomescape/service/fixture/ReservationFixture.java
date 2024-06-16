package roomescape.service.fixture;

import roomescape.model.Reservation;

import java.time.LocalDate;

public enum ReservationFixture {
    GENERAL(1L, LocalDate.now().plusDays(1));

    private Long id;
    private LocalDate localDate;

    ReservationFixture(final Long id, final LocalDate localDate) {
        this.id = id;
        this.localDate = localDate;
    }

    public Reservation getReservation() {
        return new Reservation(id, localDate,
                ReservationTimeFixture.GENERAL.getReservationTime(),
                ThemeFixture.GENERAL.getTheme(),
                MemberFixture.GENERAL.getMember());
    }
}
