package roomescape.reservation.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class MyReservation {

    private final Reservation reservation;
    private final Long waitingNumber;
    private final String paymentKey;
    private final String orderId;
    private final BigDecimal amount;

    public MyReservation(Reservation reservation,
                         Long waitingNumber,
                         String paymentKey,
                         String orderId,
                         BigDecimal amount) {
        this.reservation = reservation;
        this.waitingNumber = waitingNumber;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
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

    public Long getWaitingNumber() {
        return waitingNumber;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyReservation that = (MyReservation) o;
        return waitingNumber == that.waitingNumber && Objects.equals(reservation, that.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservation, waitingNumber);
    }
}
