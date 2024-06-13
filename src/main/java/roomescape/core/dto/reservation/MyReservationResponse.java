package roomescape.core.dto.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Theme;
import roomescape.core.domain.Waiting;
import roomescape.core.domain.WaitingWithRank;
import roomescape.core.dto.payment.PaymentConfirmResponse;

public record MyReservationResponse(Long id, String theme, String date, String time, long waitingOrder,
                                    ReservationResponseType status, String paymentKey, Integer amount) {

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        final Waiting waiting = waitingWithRank.waiting();
        final Theme theme = waiting.getTheme();
        final LocalDate date = waiting.getDate();
        final ReservationTime time = waiting.getTime();
        final long waitingOrder = waitingWithRank.rank() + 1;

        return new MyReservationResponse(waiting.getId(), theme.getName(), date.format(DateTimeFormatter.ISO_DATE),
                time.getStartAtString(), waitingOrder, ReservationResponseType.WAITING, null, null);
    }

    public static MyReservationResponse from(final Reservation reservation, final Payment payment) {
        final Theme theme = reservation.getTheme();
        final ReservationTime time = reservation.getReservationTime();

        return new MyReservationResponse(reservation.getId(), theme.getName(), reservation.getDateString(),
                time.getStartAtString(), 0, ReservationResponseType.BOOKED, payment.getPaymentKey(),
                payment.getAmount());
    }

    public static MyReservationResponse from(final Reservation reservation) {
        final Theme theme = reservation.getTheme();
        final ReservationTime time = reservation.getReservationTime();

        return new MyReservationResponse(reservation.getId(), theme.getName(), reservation.getDateString(),
                time.getStartAtString(), 0, ReservationResponseType.PAYMENT_WAITING, null, null);
    }

    public static MyReservationResponse from(final ReservationResponse reservation,
                                             final PaymentConfirmResponse payment) {
        final Long id = reservation.id();
        final String theme = reservation.theme().name();
        final String date = reservation.date();
        final String time = reservation.time().startAt();
        final long waitingOrder = 0;
        final ReservationResponseType status = ReservationResponseType.BOOKED;
        final String paymentKey = payment.paymentKey();
        final Integer amount = payment.totalAmount();

        return new MyReservationResponse(id, theme, date, time, waitingOrder, status, paymentKey, amount);
    }
}
