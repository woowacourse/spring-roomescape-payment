package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import roomescape.reservation.domain.Reservation;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "payment_key")
    private String paymentKey;

    @Column(nullable = false, name = "order_id")
    private String orderId;

    @Column(nullable = false, name = "total_amount")
    private Long totalAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "reservation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, Long totalAmount, Reservation reservation) {
        this(null, paymentKey, orderId, totalAmount, reservation);
    }

    public Payment(Long id, String paymentKey, String orderId, Long totalAmount, Reservation reservation) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
