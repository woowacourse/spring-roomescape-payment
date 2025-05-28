package roomescape.payment.domain;

import jakarta.persistence.*;
import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private LocalDateTime paymentDateTime;

    private Long amount;

    private PaymentStatus status;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Reservation reservation;

    public Payment(String orderId, LocalDateTime paymentDateTime, Long amount, PaymentStatus status, Reservation reservation) {
        this.orderId = orderId;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
        this.reservation = reservation;
    }

    public Payment() {
    }

    public void cancel(){
        this.status = PaymentStatus.CANCEL;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
