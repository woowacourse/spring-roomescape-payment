package roomescape.core.dto.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Theme;
import roomescape.core.domain.Waiting;
import roomescape.core.domain.WaitingWithRank;

public record MyReservationResponse(Long id, String theme, String date, String time, String status, String paymentKey,
                                    Integer amount) {

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        final Waiting waiting = waitingWithRank.getWaiting();
        final Theme theme = waiting.getTheme();
        final LocalDate date = waiting.getDate();
        final ReservationTime time = waiting.getTime();
        final String status = (waitingWithRank.getRank() + 1) + "번째 예약 대기";

        return new MyReservationResponse(waiting.getId(), theme.getName(), date.format(DateTimeFormatter.ISO_DATE),
                time.getStartAtString(), status, null, null);
    }

    public static MyReservationResponse from(final Reservation reservation, final Payment payment) {
        final Theme theme = reservation.getTheme();
        final ReservationTime time = reservation.getReservationTime();
        final String status = "예약";

        return new MyReservationResponse(reservation.getId(), theme.getName(), reservation.getDateString(),
                time.getStartAtString(), status, payment.getPaymentKey(), payment.getAmount());
    }

    public static MyReservationResponse from(final Reservation reservation) {
        final Theme theme = reservation.getTheme();
        final ReservationTime time = reservation.getReservationTime();
        final String status = "결제 대기";

        return new MyReservationResponse(reservation.getId(), theme.getName(), reservation.getDateString(),
                time.getStartAtString(), status, null, null);
    }
}
