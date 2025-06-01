package roomescape.fixture.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.fixture.entity.ReservationDateFixture;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDateTime;
import roomescape.reservation.repository.ReservationRepository;

@Component
@RequiredArgsConstructor
public class ReservationDbFixture {

    private final MemberDbFixture memberDbFixture;
    private final ThemeDbFixture themeDbFixture;
    private final ReservationTimeDbFixture reservationTimeDbFixture;
    private final ReservationRepository reservationRepository;

    public Reservation reserve() {
        return reservationRepository.save(Reservation.reserve(
                memberDbFixture.유저1_생성(),
                new ReservationDateTime(ReservationDateFixture.예약날짜_내일, reservationTimeDbFixture.열시()),
                themeDbFixture.공포())
        );
    }

    public Reservation waiting() {
        return reservationRepository.save(Reservation.waiting(
                memberDbFixture.유저1_생성(),
                new ReservationDateTime(ReservationDateFixture.예약날짜_내일, reservationTimeDbFixture.열시()),
                themeDbFixture.공포())
        );
    }
}
