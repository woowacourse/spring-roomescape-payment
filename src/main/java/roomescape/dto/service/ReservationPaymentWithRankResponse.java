package roomescape.dto.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Theme;

public class ReservationPaymentWithRankResponse {

    private Reservation reservation;
    private Payment payment;
    private Long rank;

    public ReservationPaymentWithRankResponse(Reservation reservation, Payment payment, Long rank) {
        this.reservation = reservation;
        this.payment = payment;
        this.rank = rank;
    }

    public long getId() {
        return reservation.getId();
    }

    public LocalDate getDate() {
        return reservation.getDate();
    }

    public LocalTime getTime() {
        return reservation.getTime();
    }

    public Theme getTheme() {
        return reservation.getTheme();
    }

    public String getStatusMessage() {
        return reservation.getStatus().makeStatusMessage(rank);
    }

    public String getPaymentKey() {
        if (this.payment == null) {
            return null;
        }
        return payment.getPaymentKey();
    }

    public BigDecimal getAmount() {
        if (this.payment == null) {
            return null;
        }
        return payment.getAmount();
    }
}
