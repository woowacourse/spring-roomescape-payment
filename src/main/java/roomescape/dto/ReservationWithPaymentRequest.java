package roomescape.dto;

import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import java.time.LocalDate;

public record ReservationWithPaymentRequest(LocalDate date,
                                            long timeId,
                                            long themeId,
                                            String paymentKey,
                                            String orderId,
                                            int amount) {

    public Reservation toReservation(Member requestedMember, ReservationTime requestedTime, Theme requestedTheme) {
        return new Reservation(
                date,
                requestedTime,
                requestedTheme,
                requestedMember,
                paymentKey,
                amount
        );
    }
}
