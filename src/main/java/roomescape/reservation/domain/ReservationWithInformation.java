package roomescape.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.payment.domain.Amount;

public class ReservationWithInformation {

    private final Reservation reservation;
    private final int waitingNumber;
    private final String paymentKey;
    private final Amount amount;

    public ReservationWithInformation(Reservation reservation, int waitingNumber, String paymentKey, BigDecimal amount) {
        this.reservation = reservation;
        this.waitingNumber = waitingNumber;
        this.paymentKey = paymentKey;
        this.amount = new Amount(amount);
    }

    public ReservationWithInformation(Reservation reservation, Long waitingNumber, String paymentKey, BigDecimal amount) {
        this(reservation, waitingNumber.intValue(), paymentKey, amount);
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Long getReservationId() {
        return reservation.getId();
    }

    public String getThemeName() {
        return reservation.getThemeName();
    }

    public LocalDate getReservationDate() {
        return reservation.getDate();
    }

    public LocalTime getStartAt() {
        return reservation.getStartAt();
    }

    public int getWaitingNumber() {
        return waitingNumber;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationWithInformation that = (ReservationWithInformation) o;
        return waitingNumber == that.waitingNumber && Objects.equals(reservation, that.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservation, waitingNumber, paymentKey, amount);
    }
}
