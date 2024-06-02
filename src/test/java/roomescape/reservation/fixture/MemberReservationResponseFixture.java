package roomescape.reservation.fixture;

import roomescape.reservation.domain.ReservationWithWaiting;
import roomescape.reservation.dto.MemberReservationResponse;

public class MemberReservationResponseFixture {

    public static final MemberReservationResponse RESERVATION_1_WAITING_1 = new MemberReservationResponse(
            new ReservationWithWaiting(
                    ReservationFixture.SAVED_RESERVATION_1,
                    1
            )
    );

    public static final MemberReservationResponse RESERVATION_2_WAITING_1 = new MemberReservationResponse(
            new ReservationWithWaiting(
                    ReservationFixture.SAVED_RESERVATION_2,
                    1
            )
    );
}
