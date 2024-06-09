package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import roomescape.payment.infrastructure.PGCompanyPersistConverter;
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

    @Column(nullable = false, name = "company")
    @Convert(converter = PGCompanyPersistConverter.class)
    private PGCompany company;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, Long totalAmount, Reservation reservation, PGCompany company) {
        this(null, paymentKey, orderId, totalAmount, reservation, company);
    }

    public Payment(Long id, String paymentKey, String orderId,
                   Long totalAmount, Reservation reservation, PGCompany company) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.reservation = reservation;
        this.company = company;
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
