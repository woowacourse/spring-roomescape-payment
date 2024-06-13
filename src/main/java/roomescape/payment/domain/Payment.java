package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String externalPaymentKey;

    @Column(nullable = false)
    private String externalOrderId;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    protected Payment() {
    }

    public Payment(String externalPaymentId,
                   String externalOrderId, Reservation reservation) {
        this.id = null;
        this.createdAt = null;
        this.externalPaymentKey = externalPaymentId;
        this.externalOrderId = externalOrderId;
        this.reservation = reservation;
    }

    public Long getId() {
        return id;
    }

    public String getExternalPaymentKey() {
        return externalPaymentKey;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Long getAmount() {
        return this.reservation.getTheme().getPrice();
    }
}
