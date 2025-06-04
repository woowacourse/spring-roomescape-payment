package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import roomescape.reservation.domain.Reservation;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column
    private String paymentKey;

    @Column
    private String orderId;

    @Column
    private String type;

    @Column
    private Integer totalAmount;

    @Column
    private String status;

    @Column
    private String requestedAt;


    public Payment(final String paymentKey, final String orderId, final String type,
                   final Integer totalAmount, final String status,
                   final String requestedAt) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.type = type;
        this.totalAmount = totalAmount;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getType() {
        return type;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public Payment() {
    }

}
