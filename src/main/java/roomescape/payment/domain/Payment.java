package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.payment.domain.vo.PaymentStatus;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private Integer amount;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    private Long reservationId;

    protected Payment() {}

    public Payment(Long id, String paymentKey, Integer amount, PaymentStatus status, Long reservationId) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.status = status;
        this.reservationId = reservationId;
    }

    public Payment(String paymentKey, Integer amount, PaymentStatus status, Long reservationId) {
        this(null, paymentKey, amount, status, reservationId);
    }

    public void complete() {
        status = PaymentStatus.COMPLETE;
    }

    public void fail() {
        status = PaymentStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
