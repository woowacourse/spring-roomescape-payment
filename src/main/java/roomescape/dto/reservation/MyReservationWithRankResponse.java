package roomescape.dto.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public class MyReservationWithRankResponse {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private Long reservationId;
    private String theme;
    private LocalDate date;
    private String time;
    private String status;
    private Long rank;
    private String paymentKey;
    private Long amount;

    public MyReservationWithRankResponse(final Reservation reservation, final Long rank, final Payment payment) {
        this.reservationId = reservation.getId();
        this.theme = reservation.getThemeName();
        this.date = reservation.getDate();
        this.time = reservation.getTime().getStartAt().format(FORMATTER);
        this.status = reservation.getStatus().value();
        this.rank = rank;
        this.paymentKey = payment.getPaymentKey();
        this.amount = payment.getAmount();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public Long getRank() {
        return rank;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }
}
