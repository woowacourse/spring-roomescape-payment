package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationWithPayment {

    private final Long id;
    private final String theme;
    private final LocalDate date;
    private final LocalTime time;
    private final Status status;
    private final String paymentKey;
    private final Long totalAmount;

    public ReservationWithPayment(Long id, String theme, LocalDate date, LocalTime time, Status status, String paymentKey, Long totalAmount) {
        this.id = id;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Status getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }
}
