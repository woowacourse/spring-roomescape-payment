package roomescape.service.dto.request;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationCreateRequest(
        LocalDate date,
        Long timeId,
        Long themeId,
        Long memberId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {

    public Reservation toReservation(ReservationTime time, Theme theme, Member member) {
        return new Reservation(date, member, time, theme);
    }

    public PaymentCreateRequest toPaymentCreateRequest(Reservation reservation) {
        return new PaymentCreateRequest(paymentKey, orderId, amount, reservation);
    }

    public PaymentConfirmRequest toPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
