package roomescape.core.dto.reservation;

public class MyReservationResponse {
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

    public static MyReservationResponse ofReservationWaiting(final Long reservationId, final String theme,
                                                             final String date, final String time,
                                                             final String status, final Integer rank,
                                                             final String paymentKey, final Long amount) {
        return new MyReservationResponse(reservationId, theme, date, time, waitingRankStatus(status, rank), paymentKey,
                amount);
    }

    public static MyReservationResponse ofReservation(final Long reservationId, final String theme,
                                                      final String date, final String time, final String status,
                                                      final String paymentKey, final Long amount) {
        return new MyReservationResponse(reservationId, theme, date, time, status, paymentKey, amount);
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
