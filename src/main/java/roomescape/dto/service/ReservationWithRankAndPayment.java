package roomescape.dto.service;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.Theme;

public class ReservationWithRankAndPayment {

    private Reservation reservation;
    private Long rank;
    private Payment payment;

    public ReservationWithRankAndPayment(Reservation reservation, Long rank, Payment payment) {
        this.reservation = reservation;
        this.rank = rank;
        this.payment = payment;
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

    public Payment getPayment() {
        return payment;
    }

    public String getPaymentKey() {
        if (payment == null) {
            return null;
        }
        return payment.getPaymentKey();
    }

    public Long getPaymentAmount() {
        if (payment == null) {
            return null;
        }
        return payment.getAmount();
    }
}
