package roomescape.reservation.fixture;

import java.math.BigDecimal;
import roomescape.reservation.domain.MyReservation;
import roomescape.reservation.dto.MyReservationResponse;

public class MemberReservationResponseFixture {

    public static final MyReservationResponse RESERVATION_1_WAITING_1 = new MyReservationResponse(
            new MyReservation(
                    ReservationFixture.SAVED_RESERVATION_1,
                    1L,
                    "paymentKey",
                    "orderId",
                    BigDecimal.valueOf(1000L)
            )
    );

    public static final MyReservationResponse RESERVATION_2_WAITING_1 = new MyReservationResponse(
            new MyReservation(
                    ReservationFixture.SAVED_RESERVATION_2,
                    1L,
                    "paymentKey",
                    "orderId",
                    BigDecimal.valueOf(1000L)
            )
    );
}
