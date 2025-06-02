package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "payment")
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(unique = true, nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    protected Payment() {
    }

    public Long getId() {
        return id;
    }

    private Payment(Long id, String orderId, String paymentKey, Long amount, Reservation reservation) {
        validateAmountNotNegative(amount);
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.reservation = reservation;
    }

    private void validateAmountNotNegative(final Long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("결제 금액은 음수가 될 수 없습니다.");
        }
    }

    public static Payment createWithoutId(String orderId, String paymentKey, Long amount, Reservation reservation) {
        return new Payment(null, orderId, paymentKey, amount, reservation);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
