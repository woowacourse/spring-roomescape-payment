package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PaymentHistory extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Reservation reservation;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String paymentKey;
    @Column(nullable = false)
    private String paymentType;

    public PaymentHistory(Long id, Reservation reservation, String orderId, String paymentKey, String paymentType) {
        this.id = id;
        this.reservation = reservation;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
    }

    public PaymentHistory() {

    }
}
