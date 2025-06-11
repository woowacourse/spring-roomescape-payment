package roomescape.reservation.dto.response;

import java.time.LocalDate;
import roomescape.member.dto.response.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

public record ReservationResponseWithPayment(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String reservedStatus,
        String paymentKey,
        Integer amount
) {
    public static ReservationResponseWithPayment of(Payment payment) {
        Reservation reservation = payment.getReservation();
        return new ReservationResponseWithPayment(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()), ThemeResponse.from(reservation.getTheme()),
                ReservationStatus.RESERVED.getName(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
