package roomescape.dto.reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import roomescape.domain.reservation.Reservation;

public class MyReservationWithRankResponse {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Long reservationId;
    private final String theme;
    private final LocalDate date;
    private final String time;
    private final String status;
    private final Long rank;
    private final String paymentKey;
    private final Long amount;

    public MyReservationWithRankResponse(final Reservation reservation, final Long rank) {
        this.reservationId = reservation.getId();
        this.theme = reservation.getTheme().getName();
        this.date = reservation.getDate();
        this.time = reservation.getTime().getStartAt().format(FORMATTER);
        this.status = reservation.getStatus().value();
        this.rank = rank;
        this.paymentKey = reservation.getPayment().getPaymentKey();
        this.amount = reservation.getPayment().getAmount();
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
