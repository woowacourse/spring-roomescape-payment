package roomescape.core.dto.reservation;

import java.time.format.DateTimeFormatter;
import roomescape.core.domain.Reservation;

public class MyReservationResponse {
    private static final String NONEXISTENT_PAYMENT_KEY = "NOT_PAID";
    private static final long NONEXISTENT_PAYMENT_AMOUNT = 0L;

    private Long reservationId;
    private String theme;
    private String date;
    private String time;
    private String status;
    private String paymentKey;
    private Long amount;

    private MyReservationResponse(final Long reservationId, final String theme, final String date, final String time,
                                  final String status, final String paymentKey, final Long amount) {
        this.reservationId = reservationId;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public static MyReservationResponse ofReservationWaiting(final Reservation reservation,
                                                             final Integer rank,
                                                             final String paymentKey,
                                                             final Long amount) {
        return new MyReservationResponse(reservation.getId(), reservation.getTheme().getName(),
                reservation.getDate().format(DateTimeFormatter.ISO_DATE),
                reservation.getReservationTime().getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                waitingRankStatus(reservation.getStatus().getValue(), rank), paymentKey, amount);
    }

    public static MyReservationResponse ofReservationWaiting(final Reservation reservation,
                                                             final Integer rank) {
        return MyReservationResponse.ofReservationWaiting(reservation, rank, NONEXISTENT_PAYMENT_KEY,
                NONEXISTENT_PAYMENT_AMOUNT);
    }

    public static MyReservationResponse ofReservation(final Reservation reservation,
                                                      final String paymentKey, final Long amount) {
        return new MyReservationResponse(reservation.getId(), reservation.getTheme().getName(),
                reservation.getDate().format(DateTimeFormatter.ISO_DATE),
                reservation.getReservationTime().getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                reservation.getStatus().getValue(), paymentKey, amount);
    }

    public static MyReservationResponse ofReservation(final Reservation reservation) {
        return MyReservationResponse.ofReservation(reservation, NONEXISTENT_PAYMENT_KEY, NONEXISTENT_PAYMENT_AMOUNT);
    }

    private static String waitingRankStatus(final String status, final Integer rank) {
        return rank + "번째 " + status;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getTheme() {
        return theme;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
